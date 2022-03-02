/**
 *
 */
package rssreader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
 * Reads RSS news feeds and displays their articles.
 *
 * @author Footeware.ca
 */
public class RssReader {

	/**
	 * The program entry point.
	 *
	 * @param args {@link String} array
	 */
	public static void main(String[] args) {
		RssReader reader = new RssReader();
		reader.open();
	}

	private final Preferences prefs = Preferences.userRoot().node(RssReader.class.getName());
	private final List<Feed> feeds = new ArrayList<>();
	private SashForm sash;
	private Composite feedsComposite;
	private Composite articlesComposite;
	private FontRegistry fontRegistry;
	private ImageRegistry imageRegistry;
	private Shell shell;
	private Browser browser;
	private ScrolledComposite articleScroller;

	/**
	 * Compute the sizes the articles' panes should be to wrap labels keeping text
	 * visible and set the scrollbar height to be large enough to show all articles.
	 */
	private void computeArticleSizes() {
		int width = articleScroller.getClientArea().width;
		articlesComposite.setSize(width - 20, SWT.DEFAULT);
		int height = 0;
		for (Control panel : articlesComposite.getChildren()) {
			height += panel.getSize().y;
		}
		height += articlesComposite.getChildren().length * 12; // margins
		articleScroller.setMinSize(sash.computeSize(width, SWT.DEFAULT).x, height);
	}

	/**
	 * Delete a feed, remove it from Feeds tab and persist the feeds list.
	 *
	 * @param feed          {@link Feed}
	 * @param feedComposite {@link FeedComposite}
	 */
	public void deleteFeed(Feed feed, FeedComposite feedComposite) {
		feeds.remove(feed);
		persistFeeds();
		feedComposite.dispose();
		feedsComposite.setSize(feedsComposite.computeSize(shell.getSize().x - 20, SWT.DEFAULT));
	}

	/**
	 * Display the list of articles from all enabled feeds.
	 */
	public void displayArticles() {
		for (Control articleComposite : articlesComposite.getChildren()) {
			articleComposite.dispose();
		}
		getArticlesTask().start();
	}

	/**
	 * Display the list of all feeds in the Feeds tab.
	 */
	protected void displayFeeds() {
		for (Control panel : feedsComposite.getChildren()) {
			panel.dispose();
		}
		for (Feed feed : feeds) {
			FeedComposite feedComposite = new FeedComposite(feedsComposite, SWT.BORDER, this, feed);
			GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(feedComposite);
			feedsComposite.setSize(feedsComposite.computeSize(shell.getSize().x - 20, SWT.DEFAULT));
		}
	}

	/**
	 * Get the list of articles for provided feed.
	 *
	 * @param feed {@link Feed}
	 * @return {@link List} of {@link Article}
	 */
	private List<Article> fetchArticles(Feed feed) {
		List<Article> articles = new ArrayList<>();
		try {
			URL feedSource = new URL(feed.getUrl());
			SyndFeedInput input = new SyndFeedInput();
			SyndFeed syndFeed = input.build(new XmlReader(feedSource));
			List<SyndEntry> entries = syndFeed.getEntries();
			for (SyndEntry syndEntry : entries) {
				SyndImage image = syndFeed.getImage();
				Article article;
				if (image != null) {
					article = new Article(syndEntry.getTitle(), syndFeed.getTitle(), syndEntry.getPublishedDate(),
							syndFeed.getImage().getUrl(), syndEntry.getLink());
				} else {
					article = new Article(syndEntry.getTitle(), syndFeed.getTitle(), syndEntry.getPublishedDate(), null,
							syndEntry.getLink());
				}
				articles.add(article);
			}
		} catch (IOException | IllegalArgumentException | NullPointerException | FeedException e) {
			// switch to UI thread to display message
			Display.getDefault().asyncExec(() -> MessageDialog.openError(shell, "Error",
					"An error occurred showing articles for '" + feed.getName() + "':\n\n" + e.getMessage()));
		}
		return articles;
	}

	/**
	 * Gets feed articles on non-ui thread then updates UI.
	 *
	 * @return {@link Thread}
	 */
	public Thread getArticlesTask() {
		return new Thread() {
			@Override
			public void run() {
				List<Article> articles = new ArrayList<>();
				for (Feed feed : feeds) {
					if (feed.isShowItems()) {
						List<Article> fetchedArticles = fetchArticles(feed);
						articles.addAll(fetchedArticles);
					}
				}
				Collections.sort(articles);
				// update UI
				Display.getDefault().asyncExec(() -> {
					for (Article article : articles) {
						ArticleComposite articleComposite = new ArticleComposite(articlesComposite, SWT.BORDER,
								RssReader.this, article);
						GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false)
								.applyTo(articleComposite);
					}
					computeArticleSizes();
				});
			}
		};
	}

	/**
	 * @return the browser
	 */
	public Browser getBrowser() {
		return browser;
	}

	/**
	 * @return the fontRegistry
	 */
	public FontRegistry getFontRegistry() {
		return fontRegistry;
	}

	/**
	 * @return the imageRegistry
	 */
	public ImageRegistry getImageRegistry() {
		return imageRegistry;
	}

	/**
	 * Initialize the font registry with commonly used fonts. The registry will
	 * handle disposal of the fonts.
	 *
	 * @param display {@link Display}
	 */
	private void initFontRegistry(Display display) {
		fontRegistry = new FontRegistry(display);
		fontRegistry.put("bold", new FontData[] { new FontData("Arial", 12, SWT.BOLD) });
		fontRegistry.put("italic", new FontData[] { new FontData("Arial", 12, SWT.ITALIC) });
	}

	/**
	 * Initialize the image registry with commonly used images. The registry will
	 * handle disposal of the images.
	 *
	 * @param display {@link Display}
	 */
	private void initImageRegistry(Display display) {
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

	/**
	 * Load the feeds from persisted preferences.
	 */
	@SuppressWarnings("unchecked")
	private void loadFeeds() {
		Gson gson = new Gson();
		String json = prefs.get("feeds", "");
		Type feedListType = TypeToken.getParameterized(ArrayList.class, Feed.class).getType();
		Object fromJson = gson.fromJson(json, feedListType);
		if (fromJson != null) {
			feeds.addAll((Collection<? extends Feed>) fromJson);
		}
	}

	private void open() {
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
					Feed feed = new Feed(dialog.getName(), dialog.getUrl(), false);
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
		feedsScroller.addListener(SWT.Resize, e -> computeFeedSizes());
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
		articleScroller.addListener(SWT.Resize, e -> computeArticleSizes());
		articleScroller.setContent(articlesComposite);
		articlesComposite.setBackground(display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		GridLayoutFactory.fillDefaults().numColumns(1).margins(10, 10).applyTo(articlesComposite);

		browser = new Browser(sash, SWT.BORDER | SWT.WEBKIT);
		browser.setJavascriptEnabled(true);

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

	private void computeFeedSizes() {
		feedsComposite.setSize(feedsComposite.computeSize(shell.getSize().x - 20, SWT.DEFAULT));
	}

	/**
	 * Persist the list of feeds in local preferences.
	 */
	public void persistFeeds() {
		Gson gson = new Gson();
		String json = gson.toJson(feeds);
		prefs.put("feeds", json);
	}

	/**
	 * Update the provided composite to reflect changes in a feed's name or URL.
	 *
	 * @param feedComposite {@link FeedComposite}
	 */
	public void updateFeedDisplay(FeedComposite feedComposite) {
		feedComposite.update();
		feedComposite.layout(true);
	}

}
