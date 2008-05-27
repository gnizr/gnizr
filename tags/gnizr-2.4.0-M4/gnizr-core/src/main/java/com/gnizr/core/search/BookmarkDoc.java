package com.gnizr.core.search;

import java.io.Serializable;

public class BookmarkDoc implements Serializable{

	private static final long serialVersionUID = -9077686671936055197L;
	
	private int bookmarkId;
	private String url;
	private String urlHash;
	private String title;
	private String notes;
	private String username;
	
	public int getBookmarkId() {
		return bookmarkId;
	}
	public void setBookmarkId(int bookmarkId) {
		this.bookmarkId = bookmarkId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrlHash() {
		return urlHash;
	}
	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
		
}
