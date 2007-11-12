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
package com.gnizr.core.managers;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.GnizrDao;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.foruser.ForUserDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.user.UserDao;

/**
 * 
 */
public class ForUserManager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8867759728094086548L;

	private static final Logger logger = Logger.getLogger(ForUserManager.class
			.getName());

	private LinkDao linkDao;

	private BookmarkDao bookmarkDao;

	private ForUserDao forUserDao;

	private UserDao userDao;

	public ForUserManager(GnizrDao gnizrDao) {
		this.linkDao = gnizrDao.getLinkDao();
		this.forUserDao = gnizrDao.getForUserDao();
		this.userDao = gnizrDao.getUserDao();
		this.bookmarkDao = gnizrDao.getBookmarkDao();
	}

	public int addForUser(ForUser forUser) throws NoSuchUserException,
			NoSuchLinkException, MissingIdException, NoSuchBookmarkException {
		logger.debug("addForUser: forUser="+forUser);		
		GnizrDaoUtil.checkNull(forUser);
		GnizrDaoUtil.fillId(userDao, forUser.getForUser());
		GnizrDaoUtil.fillId(bookmarkDao, userDao, linkDao, forUser
				.getBookmark());
		int id = forUserDao.createForUser(forUser);
		logger.debug("forUserDao.addForUser() returns "+ id);
		return id;
	}
	
	/**
	 * @deprecated use <code>deleteForUserById</code> instead 
	 * @param forUser
	 * @return
	 * @throws MissingIdException
	 * @throws NullPointerException
	 * @throws IllegalArgumentException
	 */
	public boolean deleteForUser(ForUser forUser) throws MissingIdException,
			NullPointerException, IllegalArgumentException {
		if (forUser == null)
			throw new NullPointerException("ForUser is NULL");
		if (forUser.getId() <= 0)
			throw new MissingIdException("forUser is missing a valid ID");

		return forUserDao.deleteForUser(forUser.getId());
	}
	
	private void fillForUserObject(ForUser forUser) throws MissingIdException, NoSuchUserException, NoSuchLinkException, NoSuchBookmarkException{
		if(forUser == null) return;	
		GnizrDaoUtil.fillObject(forUserDao,bookmarkDao, userDao, linkDao,forUser);
	}
	
	public boolean hasForUser(Bookmark bookmark, User user)
			throws MissingIdException, NullPointerException {
		if (user == null)
			throw new NullPointerException("User is NULL");
		if (user.getId() < 0)
			throw new MissingIdException("User is missing valid Id");
		if (bookmark == null)
			throw new NullPointerException("Bookmark is NULL");
		if (bookmark.getId() < 0)
			throw new MissingIdException("Bookmark is missing valid Id");

		return forUserDao.hasForUser(bookmark, user);
	}
	
	public int getForUserCount(User user) throws NoSuchUserException{
		if(user != null){
			GnizrDaoUtil.fillId(userDao, user);
			return forUserDao.getForUserCount(user);
		}
		return 0;
	}
	
	public ForUser getForUser(int id) {
		ForUser forUserRec = forUserDao.getForUser(id);
		if(forUserRec != null){
			try {
				fillForUserObject(forUserRec);
				return forUserRec;
			} catch (Exception e) {
				logger.error(e);
			}
		}
		return null;
	}
	
	public DaoResult<ForUser> pageForUser(User user, User sender, int offset, int count) throws NoSuchUserException{
		DaoResult<ForUser> result = null;
		GnizrDaoUtil.fillId(userDao, user);
		GnizrDaoUtil.fillId(userDao, sender);
		result = forUserDao.pageForUser(user, sender, offset, count);
		return result;
	}
	
	public boolean deleteForUserById(User user, int[] ids) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao,user);
		return forUserDao.deleteForUser(user, ids);
	}
	
	public boolean deleteAllForUser(User user) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao,user);
		return forUserDao.deleteAllForUser(user);
	}
	
	public List<User> listForUserSenders(User recipient) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao,recipient);
		return forUserDao.listForUserSenders(recipient);
	}
	
}
