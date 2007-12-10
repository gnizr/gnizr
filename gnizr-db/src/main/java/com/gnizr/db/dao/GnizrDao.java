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
package com.gnizr.db.dao;

import java.io.Serializable;

import javax.sql.DataSource;

import com.gnizr.db.dao.bookmark.BookmarkDBDao;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.bookmark.GeometryMarkerDBDao;
import com.gnizr.db.dao.bookmark.GeometryMarkerDao;
import com.gnizr.db.dao.folder.FolderDBDao;
import com.gnizr.db.dao.folder.FolderDao;
import com.gnizr.db.dao.foruser.ForUserDBDao;
import com.gnizr.db.dao.foruser.ForUserDao;
import com.gnizr.db.dao.link.LinkDBDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.subscription.FeedSubscriptionDBDao;
import com.gnizr.db.dao.subscription.FeedSubscriptionDao;
import com.gnizr.db.dao.tag.TagAssertionDBDao;
import com.gnizr.db.dao.tag.TagAssertionDao;
import com.gnizr.db.dao.tag.TagDBDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.tag.TagPropertyDBDao;
import com.gnizr.db.dao.tag.TagPropertyDao;
import com.gnizr.db.dao.user.UserDBDao;
import com.gnizr.db.dao.user.UserDao;

/**
 * A convenient class that wraps DAO objects for access gnizr persistent store. There are two ways to create
 * an instance of this class: (1) via the static class method <code>GnizrDao.getInstance</code> and (2) 
 * use the default constructor and explicitly set individual DAO objects to be wrapped. Objects created via 
 * (1) will be configured to wrap a default of DAO objects. 
 *  
 * <p>To create an instance of this class:</p>
 * <p>
 * <code>
 *  javax.sql.DataSource ds = ... //create data source <br/>
 *  GnizrDao gDao = GnizrDao.getInstance(ds);
 *  
 * </code>
 * </p>
 * @author harryc
 *
 */
public class GnizrDao implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6708409487852037019L;
	
	private LinkDao linkDao;
	private BookmarkDao bookmarkDao;
	private UserDao userDao;
	private TagDao tagDao;
	private TagPropertyDao tagPropertyDao;
	private TagAssertionDao tagAssertionDao;
	private FeedSubscriptionDao feedSubscriptionDao;
	private ForUserDao forUserDao;
	private FolderDao folderDao;
	private GeometryMarkerDao geometryMarkerDao;
	private static GnizrDao gnizrDao;
	
	/**
	 * Creates a singlton of this class using the defined connection object <code>datasource</code>. This singlton 
	 * class will wrap a default set of DAO objects. Clients can expect all <code>get</code> methods of this class
	 * will return instantiated DAO objects. 
	 *  
	 * @param datasource an instantiated connection object for accessing persistent store. 
	 * @return an object this class
	 */
	public static final GnizrDao getInstance(DataSource datasource){
		if(gnizrDao == null){
			gnizrDao = new GnizrDao();
			gnizrDao.setBookmarkDao(new BookmarkDBDao(datasource));
			gnizrDao.setUserDao(new UserDBDao(datasource));
			gnizrDao.setLinkDao(new LinkDBDao(datasource));
			gnizrDao.setTagDao(new TagDBDao(datasource));
			gnizrDao.setFeedSubscriptionDao(new FeedSubscriptionDBDao(datasource));
			gnizrDao.setForUserDao(new ForUserDBDao(datasource));
			gnizrDao.setTagAssertionDao(new TagAssertionDBDao(datasource));
			gnizrDao.setTagPropertyDao(new TagPropertyDBDao(datasource));
			gnizrDao.setFolderDao(new FolderDBDao(datasource));
			gnizrDao.setGeometryMarkerDao(new GeometryMarkerDBDao(datasource));
		}
		return gnizrDao;
	}
	
	/**
	 * Returns a DAO object for tag assertion operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public TagAssertionDao getTagAssertionDao() {
		return tagAssertionDao;
	}

	/**
	 * Sets the DAO object for tag assertion operations.
	 * 
	 * @param tagAssertionDao an instantiated DAO object
	 */
	public void setTagAssertionDao(TagAssertionDao tagAssertionDao) {
		this.tagAssertionDao = tagAssertionDao;
	}

	/**
	 * Returns a DAO object for tag property operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public TagPropertyDao getTagPropertyDao() {
		return tagPropertyDao;
	}

	/**
	 * Sets the DAO object for tag property operations.
	 * 
	 * @param tagPropertyDao an instantiated DAO object
	 */
	public void setTagPropertyDao(TagPropertyDao tagPropertyDao) {
		this.tagPropertyDao = tagPropertyDao;
	}

	public GnizrDao(){
		// no code;
	}

	/**
	 * Returns a DAO object for bookmark operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public BookmarkDao getBookmarkDao() {
		return bookmarkDao;
	}

	/**
	 * Sets the DAO object for bookmark operations.
	 * 
	 * @param bookmarkDao an instantiated DAO object
	 */
	public void setBookmarkDao(BookmarkDao bookmarkDao) {
		this.bookmarkDao = bookmarkDao;
	}

	/**
	 * Returns a DAO object for link operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public LinkDao getLinkDao() {
		return linkDao;
	}

	/**
	 * Sets the DAO object for link operations.
	 * 
	 * @param linkDao an instantiated DAO object
	 */
	public void setLinkDao(LinkDao linkDao) {
		this.linkDao = linkDao;
	}

	/**
	 * Returns a DAO object for tag operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public TagDao getTagDao() {
		return tagDao;
	}

	/**
	 * Sets the DAO object for tag operations.
	 * 
	 * @param tagDao an instantiated DAO object
	 */
	public void setTagDao(TagDao tagDao) {
		this.tagDao = tagDao;
	}

	/**
	 * Returns a DAO object for user operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public UserDao getUserDao() {
		return userDao;
	}

	/**
	 * Sets the DAO object for user operations.
	 * 
	 * @param userDao an instantiated DAO object
	 */
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * Returns a DAO object for feed subscription operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public FeedSubscriptionDao getFeedSubscriptionDao() {
		return feedSubscriptionDao;
	}

	/**
	 * Sets the DAO object for feed subscription operations.
	 * 
	 * @param feedSubscriptionDao an instantiated DAO object
	 */
	public void setFeedSubscriptionDao(FeedSubscriptionDao feedSubscriptionDao) {
		this.feedSubscriptionDao = feedSubscriptionDao;
	}
	
	/**
	 * Returns a DAO object for for-user operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public ForUserDao getForUserDao ()
	{
		return forUserDao;
	}
	
	/**
	 * Sets the DAO object for for-user operations.
	 * 
	 * @param forUserDao an instantiated DAO object
	 */
	public void setForUserDao (ForUserDao forUserDao) {
		this.forUserDao = forUserDao;
	}

	/**
	 * Returns a DAO object for folder operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public FolderDao getFolderDao() {
		return folderDao;
	}

	/**
	 * Sets the DAO object for folder operations.
	 * 
	 * @param folderDao an instantiated DAO object
	 */
	public void setFolderDao(FolderDao folderDao) {
		this.folderDao = folderDao;
	}

	/**
	 * Returns a DAO object for geometry marker operations. 
	 * 
	 * @return <code>null</code> if the associated DAO is undefined. 
	 */
	public GeometryMarkerDao getGeometryMarkerDao() {
		return geometryMarkerDao;
	}

	/**
	 * Sets the DAO object for geometry marker operations.
	 * 
	 * @param geometryMarkerDao an instantiated DAO object
	 */
	public void setGeometryMarkerDao(GeometryMarkerDao geometryMarkerDao) {
		this.geometryMarkerDao = geometryMarkerDao;
	}
}
