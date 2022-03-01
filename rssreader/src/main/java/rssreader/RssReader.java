/**
 *
 */
package rssreader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndImage;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import rssreader.dialog.FeedDialog;
import rssreader.model.Article;
import rssreader.model.Feed;
import rssreader.widget.ArticleComposite;
import rssreader.widget.FeedComposite;

/**
 * @author Footeware.ca
 */
public class RssReader {

	private static final Preferences prefs = Preferences.userRoot().node(RssReader.class.getName());
	private static final List<Feed> feeds = new ArrayList<>();
	private static SashForm sash;
	private static Composite feedsComposite;
	private static Composite articlesComposite;
	public static FontRegistry fontRegistry;
	public static ImageRegistry imageRegistry;
	private static Shell shell;
	public static Browser browser;
	private static ScrolledComposite articleScroller;

	private static void computerArticleSizes() {
		int width = articleScroller.getClientArea().width;
		articlesComposite.setSize(width - 20, SWT.DEFAULT);
		int height = 0;
		for (Control panel : articlesComposite.getChildren()) {
			height += panel.getSize().y;
		}
		height += articlesComposite.getChildren().length * 12; // margins
		articleScroller.setMinSize(sash.computeSize(width, SWT.DEFAULT).x, height);
	}

	public static void deleteFeed(Feed feed, FeedComposite feedComposite) {
		feeds.remove(feed);
		persistFeeds();
		feedComposite.dispose();
		feedsComposite.setSize(feedsComposite.computeSize(shell.getSize().x - 20, SWT.DEFAULT));
	}

	public static void displayArticles() {
		for (Control articleComposite : articlesComposite.getChildren()) {
			articleComposite.dispose();
		}
		List<Article> articles = new ArrayList<>();
		for (Feed feed : feeds) {
			if (feed.isShowItems()) {
				try {
					URL feedSource = new URL(feed.getUrl());
					SyndFeedInput input = new SyndFeedInput();
					SyndFeed syndFeed = input.build(new XmlReader(feedSource));
					List<SyndEntry> entries = syndFeed.getEntries();
					for (SyndEntry syndEntry : entries) {
						SyndImage image = syndFeed.getImage();
						Article article;
						if (image != null) {
							article = new Article(syndEntry.getTitle(), syndFeed.getTitle(),
									syndEntry.getPublishedDate(), syndFeed.getImage().getUrl(), syndEntry.getLink());
						} else {
							article = new Article(syndEntry.getTitle(), syndFeed.getTitle(),
									syndEntry.getPublishedDate(), null, syndEntry.getLink());
						}
						articles.add(article);
					}
				} catch (IOException | IllegalArgumentException | NullPointerException | FeedException e) {
					MessageDialog.openError(shell, "Error",
							"An error occurred showing articles for '" + feed.getName() + "':\n\n" + e.getMessage());
				}
			}
		}
		articles.sort((o1, o2) -> -(o1.getPublishDate().compareTo(o2.getPublishDate())));
		for (Article article : articles) {
			ArticleComposite articleComposite = new ArticleComposite(articlesComposite, SWT.BORDER, article);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(articleComposite);
		}
		computerArticleSizes();
	}

	protected static void displayFeeds() {
		for (Control panel : feedsComposite.getChildren()) {
			panel.dispose();
		}
		for (Feed feed : feeds) {
			FeedComposite feedComposite = new FeedComposite(feedsComposite, SWT.BORDER, feed);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(feedComposite);
			feedsComposite.setSize(feedsComposite.computeSize(shell.getSize().x - 20, SWT.DEFAULT));
		}
	}

	private static void initFontRegistry(Display display) {
		fontRegistry = new FontRegistry(display);
		fontRegistry.put("bold", new FontData[] { new FontData("Arial", 12, SWT.BOLD) });
		fontRegistry.put("italic", new FontData[] { new FontData("Arial", 12, SWT.ITALIC) });
	}

	private static void initImageRegistry(Display display) {
		imageRegistry = new ImageRegistry(display);
		Image addImage = new Image(display, RssReader.class.getResourceAsStream("/images/add.png"));
		imageRegistry.put("add", addImage);
		Image editImage = new Image(display, RssReader.class.getResourceAsStream("/images/edit.png"));
		imageRegistry.put("edit", editImage);
		Image deleteImage = new Image(display, RssReader.class.getResourceAsStream("/images/delete.png"));
		imageRegistry.put("delete", deleteImage);
		Image rssImage = new Image(display, RssReader.class.getResourceAsStream("/images/rss.png"));
		imageRegistry.put("rss", rssImage);
	}

	private static void loadFeeds() {
		Gson gson = new Gson();
		String json = prefs.get("feeds", "");
		Type feedList = TypeToken.getParameterized(ArrayList.class, Feed.class).getType();
		feeds.addAll(gson.fromJson(json, feedList));
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Display display = new Display();
		shell = new Shell(display);
		shell.setText("RSS Reader");
		shell.setSize(1500, 850);
		shell.setLayout(new GridLayout(1, true));

		initFontRegistry(display);
		initImageRegistry(display);

		shell.setImage(imageRegistry.get("rss"));

		ToolBar bar = new ToolBar(shell, SWT.BORDER);
		bar.setLayoutData(new GridData(SWT.END, SWT.TOP, true, false));
		ToolItem addFeedToolItem = new ToolItem(bar, SWT.PUSH);
		addFeedToolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FeedDialog dialog = new FeedDialog(shell);
				if (dialog.open() == Window.OK) {
					String name = dialog.getName();
					String url = dialog.getUrl();
					Feed feed = new Feed(name, url, false);
					feeds.add(feed);
					displayFeeds();
					persistFeeds();
				}
			}
		});

		addFeedToolItem.setImage(imageRegistry.get("add"));
		addFeedToolItem.setToolTipText("Add New Feed");
		TabFolder tabFolder = new TabFolder(shell, SWT.BORDER);
		tabFolder.setLayout(new FillLayout());
		tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		TabItem feedsTabItem = new TabItem(tabFolder, SWT.NONE);
		feedsTabItem.setText("Feeds");
		ScrolledComposite feedsScroller = new ScrolledComposite(tabFolder, SWT.H_SCROLL | SWT.V_SCROLL);
		feedsScroller.setLayout(new FillLayout());
		feedsTabItem.setControl(feedsScroller);

		feedsComposite = new Composite(feedsScroller, SWT.NONE);
		feedsScroller.setContent(feedsComposite);
		feedsComposite.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridLayoutFactory.fillDefaults().numColumns(1).equalWidth(true).margins(10, 10).applyTo(feedsComposite);

		TabItem articlesTabItem = new TabItem(tabFolder, SWT.NONE);
		articlesTabItem.setText("Articles");
		sash = new SashForm(tabFolder, SWT.BORDER | SWT.HORIZONTAL);
		articlesTabItem.setControl(sash);

		articleScroller = new ScrolledComposite(sash, SWT.V_SCROLL);
		articleScroller.setLayout(new FillLayout());
		articleScroller.setExpandVertical(true);
		articlesComposite = new Composite(articleScroller, SWT.NONE);
		articleScroller.addListener(SWT.Resize, e -> computerArticleSizes());
		articleScroller.setContent(articlesComposite);
		articlesComposite.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridLayoutFactory.fillDefaults().numColumns(1).margins(10, 10).applyTo(articlesComposite);

		browser = new Browser(sash, SWT.BORDER);

		loadFeeds();
		displayFeeds();
		displayArticles();

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}

	public static void persistFeeds() {
		Gson gson = new Gson();
		String json = gson.toJson(feeds);
		prefs.put("feeds", json);
	}

	public static void updateFeedDisplay(FeedComposite feedComposite) {
		feedComposite.update();
		feedComposite.layout(true);
	}

}
