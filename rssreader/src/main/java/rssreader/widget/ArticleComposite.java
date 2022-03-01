/**
 *
 */
package rssreader.widget;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

import rssreader.RssReader;
import rssreader.model.Article;
import rssreader.model.Feed;

/**
 * A panel displaying the details of a {@link Feed} article.
 *
 * @author Footeware.ca
 */
public class ArticleComposite extends Composite {

	/**
	 * Listens for mouse enetering or exiting a {@link Control} and modifies its
	 * colors.
	 *
	 * @author Footeware.ca
	 *
	 */
	class HoverListener extends MouseTrackAdapter {
		@Override
		public void mouseEnter(MouseEvent e) {
			ArticleComposite.this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_SELECTION));
		}

		@Override
		public void mouseExit(MouseEvent e) {
			ArticleComposite.this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		}
	}

	/**
	 * Constructor.
	 *
	 * @param parent  {@link Composite}
	 * @param style   int
	 * @param article {@link Article}
	 */
	public ArticleComposite(Composite parent, int style, Article article) {
		super(parent, style);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(this);

		this.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		HoverListener hoverListener = new HoverListener();
		MouseListener clickListener = new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				RssReader.browser.setUrl(article.getLink());
			}
		};

		this.addMouseTrackListener(hoverListener);
		this.addMouseListener(clickListener);

		Image image = RssReader.imageRegistry.get(article.getFeedName());
		if (image == null) {
			try {
				URL url = new URL(article.getImageUrl());
				InputStream inStream = url.openStream();
				image = new Image(Display.getCurrent(), inStream);
				RssReader.imageRegistry.put(article.getFeedName(), image);
			} catch (IOException e1) {
				image = RssReader.imageRegistry.get("rss");
			}
		}
		Label imageLabel = new Label(this, SWT.NONE);
		imageLabel.setImage(image);
		imageLabel.addMouseTrackListener(hoverListener);
		imageLabel.addMouseListener(clickListener);
		GridDataFactory.fillDefaults().span(1, 3).applyTo(imageLabel);

		Label titleLabel = new Label(this, SWT.WRAP);
		titleLabel.setText(article.getTitle());
		titleLabel.setFont(RssReader.fontRegistry.get("bold"));
		titleLabel.addMouseTrackListener(hoverListener);
		titleLabel.addMouseListener(clickListener);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(titleLabel);

		Label feedLabel = new Label(this, SWT.WRAP);
		feedLabel.setText(article.getFeedName());
		feedLabel.addMouseTrackListener(hoverListener);
		feedLabel.addMouseListener(clickListener);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(feedLabel);

		Label publishedLabel = new Label(this, SWT.WRAP);
		publishedLabel.setText(article.getPublishDate().toString());
		publishedLabel.setFont(RssReader.fontRegistry.get("italic"));
		publishedLabel.addMouseTrackListener(hoverListener);
		publishedLabel.addMouseListener(clickListener);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(publishedLabel);
	}
}
