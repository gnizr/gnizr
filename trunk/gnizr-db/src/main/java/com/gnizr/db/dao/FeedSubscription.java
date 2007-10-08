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

	public FeedSubscription() {
		this(null, true);
	}

	public FeedSubscription(int id) {
		this(id, null, null, null, null, true);
	}

	public FeedSubscription(Bookmark bmark, boolean autoImport) {
		this(-1, bmark, null, null, null, autoImport);
	}

	public FeedSubscription(FeedSubscription copyObj) {
		this(copyObj.id, copyObj.bookmark, copyObj.lastSync, copyObj.pubDate, copyObj.matchText,
				copyObj.autoImport);
	}

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

	public Bookmark getBookmark() {
		return bookmark;
	}

	public int getId() {
		return this.id;
	}

	public Date getLastSync() {
		return lastSync;
	}

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

	public boolean isAutoImport() {
		return autoImport;
	}

	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}

	public void setMatchText(String matchText) {
		this.matchText = matchText;
	}

	public List<String> getImportFolders() {
		return importFolders;
	}

	public void setImportFolders(List<String> importFolders) {
		this.importFolders = importFolders;
	}

	public Date getPubDate() {
		return pubDate;
	}

	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}

}
