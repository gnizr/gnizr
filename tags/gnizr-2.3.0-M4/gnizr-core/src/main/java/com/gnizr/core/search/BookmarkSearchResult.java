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

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;

public class BookmarkSearchResult implements SearchResult<Bookmark>{

	private Logger logger = Logger.getLogger(BookmarkSearchResult.class);
	
	private BookmarkDao bookmarkDao;
	private User user;
	private String searchQuery;
	private DaoResult<Bookmark> result;
	
	public BookmarkSearchResult(GnizrDao gnizrDao, String searchQuery){
		this.searchQuery = searchQuery;
		this.bookmarkDao = gnizrDao.getBookmarkDao();
		doSearch(0,10);
	}
	
	public BookmarkSearchResult(GnizrDao gnizrDao, String searchQuery, User user){
		this.searchQuery = searchQuery;
		this.bookmarkDao = gnizrDao.getBookmarkDao();
		this.user = user;
		doSearch(0,10);
	}
	
	private void doSearch(int offset, int count) {
		logger.debug("doSearch: user="+user+",offset="+offset+",count="+count);		
		if(user == null){
			result = bookmarkDao.searchCommunityBookmarks(searchQuery, offset, count);
		}else{
			result = bookmarkDao.searchUserBookmarks(searchQuery, user, offset, count);
		}
	}
	
	public Bookmark getResult(int n) {
		if(n >= result.getResult().size()){
			doSearch(0,n+10);
		}		
		return result.getResult().get(n);
	}
	
	public List<Bookmark> getResults(int offset, int count) {
		doSearch(offset, count);
		return result.getResult();
	}
	
	public int length() {
		return result.getSize();
	}
	
}
