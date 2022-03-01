module rssreader {
    exports rssreader;
	requires org.eclipse.swt.gtk.linux.x86_64;
	requires jface;
	requires com.google.gson;
	requires java.prefs;
	requires com.rometools.rome;
	requires org.jdom2;
	opens rssreader.model to com.google.gson;
}