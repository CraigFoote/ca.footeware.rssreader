/**
 *
 */
package rssreader.model;

import java.util.Date;
import java.util.Objects;

/**
 * A domain model representing a {@link Feed} article.
 *
 * @author Footeware.ca
 */
public class Article implements Comparable<Article> {
	private String title;
	private String feedName;
	private Date publishDate;
	private String imageUrl;
	private String link;

	/**
	 * Constructor.
	 *
	 * @param title       {@link String}
	 * @param feedName    {@link String}
	 * @param publishDate {@link Date}
	 * @param imageUrl    {@link String}
	 * @param link        {@link String}
	 */
	public Article(String title, String feedName, Date publishDate, String imageUrl, String link) {
		super();
		this.title = title;
		this.feedName = feedName;
		this.publishDate = publishDate;
		this.imageUrl = imageUrl;
		this.link = link;
	}

	@Override
	public int compareTo(Article o) {
		// sort descending, i.e. newest article listed first
		return o.getPublishDate().compareTo(this.getPublishDate());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (getClass() != obj.getClass()))
			return false;
		Article other = (Article) obj;
		return Objects.equals(feedName, other.feedName) && Objects.equals(imageUrl, other.imageUrl)
				&& Objects.equals(link, other.link) && Objects.equals(publishDate, other.publishDate)
				&& Objects.equals(title, other.title);
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

	@Override
	public int hashCode() {
		return Objects.hash(feedName, imageUrl, link, publishDate, title);
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
