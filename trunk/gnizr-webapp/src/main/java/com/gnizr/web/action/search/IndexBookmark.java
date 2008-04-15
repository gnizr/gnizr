package com.gnizr.web.action.search;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.search.DocumentCreator;
import com.gnizr.core.search.SearchIndexManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserStat;
import com.gnizr.web.action.AbstractLoggedInUserAction;
import com.opensymphony.webwork.interceptor.SessionAware;

/**
 * <p>An Action implementation for building a search index of all bookmarks. 
 * This action should be accessible only to
 * the superuser of the gnizr system (i.e., the user <code>gnizr</code>).</p>
 * <p>The enforcement of this logic is done by using the proper
 * interceptor configuration in the XWork configuration.
 * </p>
 * 
 * @author Harry Chen
 * @since 2.4
 */
public class IndexBookmark extends AbstractLoggedInUserAction implements SessionAware{

	private static final long serialVersionUID = 7092403721954833011L;
	private static final Logger logger = Logger.getLogger(IndexBookmark.class);
	
	private SearchIndexManager searchIndexManager;
	private BookmarkPager bookmarkPager;
	private IndexStatus status;
	@SuppressWarnings("unchecked")
	private Map session;
	
	/**
	 * Gets the search index manager used by this class.
	 * 
	 * @return an instance of the <code>SearchIndexManager</code>.
	 * Returns <code>null</code> if the manager has been set.
	 */
	public SearchIndexManager getSearchIndexManager() {
		return searchIndexManager;
	}

	/**
	 * Sets the search index manager used by this class to perform 
	 * search index updates. 
	 * 
	 * @param searchIndexManager an instantiated <code>SearchIndexManager</code>
	 */
	public void setSearchIndexManager(SearchIndexManager searchIndexManager) {
		this.searchIndexManager = searchIndexManager;
	}

	/**
	 * Gets the bookmark pager used for iterating through
	 * all bookmarks saved in the system.
	 * 
	 * @return an instance of the <code>BookmarkPager</code>.
	 * Returns <code>null</code> if the pager has been set.
	 */
	public BookmarkPager getBookmarkPager() {
		return bookmarkPager;
	}

	/**
	 * Sets the bookmark pager used for iterating through
	 * all bookmarks saved in the system.
	 * 
	 * @param bookmarkPager an instantiated <code>BookmarkPager</code.
	 */
	public void setBookmarkPager(BookmarkPager bookmarkPager) {
		this.bookmarkPager = bookmarkPager;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return true;
	}
	
	@Override
	public String doDefault() throws Exception {
		logger.debug("IndexBookmark.doDefault()");
		IndexStatus status = (IndexStatus)session.get("status");
		if(status != null){
			logger.debug("Clear index status report from Session.");
			session.remove("status");
		}
		return SUCCESS;
	}

	/**
	 * <p>Executes this action to perform search index update. Once the process is started,
	 * it can't be terminated. Interrupting the running thread may be result a partially 
	 * completed search index (i.e., not all saved bookmarks are searchable).</p> 
	 * <p>While the index process in progress, this action reports the status 
	 * updating the <code>IndexStatus</code> object. This object can accessible
	 * by calling <code>getIndexStatus</code> or accessible it in the HTTP Session under
	 * the name <code>"status"</code>. </p>
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected String go() throws Exception {
		logger.debug("IndexBookmark action go() is called.");
		resolveUser();
		
		logger.debug("Resetting search index database...");
		searchIndexManager.resetIndex();
		
		status = new IndexStatus();
		session.put("status", status);
		
		int cnt = 0;
		List<UserStat> userStats = userManager.listUserStats();
		for(UserStat stat : userStats){
			cnt = cnt+stat.getNumOfBookmarks();
		}
		status.setTotalBookmarkCount(cnt);
			
		for(UserStat stat : userStats){
			User user = userManager.getUser(stat.getUsername());
			logger.info("Indexing the bookmarks of user: " + user.getUsername());
			int ppc = 10;
			int start = 0;
			int numOfPages = bookmarkPager.getMaxPageNumber(user,ppc);
			for(int i = 0; i < numOfPages; i++){
				logger.debug("--> page=" + i + ", start="+start+", ppc=" + ppc);
				DaoResult<Bookmark> result  = bookmarkPager.pageBookmark(user,start,ppc);
				doIndex(result.getResult());
				start = start + ppc;
				int indexCount = status.getBookmarkIndexed() + ppc;
				if(indexCount > status.getTotalBookmarkCount()){
					status.setBookmarkIndexed(status.getTotalBookmarkCount());
				}else{
					status.setBookmarkIndexed(indexCount);
				}			
				logger.debug("searchIndexManager.getIndexProcessWorkload = " + searchIndexManager.getIndexProcessWorkLoad());
				while(searchIndexManager.getIndexProcessWorkLoad() > 100){
					try{
						logger.debug("Wait 100ms. SearchIndexManager seems to be too busy");
						Thread.sleep(100);					
					}catch(Exception e){						
						logger.error(e);					
					}
				}				
			}
		}
		while(searchIndexManager.isIndexProcessActive() == true){
			try{
				logger.debug("Wait 5 seconds. SearchIndexManager still have work to do.");
				Thread.sleep(5000);					
			}catch(Exception e){						
				logger.error(e);					
			}
		}
		return SUCCESS;
	}

	private void doIndex(List<Bookmark> bookmarks){
		try{
			for(Bookmark b : bookmarks){
				Document d = DocumentCreator.createDocument(b);
				if(d != null){
					searchIndexManager.addIndex(d);
				}
			}			
		}catch(Exception e){
			logger.error("IndexBookmark.doIndex(), "+e);
		}
	}
	
	public class IndexStatus {
		
		private int totalBookmarkCount;
		private int bookmarkIndexed;
		
		public IndexStatus(){
			totalBookmarkCount = 0;
			bookmarkIndexed = 0;
		}
		public int getTotalBookmarkCount() {
			return totalBookmarkCount;
		}
		public void setTotalBookmarkCount(int totalBookmarkCount) {
			this.totalBookmarkCount = totalBookmarkCount;
		}
		public int getBookmarkIndexed() {
			return bookmarkIndexed;
		}
		public void setBookmarkIndexed(int bookmarkIndexed) {
			this.bookmarkIndexed = bookmarkIndexed;
		}
			
	}

	@SuppressWarnings("unchecked")
	public void setSession(Map session) {
		this.session = session;
	}
	
}
