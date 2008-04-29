/*
 * gnizr is a trademark of Image Matters LLC in the United States.
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either expressed or implied. See the License
 * for the specific language governing rights and limitations under the License.
 * 
 * The Initial Contributor of the Original Code is Image Matters LLC.
 * Portions created by the Initial Contributor are Copyright (C) 2007
 * Image Matters LLC. All Rights Reserved.
 */
package com.gnizr.db.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>This class represents a <code>Bookmark</code> that is a valid RSS feed. 
 * It defines the properties used by gnizr RSS robot to support automatic 
 * feed imports.</p> 
 * <p>Before creating a valid instance of this class, a <code>Bookmark</code>
 * object of a RSS feed must be created. The ID of this class doesn't 
 * share the ID of the <code>Bookmark</code> that it wraps.
 * </p>
 * <p>
 * All <code>FeedSubscription</code> have an unique ID, which is usually assigned by the database system
 * when the DB record is created for the first time. 
 * </p>
 * @author Harry Chen
 *
 */
public class FeedSubscription implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5917783018394456833L;

	private boolean autoImport;

	private Bookmark bookmark;

	private int id;

	private Date lastSync;

	private Date pubDate;
	
	private String matchText;
	
	private List<String> importFolders;

	/**
	 * Creates a new instance of this class.
	 */
	public FeedSubscription() {
		this(null, true);
	}

	/**
	 * Creates a new instance of this class with a defined ID.
	 * 
	 * @param id ID of this feed subscription
	 */
	public FeedSubscription(int id) {
		this(id, null, null, null, null, true);
	}

	/**
	 * Creates a new instance of this class with a defined 
	 * <code>Bookmark</code> and flags the automatic import value.
	 * @param bmark the bookmark that is a valid RSS feed.
	 * @param autoImport <code>true</code> if the auto import is to be enabled.
	 */
	public FeedSubscription(Bookmark bmark, boolean autoImport) {
		this(-1, bmark, null, null, null, autoImport);
	}

	/**
	 * Copy constructor used for duplicating the properties of 
	 * <code>copyObj</code> in this object.
	 * @param copyObj an object to copy from.
	 */
	public FeedSubscription(FeedSubscription copyObj) {
		this(copyObj.id, copyObj.bookmark, copyObj.lastSync, copyObj.pubDate, copyObj.matchText,
				copyObj.autoImport);
	}

	/**
	 * Creates a new instance of this class with defined object properties.
	 * 
	 * @param id the ID of this feed subscription
	 * @param bmark the bookmark that describes a valid RSS feed
	 * @param lastSync the last know sync date/time of this RSS feed. Use <code>null</code>
	 * if the date/time is unknown.
	 * @param pubDate the publish date/time of this RSS feed. Use <code>null</code>
	 * if the date/time is unknown.
	 * @param matchText defined a specific text string to match during the feed import. 
	 * (NOTE: this property is not used in the RSS robot implementation, as of 2.3). 
	 * @param autoImport <code>true</code> to enable auto import.
	 */
	public FeedSubscription(int id, Bookmark bmark, Date lastSync, Date pubDate,
			String matchText, boolean autoImport) {
		this.id = id;
		if (bmark != null) {
			this.bookmark = new Bookmark(bmark);
		} else {
			this.bookmark = null;
		}
		if (lastSync != null) {
			this.lastSync = (Date) lastSync.clone();
		}
		if(pubDate != null){
			this.pubDate = (Date)pubDate.clone();
		}
		this.matchText = matchText;
		this.autoImport = autoImport;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FeedSubscription other = (FeedSubscription) obj;
		if (autoImport != other.autoImport)
			return false;
		if (bookmark == null) {
			if (other.bookmark != null)
				return false;
		} else if (!bookmark.equals(other.bookmark))
			return false;
		if (id != other.id)
			return false;
		if (importFolders == null) {
			if (other.importFolders != null)
				return false;
		} else if (!importFolders.equals(other.importFolders))
			return false;
		if (lastSync == null) {
			if (other.lastSync != null)
				return false;
		} else if (!lastSync.equals(other.lastSync))
			return false;
		if (matchText == null) {
			if (other.matchText != null)
				return false;
		} else if (!matchText.equals(other.matchText))
			return false;
		if (pubDate == null) {
			if (other.pubDate != null)
				return false;
		} else if (!pubDate.equals(other.pubDate))
			return false;
		return true;
	}

	/**
	 * Returns the bookmark that describes the RSS feed.
	 * @return an instantiated bookmark. Return <code>null</code> if 
	 * this subscription object has not yet been configured with a bookmark. 
	 */
	public Bookmark getBookmark() {
		return bookmark;
	}

	/**
	 * Returns the ID of this feed subscription.
	 * @return a positive integer if the ID is defined. 0 or negative number if 
	 * the ID is not defined.
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Returns the date/time when the RSS item is read.
	 * @return last known sync date/time. Returns <code>null</code> if the value is unknown.
	 */
	public Date getLastSync() {
		return lastSync;
	}

	/**
	 * Returns the text used for auto import filtering.
	 * @return 
	 */
	public String getMatchText() {
		return matchText;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + (autoImport ? 1231 : 1237);
		result = PRIME * result + ((bookmark == null) ? 0 : bookmark.hashCode());
		result = PRIME * result + id;
		result = PRIME * result + ((importFolders == null) ? 0 : importFolders.hashCode());
		result = PRIME * result + ((lastSync == null) ? 0 : lastSync.hashCode());
		result = PRIME * result + ((matchText == null) ? 0 : matchText.hashCode());
		result = PRIME * result + ((pubDate == null) ? 0 : pubDate.hashCode());
		return result;
	}

	/**
	 * Returns the flag value that determines whether or not
	 * the RSS robot should be enabled to automatically crawl and 
	 * import bookmarks from this feed subscription.
	 * 
	 * @return <code>true</code> if the auto import feature is enabled. Returns <code>false</code>, otherwise.
	 */
	public boolean isAutoImport() {
		return autoImport;
	}

	/**
	 * Sets the flag value that determines whether or not 
	 * the RSS robot should be enabled to automatically crawl
	 * and import bookmarks from this feed subscription.
	 * @param autoImport <code>true</code> if the auto import feature is to be enabled.
	 */
	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
	}

	/**
	 * Sets the bookmark that represents the RSS feed. The owner of
	 * bookmark should be the same user as the user who wants to
	 * subscribe to this feed. 
	 * 
	 * @param bookmark an instantiated <code>bookmark</code>.
	 */
	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	/**
	 * Sets the ID of this feed subscription.
	 * @param id feed Id.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Set the last synd date/time
	 * 
	 * @param lastSync 
	 */
	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}

	/**
	 * Sets the text string used for filtering during
	 * the import.
	 * 
	 * @param matchText a text string.
	 */
	public void setMatchText(String matchText) {
		this.matchText = matchText;
	}

	/**
	 * Gets the list of folder name (string) that 
	 * will be used for saving imported bookmarks. 
	 * 
	 * @return a list of folder names
	 */
	public List<String> getImportFolders() {
		return importFolders;
	}

	/**
	 * Sets the list of folder name (string) that 
	 * will be used for saving imported bookmarks.
	 * 
	 * @param importFolders a list of folder names
	 */
	public void setImportFolders(List<String> importFolders) {
		this.importFolders = importFolders;
	}

	/**
	 * Returns the publish date/time of this RSS feed.
	 * @return the date/time of when this RSS feed is published.
	 */
	public Date getPubDate() {
		return pubDate;
	}

	/**
	 * Sets the publish date/time of this RSS feed.
	 * 
	 * @param pubDate the date/time of when this RSS feed is published.
	 */
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

}
