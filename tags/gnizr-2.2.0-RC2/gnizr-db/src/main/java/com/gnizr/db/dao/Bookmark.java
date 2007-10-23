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

	public Bookmark(){	
		this.title = "";
		this.notes = "";
	}
	
	public Bookmark(int id){
		this();
		this.id = id;
	}
	
	public Bookmark(User user, Link link){				
		this();
		this.user = new User(user);
		this.link = new Link(link);
	}
	
	
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
	
	public Date getCreatedOn() {
		return createdOn;
	}
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	
	public List<String> getTagList(){
		TagsParser parser = new TagsParser(tags);
		return parser.getTags();		
	}
	
	public List<MachineTag> getMachineTagList() {
		TagsParser parser = new TagsParser(tags);
		return parser.getMachineTags();
	}
	
	
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
	
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Link getLink() {
		return link;
	}
	
	public void setLink(Link link) {
		this.link = link;
	}
	
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = PRIME * result + id;
		result = PRIME * result + ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = PRIME * result + ((link == null) ? 0 : link.hashCode());
		result = PRIME * result + ((notes == null) ? 0 : notes.hashCode());
		result = PRIME * result + ((tags == null) ? 0 : tags.hashCode());
		result = PRIME * result + ((title == null) ? 0 : title.hashCode());
		result = PRIME * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Bookmark other = (Bookmark) obj;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
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

	public String getFolders() {
		return folders;
	}

	public void setFolders(String folders) {
		this.folders = folders;
	}
	
	public List<String> getFolderList(){
		FoldersParser parser = new FoldersParser(getFolders());
		return parser.getFolderNames();
	}
}
