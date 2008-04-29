package com.gnizr.web.action.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.sf.json.JSONObject;

import com.gnizr.core.search.BookmarkDoc;
import com.gnizr.core.search.BookmarkSearcher;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.SyndFeedFactory;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractPagingAction;
import com.gnizr.web.action.LoggedInUserAware;
import com.gnizr.web.action.OpenSearchResultAware;
import com.sun.syndication.feed.synd.SyndFeed;


public class SearchUserBookmark extends AbstractPagingAction implements LoggedInUserAware, OpenSearchResultAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1924677398312543973L;

	private User loggedInUser;
	private String queryString;
	private int totalMatched;
	private List<BookmarkDoc> bookmarks;
	
	private BookmarkSearcher bookmarkSearcher;
	
	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public BookmarkSearcher getBookmarkSearcher() {
		return bookmarkSearcher;
	}

	public void setBookmarkSearcher(BookmarkSearcher bookmarkSearcher) {
		this.bookmarkSearcher = bookmarkSearcher;
	}
	
	public User getUser(){
		return getLoggedInUser();
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public int getTotalMatched() {
		return totalMatched;
	}

	public List<BookmarkDoc> getBookmarks() {
		return bookmarks;
	}

	@Override
	protected String go() throws Exception {
		initPagingAction();
		if(getQueryString() != null){
			int offset = computeOffset(getPage());
			DaoResult<BookmarkDoc> result = null;
			try{
				result = bookmarkSearcher.searchUser(getQueryString(),getUser().getUsername(),offset,getPerPageCount());
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
		}
		
		return SUCCESS;
	}

	public SyndFeed getOpenSearchResult(){
		String title = "Gnizr search result for '" + getQueryString() + "' -- saved by " + getUser().getFullname();
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
	
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

}
