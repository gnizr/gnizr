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
package com.gnizr.core.feed;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchFeedSubscriptionException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NotAuthorizedException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.GnizrDao;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.folder.FolderDao;
import com.gnizr.db.dao.subscription.FeedSubscriptionDao;
import com.gnizr.db.dao.user.UserDao;

public class FeedSubscriptionManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5371897569885693660L;

	private static final Logger logger = Logger
			.getLogger(FeedSubscriptionManager.class.getName());
	
	private UserDao userDao;	
	private FolderDao folderDao;
	private FeedSubscriptionDao feedDao;
	private BookmarkManager bookmarkManager;

	public FeedSubscriptionManager(GnizrDao gnizrDao) {
		this.feedDao = gnizrDao.getFeedSubscriptionDao();
		this.userDao = gnizrDao.getUserDao();
		this.folderDao = gnizrDao.getFolderDao();		
		bookmarkManager = new BookmarkManager(gnizrDao);
	}

	public FeedSubscription getSubscription(int feedId){
		return feedDao.getSubscription(feedId);
	}
	
	public FeedSubscription getSubscription(User user, String feedUrl) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		return feedDao.getSubscription(user, feedUrl);
	}
	
	/**
	 * Creates a feed subscription entry. A <code>Bookmark</code> of <code>feedUrl</code>
	 * is automatically created under the ownership of <code>user</code>, if it doesn't 
	 * already exists. 
	 * 
	 * @param user the feed subscriber
	 * @param feedUrl the url of the feed
	 * @param feedTitle the title of the feed, which is used only if a <code>Bookmark</code>
	 * of <code>feedUrl</code> doesn't already exists under the ownership of <code>user</code>
	 * @return either an existing entry of the feed or a newly created entery.
	 * 
	 * @throws NoSuchUserException indicates <code>user</code> doesn't exist in the system
	 * @throws MissingIdException 
	 * @throws NotAuthorizedException 
	 * @throws RecordAlreadyExistsException 
	 */
	public FeedSubscription createSubscription(User user, String feedUrl, String feedTitle) throws NoSuchUserException, NotAuthorizedException, MissingIdException{
		if(feedUrl == null || feedTitle == null){
			throw new NullPointerException("either feedUrl or feedTitle is NULL");
		}
		GnizrDaoUtil.fillId(userDao, user);
		Bookmark feedBmark = new Bookmark(user,new Link(feedUrl));		
		feedBmark.setTitle(feedTitle);
		int bmId = bookmarkManager.addBookmark(feedBmark);
		if(bmId <= 0){
			logger.error("unable create bookmark: " + feedBmark);
		}else{
			feedBmark.setId(bmId);
		}
		FeedSubscription feedSub = new FeedSubscription();
		feedSub.setBookmark(feedBmark);
		feedSub.setAutoImport(false);
		feedSub.setMatchText("");
		feedSub.setLastSync(null);
		int feedId = feedDao.createSubscription(feedSub);
		if(feedId <= 0){
			logger.error("unable to create feed subscription: " + feedSub);
		}else{
			feedSub = feedDao.getSubscription(feedId);
		}
		return feedSub;		
	}
	
	public boolean deleteSubscription(User user, String feedUrl) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao,user);
		return feedDao.deleteSubscription(user, feedUrl);
	}
	
	public boolean updateSubscription(FeedSubscription feed){
		return feedDao.updateSubscription(feed);
	}
	
	public DaoResult<FeedSubscription> pageSubscription(User user, int offset, int count) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		return feedDao.pageSubscription(user, offset, count);
	}
	
	public DaoResult<FeedSubscription> listSubscription(User user) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		DaoResult<FeedSubscription> result = feedDao.pageSubscription(user,0,0);
		return feedDao.pageSubscription(user,0,result.getSize());
	}

	public List<FeedSubscription> listAutoImportSubscription(int ageHour) {
		return feedDao.listAutoImportSubscription(ageHour);
	}
	
	/**
	 * Defines a list of folders to be used for importing bookmarks from a feed. 
	 * If a feed subscription doesn't already exists for <code>feedUrl</code>,
	 * it will be created understand the ownership of <code>feedOwner</code> and 
	 * use <code>feedTitle</code> as the name of the feed. If any defined folder in
	 * <code>folders</code> doesn't already exist, it will be created understand the
	 * ownership of <code>folderOwner</code>. 
	 * 
	 * @param feedOwner the subscriber to <code>feedUrl</code>
	 * @param feedUrl the URL of the feed subcription
	 * @param feedTitle the name to be used as the title of the feed subscription 
	 * if it doesn't already exists
	 * @param folderOwner the owner of the defined import folders
	 * @param folders a list of import folder names
	 * @return the number of folders have been defined as the import folders for 
	 * <code>feedUrl</code>
	 * @throws NoSuchUserException indicates either <code>feedOwner</code> or 
	 * <code>folderOwner</code> doesn't exist in the system
	 * @throws MissingIdException 
	 * @throws NotAuthorizedException 
	 * @throws RecordAlreadyExistsException 
	 */
	public int addImportFolders(User feedOwner, String feedUrl, String feedTitle, User folderOwner, List<String> folders) throws NoSuchUserException, NotAuthorizedException, MissingIdException{
		GnizrDaoUtil.fillId(userDao,feedOwner);
		GnizrDaoUtil.fillId(userDao,folderOwner);
		FeedSubscription feedSub = null;
		if(feedTitle == null){
			feedSub = getSubscription(feedOwner, feedUrl);
		}else{
			feedSub = createSubscription(feedOwner, feedUrl, feedTitle);
		}
		List<Folder> folderImport = new ArrayList<Folder>();
		for(String folderName : folders){
			int fid = folderDao.createFolder(new Folder(folderName,folderOwner,"",GnizrDaoUtil.getNow()));
			folderImport.add(new Folder(fid));
		}
		return feedDao.addImportFolders(feedSub, folderImport);
	}
	
	public int removeImportFolders(User feedOwner, String feedUrl, User folderOwner, List<String> folders) throws NoSuchUserException, NoSuchFeedSubscriptionException{
		GnizrDaoUtil.fillId(userDao,feedOwner);
		GnizrDaoUtil.fillId(userDao,folderOwner);
		FeedSubscription feedSub = getSubscription(feedOwner, feedUrl);
		if(feedSub == null){
			throw new NoSuchFeedSubscriptionException("no such feed: " + feedUrl + " for user: " + feedOwner.getUsername());
		}
		List<Folder> folderImport = new ArrayList<Folder>();
		for(String folderName : folders){
			Folder f = folderDao.getFolder(folderOwner,folderName);
			if(f != null){
				folderImport.add(f);
			}
		}
		return feedDao.removeImportFolders(feedSub, folderImport);
	}
	
	public List<Folder> listImportFolder(User feedOwner, String feedUrl) throws NoSuchUserException, NoSuchFeedSubscriptionException{
		GnizrDaoUtil.fillId(userDao,feedOwner);
		FeedSubscription feedSub = getSubscription(feedOwner, feedUrl);
		if(feedSub == null){
			throw new NoSuchFeedSubscriptionException("no such feed: " + feedUrl + " for user: " + feedOwner.getUsername());
		}
		List<Folder> folders = new ArrayList<Folder>();
		folders.addAll(feedDao.listImportFolder(feedSub));
		return folders;		
	}	
	
}
