/**
 * 
 */
package rssreader.model;

/**
 * @author Footeware.ca
 *
 */
public class Article {
	private String title;
	private String feedName;
	private String publishDate;
	private String imageUrl;
	private String link;
	
	/**
	 * @param title
	 * @param feedName
	 * @param publishDate
	 * @param imageUrl
	 * @param link
	 */
	public Article(String title, String feedName, String publishDate, String imageUrl, String link) {
		super();
		this.title = title;
		this.feedName = feedName;
		this.publishDate = publishDate;
		this.imageUrl = imageUrl;
		this.link = link;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the feedName
	 */
	public String getFeedName() {
		return feedName;
	}

	/**
	 * @param feedName the feedName to set
	 */
	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}

	/**
	 * @return the publishDate
	 */
	public String getPublishDate() {
		return publishDate;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}
}
