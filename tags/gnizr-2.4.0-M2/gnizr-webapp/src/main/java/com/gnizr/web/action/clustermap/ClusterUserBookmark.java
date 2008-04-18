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
package com.gnizr.web.action.clustermap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.tag.TagManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;

public class ClusterUserBookmark extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8507212769894768997L;
	private static final Logger logger = Logger.getLogger(ClusterUserBookmark.class);
	
	private String username;
	private List<Bookmark> bookmarks = new ArrayList<Bookmark>();
	private Map<String, List<Integer>> cluster = new HashMap<String, List<Integer>>();
	private List<Tag> tags = new ArrayList<Tag>();
	private TagManager tagManager;
	
	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public Map<String, List<Integer>> getCluster() {
		return cluster;
	}

	public void setCluster(Map<String, List<Integer>> cluster) {
		this.cluster = cluster;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("go(): username="+username);
		if(username != null){
			List<BookmarkTag> bookmarkTags = tagManager.listBookmarkTag(new User(username));				
			ClusterMapTool.clusterBookmarkTags(bookmarkTags, cluster, bookmarks, tags);	
		}		
		return SUCCESS;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

}
