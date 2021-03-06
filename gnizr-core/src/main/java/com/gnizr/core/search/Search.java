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
package com.gnizr.core.search;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.User;

/**
 * This class provides the bookmark search implementation. 
 *  
 * @author Harry Chen
 *
 */
public class Search implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5131043207555467304L;

	private static final Logger logger = Logger.getLogger(Search.class);
	
	private GnizrDao gnizrDao;
	private SearchIndexProfile searchIndexProfile;
	
	public Search(GnizrDao gnizrDao){
		this.gnizrDao = gnizrDao;
	}
	
	public GnizrDao getGnizrDao() {
		return gnizrDao;
	}

	public void setGnizrDao(GnizrDao gnizrDao) {
		this.gnizrDao = gnizrDao;
	}

	public SearchIndexProfile getSearchIndexProfile() {
		return searchIndexProfile;
	}

	public void setSearchIndexProfile(SearchIndexProfile searchIndexProfile) {
		this.searchIndexProfile = searchIndexProfile;
	}

	public void init(){
		if(gnizrDao == null){
			throw new NullPointerException("Search.init(): GnizrDao is not defined");
		}
		if(searchIndexProfile == null){
			throw new NullPointerException("Search.init(): searchIndexProfile is not defined");
		}
	}
	
	/**
	 * Returns bookmarks saved in the community that match the input search query.
	 * @deprecated
	 * @param textQuery a search query 
	 * @return matching bookmarks
	 */
	public SearchResult<Bookmark> searchBookmarkCommunity(String textQuery){
		logger.debug("searchLink: textQuery="+textQuery);
		return new BookmarkSearchResult(gnizrDao, textQuery);		
	}
	
	/**
	 * Returns bookmarks saved by <code>user</code> that match the input search query.
	 * @deprecated
	 * @param textQuery a search query 
	 * @return matching bookmarks
	 */
	public SearchResult<Bookmark> searchBookmarkUser(String textQuery, User user){
		logger.debug("searchLink: textQuery="+textQuery+",user="+user);
		return new BookmarkSearchResult(gnizrDao, textQuery, user);
	}
	
	/**
	 * Returns tags used to label bookmarks that match input search query.
	 * 
	 * @param textQuery a search query used to match bookmarks
	 * @return a list of bookmark-tag relations
	 */
	public SearchResult<BookmarkTag> searchBookmarkTags(String textQuery){
		return new BookmarkTagSearchResult(gnizrDao,textQuery);
	}

}
