/**
 *
 */
package rssreader.model;

import java.util.Date;

/**
 * @author Footeware.ca
 *
 */
public class Article {
	private String title;
	private String feedName;
	private Date publishDate;
	private String imageUrl;
	private String link;

	/**
	 * @param title
	 * @param feedName
	 * @param publishDate
	 * @param imageUrl
	 * @param link
	 */
	public Article(String title, String feedName, Date publishDate, String imageUrl, String link) {
		super();
		this.title = title;
		this.feedName = feedName;
		this.publishDate = publishDate;
		this.imageUrl = imageUrl;
		this.link = link;
	}

	/**
	 * @return the feedName
	 */
	public String getFeedName() {
		return feedName;
	}

	/**
	 * @return the imageUrl
	 */
	public String getImageUrl() {
		return imageUrl;
	}

	/**
	 * @return the link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @return the publishDate
	 */
	public Date getPublishDate() {
		return publishDate;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param feedName the feedName to set
	 */
	public void setFeedName(String feedName) {
		this.feedName = feedName;
	}

	/**
	 * @param imageUrl the imageUrl to set
	 */
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	/**
	 * @param link the link to set
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * @param publishDate the publishDate to set
	 */
	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
}
