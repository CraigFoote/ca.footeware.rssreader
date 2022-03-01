module rssreader {
	requires org.eclipse.swt.gtk.linux.x86_64;
	requires jface;
	requires com.google.gson;
	requires com.rometools.rome;
	requires org.jdom2;
	requires java.prefs;

	opens rssreader.model to com.google.gson;

	exports rssreader;
	exports rssreader.dialog;
	exports rssreader.widget;
	exports rssreader.model;
}