package rssreader.model;

import java.util.Date;

/**
 * A domain model representing a {@link Feed} article.
 *
 * @author Footeware.ca
 * @param title       {@link String}
 * @param feedName    {@link String}
 * @param publishDate {@link Date}
 * @param imageUrl    {@link String}
 * @param link        {@link String}
 */
public record Article(String title, String feedName, Date publishDate, String imageUrl, String link)
		implements Comparable<Article> {

	@Override
	public int compareTo(Article o) {
		// sort descending, i.e. newest article listed first
		return o.publishDate().compareTo(this.publishDate());
	}
}
