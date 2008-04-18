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

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;

import com.gnizr.core.search.Search;
import com.gnizr.core.search.SearchResult;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.SyndFeedFactory;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractPagingAction;
import com.gnizr.web.action.LoggedInUserAware;
import com.gnizr.web.action.SessionConstants;
import com.sun.syndication.feed.synd.SyndFeed;

public class SearchBookmark extends AbstractPagingAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1429018432493873503L;

	private static final Logger logger = Logger.getLogger(SearchBookmark.class);
	
	public static final String TYPE_TEXT = "text";
	public static final String TYPE_USER = "user";
	public static final String TYPE_OPEN_SEARCH = "opensearch";
	
	private Search search;
	private UserManager userManager;
	private String queryString;
	private String type;	
	private List<Bookmark> bookmarks;
	private int totalMatched;
	private User loggedInUser;
	private String username;	

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	
	public String executeNoPaging() throws Exception{
		SearchResult<Bookmark> resultset = doSearch();
		if(resultset == null){
			bookmarks = new ArrayList<Bookmark>();
		}else{
			bookmarks = resultset.getResults(0,resultset.length());
		}
		totalMatched = bookmarks.size();
		return SUCCESS;
	}

	@Override
	protected String go() throws Exception {	
		if(TYPE_OPEN_SEARCH.equalsIgnoreCase(getType())){
			return REDIRECT;
		}		
		initPagingAction();
		// reuse an existing query string if possible
		if(getQueryString() == null){
			setQueryString((String)session.get(SessionConstants.SEARCH_STRING));
		}
		if(getType() == null){
			setType((String)session.get(SessionConstants.SEARCH_TYPE));
		}
		
		logger.debug("searchLink: queryString="+queryString+",type="+type+",page="+getPage());		
		bookmarks = new ArrayList<Bookmark>();
		SearchResult<Bookmark> results = getCachedSearchResult();
		if(results == null){
			results = doSearch();
			setCacheSearchResult(getQueryString(),getType(),results);
		}
		if(results != null){
			totalMatched = results.length();		
			int max = getMaxPageNumber(totalMatched, getPerPageCount());	
			setTotalNumOfPages(max);
			int offset = computeOffset(getPage());
			bookmarks.addAll(results.getResults(offset,getPerPageCount()));
			setPreviousPageNum(getPage(),max);
			setNextPageNum(getPage(),max);
			
		}
		return SUCCESS;
	}
	
	public SyndFeed getOpenSearchResult(){
		String title = "Gnizr Search Result for '" + getQueryString() + "'";
		String author = "gnizr";
		String link = getGnizrConfiguration().getWebApplicationUrl();
		String feedUri = GnizrDaoUtil.getRandomURI();
		Date pubDate = GnizrDaoUtil.getNow();
		SyndFeed syndFeed = SyndFeedFactory.create(getBookmarks(),author,title,link,pubDate,feedUri);
		syndFeed = SyndFeedFactory.addOpenSearchModule(syndFeed,getPerPageCount(),computeOffset(getPage()),getTotalMatched(), null);
		return syndFeed;
	}
	
	private SearchResult<Bookmark> doSearch(){		
		if(TYPE_USER.equalsIgnoreCase(getType()) == true){
			User u = loggedInUser;
			if(u == null && username != null){
				if(userManager == null){
					throw new NullPointerException("UserManager is NULL; can't lookup information of user: " + username);
				}
				try{					
					u = userManager.getUser(getUsername());
				}catch(Exception e){
					logger.debug("No such user: " + getUsername());
				}
			}			
			return search.searchBookmarkUser(queryString,u);
		}else{
			return search.searchBookmarkCommunity(queryString);
		}
	}

	private int getMaxPageNumber(int numTotal, int perPageCount){
		int max = 1;
		if(numTotal > 0 && perPageCount > 0){
			int tnp = numTotal / perPageCount;
			if((numTotal % perPageCount) > 0){
				tnp++;
			}
			if(tnp > 1){
				max = tnp;
			}
		}
		return max;
	}
	
	@SuppressWarnings("unchecked")
	private void setCacheSearchResult(String queryString, String type, SearchResult<Bookmark> results){		
		String qHash = DigestUtils.md5Hex(queryString);
		session.put(SessionConstants.SEARCH_TYPE,type);
		session.put(SessionConstants.SEARCH_STRING, queryString);
		session.put(SessionConstants.SEARCH_STRING_HASH,qHash);
		session.put(SessionConstants.SEARCH_LINK_RESULT,results);
	}
	
	@SuppressWarnings({"unchecked" })
	private SearchResult<Bookmark> getCachedSearchResult(){
		String qHash = null;
		if(getQueryString() != null){
			qHash = DigestUtils.md5Hex(getQueryString());	
		}
		String hash = (String)session.get(SessionConstants.SEARCH_STRING_HASH);
		String type = (String)session.get(SessionConstants.SEARCH_TYPE);
		if(hash != null && type != null){
			if(hash.equals(qHash) && type.equals(getType())){
				return (SearchResult<Bookmark>)session.get(SessionConstants.SEARCH_LINK_RESULT);
			}
		}
		return null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}	
	
	public int getTotalMatched(){
		return totalMatched;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
}
