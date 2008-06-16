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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gnizr.db.dao.folder.FoldersParser;
import com.gnizr.db.dao.tag.TagsParser;

/**
 * <p>Bookmark saved by a user. A bookmark 
 * represents a URL (or link) that has been saved by a given user. User can define 
 * properties about a bookmark -- title, notes and tags. A bookmark maybe saved in
 * zero or more folders. Each bookmark has two different timestampes: one describes the 
 * date/time when the bookmark is first created, and the other describes the date/time
 * when any bookmark properties have been modified.</p>
 * <p>All bookmarks have an unique ID, which is usually assigned by the database system
 * when the DB record for a bookmark is created for the first time.</p>
 * @author Harry Chen
 * @since 2.2
 */
public class Bookmark implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7440530893915763192L;
	
	private int id;
	private String title;
	private String notes;
	private Date createdOn;
	private Date lastUpdated;
	private String tags;
	private String folders;
	private User user;
	private Link link;

	/**
	 * Creates a new instance of this class. 
	 */
	public Bookmark(){	
		this.title = "";
		this.notes = "";
	}
	
	/**
	 * Creates a new instance of this class with a specific <code>id</code>.
	 * @param id the ID of this bookmark
	 */
	public Bookmark(int id){
		this();
		this.id = id;
	}
	
	/**
	 * Creates a new instance of this class and intitializes
	 * values that describe the bookmark owner and the bookmark URL. 
	 * @param user the user who owns this bookmark.
	 * @param link the URL that the user has bookmarked.
	 */
	public Bookmark(User user, Link link){				
		this();
		this.user = new User(user);
		this.link = new Link(link);
	}
	
	
	/**
	 * Copy constructor. Creates a new instance of this class and initializes 
	 * this instance using an existing <code>Bookmark</code>. 
	 * 
	 * @param b bookmark object to copy.
	 */
	public Bookmark(Bookmark b){						
		this.id = b.id;		
		this.title = b.title;
		this.notes = b.notes;
		this.folders = b.folders;
		if(b.lastUpdated != null){
			this.lastUpdated = (Date)b.lastUpdated.clone();
		}
		if(b.createdOn != null){
			this.createdOn = (Date)b.createdOn.clone();
		}
		this.tags = b.tags;
		if(b.user != null){
			this.user = new User(b.user);
		}
		if(b.link != null){
			this.link = new Link(b.link);
		}
	}
	
	/**
	 * Returns the date/time when the bookmark is created.
	 * 
	 * @return the creation date/time of this bookmark.
	 */
	public Date getCreatedOn() {
		return createdOn;
	}
	
	/**
	 * Sets the date/time when the bookmark is created.
	 * 
	 * @param createdOn date/time when this bookmark is created.
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	
	/**
	 * Returns the ID of this bookmark
	 * @return bookmark ID.
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the ID of this bookmark
	 * @param id bookmark ID.
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the date/time when any properties of this bookmark
	 * has been changed.
	 * 
	 * @return the last updated date/time of this bookmark.
	 */
	public Date getLastUpdated() {
		return lastUpdated;
	}
	
	/**
	 * Sets the last updated date/time of this bookmark.
	 * 
	 * @param lastUpdated when this bookmark was last updated.
	 */
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	/**
	 * Returns the notes about this bookmark.
	 * 
	 * @return bookmark notes
	 */
	public String getNotes() {
		return notes;
	}
	
	/**
	 * Sets the notes about this bookmark.
	 * 
	 * @param notes bookmark notes.
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	/**
	 * Returns tags used to label this bookmark. If more than one tag is used,
	 * tags are seperated by one or more white-spaces.
	 * @return a string of tags
	 */
	public String getTags() {
		return tags;
	}
	/**
	 * Sets the tags used to label this bookmark. If more than one tag is used,
	 * seperate tags using a white-space.
	 * @param tags a string of tags
	 */
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	/**
	 * Returns a parsed version of the tag string. Tags are parsed and stored in
	 * a <code>List</code> object. Each element in the list represents a single tag.
	 *
	 * @return a list of tags
	 */
	public List<String> getTagList(){
		TagsParser parser = new TagsParser(tags);
		return parser.getTags();		
	}
	
	/**
	 * Returns only machine tags of this bookmark. 
	 * 
	 * @return a list of tags.
	 */
	public List<MachineTag> getMachineTagList() {
		TagsParser parser = new TagsParser(tags);
		return parser.getMachineTags();
	}
	
	
	/**
	 * Search machine tags used to label this bookmark. 
	 * 
	 * @param nsFilter a regular expression of namespace string to match
	 * @param predFilter a regular expression of predicate string to match
	 * @return a list of machine tags that matched the defined filters.
	 */
	public List<MachineTag> getMachineTagList(String nsFilter, String predFilter){
		List<MachineTag> allMTags = getMachineTagList();
		if(nsFilter == null && predFilter == null){
			return allMTags;
		}else{
			List<MachineTag> filteredMTags = new ArrayList<MachineTag>();
			for(MachineTag mt : allMTags){
				boolean nsMatched = false;
				boolean pdMatched = false;
				if(nsFilter != null && mt.getNsPrefix() != null &&
				   nsFilter.equals(mt.getNsPrefix()) == true){
					nsMatched = true;
				}else if(nsFilter == null){
					nsMatched = true;
				}
				if(predFilter != null && mt.getPredicate() != null &&
				   predFilter.equals(mt.getPredicate()) == true){
					pdMatched = true;
				}else if(predFilter == null){
					pdMatched = true;
				}
				if(nsMatched == true && pdMatched == true){
					filteredMTags.add(mt);
				}
			}
			return filteredMTags;
		}
	}
	
	/**
	 * Returns the title description of this bookmark.
	 * 
	 * @return bookmark title.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Sets the title description of this bookmark.
	 * @param title bookmark title.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns the <code>Link</code> object reprsentation of the bookmark URL.
	 * @return bookmark url.
	 */
	public Link getLink() {
		return link;
	}
	
	/**
	 * Sets the bookmark URL, as <code>Link</code> object, of this bookmark. 
	 * @param link bookmark url.
	 */
	public void setLink(Link link) {
		this.link = link;
	}
	
	/**
	 * Returns the owner user of tihs bookmark.
	 * @return bookmark owner.
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Sets the owner user of this bookmark.
	 * 
	 * @param user bookmark user.
	 */
	public void setUser(User user) {
		this.user = user;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((createdOn == null) ? 0 : createdOn.hashCode());
		result = prime * result + ((folders == null) ? 0 : folders.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((notes == null) ? 0 : notes.hashCode());
		result = prime * result + ((tags == null) ? 0 : tags.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Bookmark other = (Bookmark) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (folders == null) {
			if (other.folders != null)
				return false;
		} else if (!folders.equals(other.folders))
			return false;
		if (id != other.id)
			return false;
		if (lastUpdated == null) {
			if (other.lastUpdated != null)
				return false;
		} else if (!lastUpdated.equals(other.lastUpdated))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (notes == null) {
			if (other.notes != null)
				return false;
		} else if (!notes.equals(other.notes))
			return false;
		if (tags == null) {
			if (other.tags != null)
				return false;
		} else if (!tags.equals(other.tags))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id",getId());
		map.put("user",getUser());
		map.put("link",getLink());
		map.put("notes",getNotes());
		map.put("title",getTitle());
		map.put("tags",getTags());
		map.put("createdOn",getCreatedOn());
		map.put("lastUpdted",getLastUpdated());		
		map.put("folders", getFolders());
		return map.toString();
	}

	/**
	 * Retuns the list of folders this bookmark is saved to. If the bookmark 
	 * is saved in more than one folder, white-spaces are used to seperate multiple
	 * folder names.
	 * @return folder names.
	 */
	public String getFolders() {
		return folders;
	}
	
	/**
	 * Sets the folders that this bookmark is saved to. If the bookmark is saved in
	 * more than one folder, use white-spaces to seperate the folder names.
	 * 
	 * @param folders folder names
	 */
	public void setFolders(String folders) {
		this.folders = folders;
	}
	
	/**
	 * Returns a parsed version of the folder names. Folder names seperated by 
	 * white-spaces are parsed and stored in a <code>List</code> object. Each element
	 * in the list represents a folder name.
	 * @return a list of parsed folder names.
	 */
	public List<String> getFolderList(){
		FoldersParser parser = new FoldersParser(getFolders());
		return parser.getFolderNames();
	}
}
