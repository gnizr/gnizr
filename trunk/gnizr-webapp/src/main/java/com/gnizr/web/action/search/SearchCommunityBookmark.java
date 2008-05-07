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
package com.gnizr.web.action.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import com.gnizr.core.search.BookmarkDoc;
import com.gnizr.core.search.BookmarkSearcher;
import com.gnizr.core.search.DocumentCreator;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.SyndFeedFactory;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractPagingAction;
import com.gnizr.web.action.LoggedInUserAware;
import com.gnizr.web.action.OpenSearchResultAware;
import com.sun.syndication.feed.synd.SyndFeed;

public class SearchCommunityBookmark extends AbstractPagingAction implements OpenSearchResultAware, LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2956638031909684580L;

	private String queryString;
	private List<BookmarkDoc> bookmarks;
	private int totalMatched;
	private User loggedInUser;
	
	private BookmarkSearcher bookmarkSearcher;
	
	
	public BookmarkSearcher getBookmarkSearcher() {
		return bookmarkSearcher;
	}

	public void setBookmarkSearcher(BookmarkSearcher bookmarkSearcher) {
		this.bookmarkSearcher = bookmarkSearcher;
	}

	public List<BookmarkDoc> getBookmarks() {
		return bookmarks;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	protected String go() throws Exception {
		initPagingAction();
		if(getQueryString() != null){
			int offset = computeOffset(getPage());			
			DaoResult<BookmarkDoc> result = null;
			try{
				StringBuilder qs = new StringBuilder(getQueryString());
				if(loggedInUser != null && loggedInUser.getUsername() != null){
					qs.append(" -");
					qs.append(DocumentCreator.FIELD_USER);
					qs.append(":");
					qs.append(loggedInUser.getUsername());
				}				
				result = bookmarkSearcher.searchAll(qs.toString(),offset,getPerPageCount());
				int max = computeMaxPageNumber(getPerPageCount(),result.getSize());
				setTotalNumOfPages(max);
				setNextPageNum(getPage(),max);
				setPreviousPageNum(getPage(),max);
				totalMatched = result.getSize();
				bookmarks = result.getResult();
			}catch(Exception e){
			
			}
			if(bookmarks == null){
				bookmarks = new ArrayList<BookmarkDoc>();
			}
			return SUCCESS;
		}
		return INPUT;
	}
	
	public SyndFeed getOpenSearchResult(){
		String title = "Gnizr Search Result for '" + getQueryString() + "'";
		String author = "gnizr";
		String link = getGnizrConfiguration().getWebApplicationUrl();
		String feedUri = GnizrDaoUtil.getRandomURI();
		Date pubDate = GnizrDaoUtil.getNow();
		SyndFeed syndFeed = SyndFeedFactory.createFromBookmarkDoc(getBookmarks(),author,title,link,pubDate,feedUri);
		syndFeed = SyndFeedFactory.addOpenSearchModule(syndFeed,getPerPageCount(),computeOffset(getPage()),getTotalMatched(), null);
		return syndFeed;
	}

	public JSONObject getJsonResult(){
		SyndFeed feed = getOpenSearchResult();
		if(feed != null){
			return OpenSearchProxy.createJsonResult(feed);
		}
		return null;
	}
	
	public int getTotalMatched() {
		return totalMatched;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}
}
