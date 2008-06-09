package com.gnizr.core.bookmark;

import java.io.Serializable;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.bookmark.BookmarkDao;

/**
 * Class for finding popular bookmarks saved by users in a gnizr community.
 * 
 * @author Harry Chen
 *
 */
public class PopularBookmarks implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5377844316081009705L;

	private static final Logger logger = Logger.getLogger(PopularBookmarks.class);
	
	private BookmarkDao bookmarkDao;
	
	/**
	 * Creates an instance of this class with a defined <code>GnizrDao</code> object.
	 * @param gnizrDao an instantiated <code>GnizrDao</code> object.
	 */
	public PopularBookmarks(GnizrDao gnizrDao){
		this.bookmarkDao = gnizrDao.getBookmarkDao();
	}
	
	public List<Bookmark> getTop10AllTime(){
		logger.debug("getTop10AllTime popular bookmarks.");
		Date start = new GregorianCalendar(1900,1,1).getTime();
		Date end = GregorianCalendar.getInstance().getTime();
		return this.bookmarkDao.getPopularCommunityBookmarks(start,end,10);
	}

}
