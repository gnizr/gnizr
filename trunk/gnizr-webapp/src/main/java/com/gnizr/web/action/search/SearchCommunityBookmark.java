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
import com.gnizr.web.action.AbstractPagingAction;
import com.gnizr.web.action.OpenSearchResultAware;
import com.sun.syndication.feed.synd.SyndFeed;

public class SearchCommunityBookmark extends AbstractPagingAction implements OpenSearchResultAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2956638031909684580L;

	private String queryString;
	private List<BookmarkDoc> bookmarks;
	private int totalMatched;
	
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
				result = bookmarkSearcher.searchAll(getQueryString(),offset,getPerPageCount());
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
}