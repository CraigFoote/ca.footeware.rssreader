/**
 *
 */
package rssreader.model;

/**
 * @author Footeware.ca
 *
 */
public class Feed {
	private String name;
	private String url;
	private boolean showItems;

	public Feed() {}

	/**
	 * @param name
	 * @param address
	 * @param url
	 * @param showItems
	 */
	public Feed(String name, String url, boolean showItems) {
		this.name = name;
		this.url = url;
		this.showItems = showItems;
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
	 * @return the showItems
	 */
	public boolean isShowItems() {
		return showItems;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param showItems the showItems to set
	 */
	public void setShowItems(boolean showItems) {
		this.showItems = showItems;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return name + "\n\t" + url + "\n\t" + isShowItems() + "\n";
	}

}
