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
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the showItems
	 */
	public boolean isShowItems() {
		return showItems;
	}

	/**
	 * @param showItems the showItems to set
	 */
	public void setShowItems(boolean showItems) {
		this.showItems = showItems;
	}

	@Override
	public String toString() {
		return name + "\n\t" + url + "\n\t" + isShowItems() + "\n";
	}

}
