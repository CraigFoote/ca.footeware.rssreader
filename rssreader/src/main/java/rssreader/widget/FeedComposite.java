/**
 *
 */
package rssreader.widget;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import rssreader.RssReader;
import rssreader.dialog.FeedDialog;
import rssreader.model.Feed;

/**
 * A panel displaying the details of a {@link Feed}.
 *
 * @author Footeware.ca
 */
public class FeedComposite extends Composite {

	/**
	 * Constructor.
	 *
	 * @param parent {@link Composite}
	 * @param style  int
	 * @param feed   {@link Feed}
	 */
	public FeedComposite(Composite parent, int style, RssReader reader, final Feed feed) {
		super(parent, style);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(10, 10).applyTo(this);

		Composite labelsComposite = new Composite(this, SWT.NONE);
		labelsComposite.setLayout(new RowLayout(SWT.VERTICAL));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(labelsComposite);

		Label nameLabel = new Label(labelsComposite, SWT.NONE);
		nameLabel.setFont(reader.getFontRegistry().get("bold"));
		nameLabel.setText(feed.getName());

		Label urlLabel = new Label(labelsComposite, SWT.NONE);
		urlLabel.setText(feed.getUrl());

		Composite buttonsComposite = new Composite(this, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(10, 10).spacing(20, 10).numColumns(3).applyTo(buttonsComposite);
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(buttonsComposite);

		Button showArticlesButton = new Button(buttonsComposite, SWT.CHECK | SWT.WRAP);
		showArticlesButton.setSelection(feed.isShowItems());
		showArticlesButton.setText("Show Articles");
		showArticlesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				feed.setShowItems(((Button) e.getSource()).getSelection());
				reader.persistFeeds();
				reader.displayArticles();
			}
		});
		GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).applyTo(showArticlesButton);

		Button editButton = new Button(buttonsComposite, SWT.PUSH);
		editButton.setImage(reader.getImageRegistry().get("edit"));
		editButton.setToolTipText("Edit");
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FeedDialog dialog = new FeedDialog(FeedComposite.this.getShell(), feed);
				if (dialog.open() == Window.OK) {
					String name = dialog.getName();
					String url = dialog.getUrl();
					feed.setName(name);
					feed.setUrl(url);
					reader.persistFeeds();
					nameLabel.setText(name);
					urlLabel.setText(url);
					reader.updateFeedDisplay(FeedComposite.this);
				}
			}
		});

		Button deleteButton = new Button(buttonsComposite, SWT.PUSH);
		deleteButton.setImage(reader.getImageRegistry().get("delete"));
		deleteButton.setToolTipText("Delete");
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean result = MessageDialog.openConfirm(FeedComposite.this.getShell(), "Confirm",
						"Are you sure you want to delete '" + feed.getName() + "'?");
				if (result) {
					reader.deleteFeed(feed, FeedComposite.this);
				}
			}
		});
	}

}
