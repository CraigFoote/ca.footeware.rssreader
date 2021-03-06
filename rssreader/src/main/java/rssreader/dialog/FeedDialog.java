package rssreader.dialog;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import rssreader.model.Feed;

/**
 * A dialog to enter or edit a {@link Feed} description.
 *
 * @author Footeware.ca
 */
public class FeedDialog extends Dialog {
	private String name = "";
	private String url = "";
	private Feed feed;

	/**
	 * Constructor.
	 *
	 * @param shell {@link Shell}
	 */
	public FeedDialog(Shell shell) {
		super(shell);
	}

	/**
	 * Constructor.
	 *
	 * @param shell {@link Shell}
	 * @param feed  {@link Feed}
	 */
	public FeedDialog(Shell shell, Feed feed) {
		this(shell);
		this.feed = feed;
	}

	/**
	 * Check whether the OK button should be enabled.
	 */
	private void checkEnableOkButton() {
		boolean haveName = !name.isEmpty();
		boolean haveUrl = !url.isEmpty() && isUrl(url);
		getButton(IDialogConstants.OK_ID).setEnabled(haveName && haveUrl);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add New Feed");
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		Button okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).margins(10, 10).applyTo(container);

		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setText("Name:");

		Text nameText = new Text(container, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (feed != null) {
			nameText.setText(feed.getName());
			this.name = feed.getName();
		}
		nameText.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			name = textWidget.getText().trim();
			checkEnableOkButton();
		});

		Label urlLabel = new Label(container, SWT.NONE);
		urlLabel.setText("Address:");

		Text urlText = new Text(container, SWT.BORDER);
		urlText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		if (feed != null) {
			urlText.setText(feed.getUrl());
			this.url = feed.getUrl();
		}
		urlText.addModifyListener(e -> {
			Text textWidget = (Text) e.getSource();
			url = textWidget.getText().trim();
			checkEnableOkButton();
		});
		container.pack();

		return container;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Tests whether the provided {@link String} can be used to construct an
	 * {@link URL}.
	 *
	 * @param string {@link String}
	 * @return boolean true if string can be used to construct an URL
	 */
	private boolean isUrl(String string) {
		try {
			URL theUrl = new URL(string);
			theUrl.toURI();
			return true;
		} catch (MalformedURLException | URISyntaxException e) {
			return false;
		}
	}

}