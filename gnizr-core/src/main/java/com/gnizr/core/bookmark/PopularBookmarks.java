package com.gnizr.core.bookmark;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.log4j.Logger;

import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.bookmark.BookmarkDao;

/**
 * Class for finding popular bookmarks saved by users in a gnizr community. This class
 * implements a data caching mechanism that can reduce database accesses. When the caching 
 * is turned on, data of popular bookmarks are fetched and then stored in the memory 
 * for a pre-defined period of time. When the cache data is expired, a fresh copy of
 * the data is fetched from the database.    
 * 
 * @author Harry Chen
 * @since 2.5.0
 */
public class PopularBookmarks implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5377844316081009705L;

	private static final Logger logger = Logger.getLogger(PopularBookmarks.class);
	
	private static final int ALL_TIME = 1;
	private ReentrantReadWriteLock lockOfAllTime = new ReentrantReadWriteLock();
	
	//private static final int PAST_24_H = 2;
	
	//private static final int PAST_WEEK = 3;
	
	
	private BookmarkDao bookmarkDao;
	
	private ConcurrentMap<Integer, Cache> cacheMap;
	
	private int cacheExpireInMinutes;
	
	/**
	 * Creates an instance of this class with a defined <code>GnizrDao</code> object.
	 * @param gnizrDao an instantiated <code>GnizrDao</code> object.
	 */
	public PopularBookmarks(GnizrDao gnizrDao){
		this.bookmarkDao = gnizrDao.getBookmarkDao();
		cacheMap = new ConcurrentHashMap<Integer, Cache>();
	}
	
	public List<Bookmark> getTop10AllTime(){
		logger.debug("getTop10AllTime popular bookmarks.");	
		List<Bookmark> bmarks = null;
		lockOfAllTime.readLock().lock();		
		if(isCacheValid(ALL_TIME) == false){
			lockOfAllTime.readLock().unlock();
			lockOfAllTime.writeLock().lock();
			if(isCacheValid(ALL_TIME) == false){
				Date start = new GregorianCalendar(1900,1,1).getTime();
				Date end = GnizrDaoUtil.getNow();	
				bmarks = this.bookmarkDao.getPopularCommunityBookmarks(start,end,10);
				Cache cache = new Cache(bmarks,end);
				cacheMap.put(ALL_TIME,cache);
			}
			lockOfAllTime.readLock().lock();
			lockOfAllTime.writeLock().unlock();
		}
		bmarks = cacheMap.get(ALL_TIME).getBookmarks();
		lockOfAllTime.readLock().unlock();
		return bmarks;
	}

	private boolean isCacheValid(int cacheCode){
		Cache cache = cacheMap.get(cacheCode);
		if(cache == null){
			return false;
		}else{
			Date lastUpdated = cache.getLastUpdated();
			Calendar now = GregorianCalendar.getInstance();
			now.add(Calendar.MINUTE, (-1*cacheExpireInMinutes));
			if(now.before(lastUpdated) == true){
				return true;
			}
		}
		return false;
	}
	
	
	public int getCacheExpireInMinutes() {
		return cacheExpireInMinutes;
	}

	public void setCacheExpireInMinutes(int cacheExpireInMinutes) {
		this.cacheExpireInMinutes = cacheExpireInMinutes;
	}
	
	private class Cache {
		private List<Bookmark> bookmarks;
		private Date lastUpdated;
		
		public Cache(){
			this.bookmarks = new ArrayList<Bookmark>();
			this.lastUpdated = null;
		}
		
		public Cache(List<Bookmark> bookmarks, Date lastUpdate){
			this.bookmarks = bookmarks;
			this.lastUpdated = lastUpdate;
		}

		public List<Bookmark> getBookmarks() {
			return bookmarks;
		}

		public void setBookmarks(List<Bookmark> bookmarks) {
			this.bookmarks = bookmarks;
		}

		public Date getLastUpdated() {
			return lastUpdated;
		}

		public void setLastUpdated(Date lastUpdated) {
			this.lastUpdated = lastUpdated;
		}	
	}
	
}
