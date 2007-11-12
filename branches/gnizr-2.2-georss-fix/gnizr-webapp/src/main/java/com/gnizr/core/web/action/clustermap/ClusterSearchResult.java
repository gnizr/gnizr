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
package com.gnizr.core.web.action.clustermap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.search.Search;
import com.gnizr.core.search.SearchResult;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;

public class ClusterSearchResult extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6748362939145561219L;
	private static final Logger logger = Logger.getLogger(ClusterSearchResult.class);
	
	private String queryString;
	private String type;	
	private Search search;
	
	private Map<String,List<Integer>> userClusterMap = new HashMap<String, List<Integer>>();
	private Map<String,List<Integer>> tagClusterMap = new HashMap<String, List<Integer>>();
	private List<Integer> rootCluster = new ArrayList<Integer>();
	private List<User> userClusterKey = new ArrayList<User>();
	private List<Tag> tagClusterKey = new ArrayList<Tag>();
	private List<Bookmark> bookmarks = new ArrayList<Bookmark>(); 
	
	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String query) {
		this.queryString = query;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public String getType() {
		return type;
	}

	public void setType(String searchType) {
		this.type = searchType;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("go: query="+queryString+",searchType="+type);
		SearchResult<BookmarkTag> searchResult = getSearchResult();
		int max = searchResult.length();
		if(max > 0){
			List<BookmarkTag> bmTags = searchResult.getResults(0,max);
			for(BookmarkTag bt : bmTags){
				Bookmark bm = bt.getBookmark();
				if(rootCluster.contains(bm.getId()) == false){
					rootCluster.add(bm.getId());
					bookmarks.add(bm);
				}
				User user = bm.getUser();
				List<Integer> userCluster = getUserCluster(user);
				if(userCluster.contains(bm.getId()) == false){
					userCluster.add(bm.getId());
				}				
				
				Tag tag = bt.getTag();
				List<Integer> tagCluster = getTagCluster(tag);
				if(tagCluster.contains(bm.getId()) == false){
					tagCluster.add(bm.getId());
				}
			}
		}		
		return SUCCESS;
	}

	private List<Integer> getUserCluster(User user){
		String key = "u"+user.getId();
		List<Integer> userCluster = userClusterMap.get(key);
		if(userCluster == null){
			userClusterKey.add(user);
			userCluster = new ArrayList<Integer>();
			userClusterMap.put(key,userCluster);
		}
		return userCluster;		
	}
	
	private List<Integer> getTagCluster(Tag tag){
		String key = "t"+tag.getId();
		List<Integer> tagCluster = tagClusterMap.get(key);
		if(tagCluster == null){
			tagClusterKey.add(tag);
			tagCluster = new ArrayList<Integer>();
			tagClusterMap.put(key,tagCluster);
		}
		return tagCluster;		
	}
	
	private SearchResult<BookmarkTag> getSearchResult(){
		return search.searchBookmarkTags(queryString);		
	}
	
	public List<Integer> getRootCluster() {
		return rootCluster;
	}

	public List<Tag> getTagClusterKey() {
		return tagClusterKey;
	}

	public Map<String, List<Integer>> getTagClusterMap() {
		return tagClusterMap;
	}

	public List<User> getUserClusterKey() {
		return userClusterKey;
	}

	public Map<String, List<Integer>> getUserClusterMap() {
		return userClusterMap;
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

}
