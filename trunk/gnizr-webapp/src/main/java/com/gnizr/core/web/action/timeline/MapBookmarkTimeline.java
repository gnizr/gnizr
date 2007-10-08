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
package com.gnizr.core.web.action.timeline;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class MapBookmarkTimeline extends ActionSupport implements LoggedInUserAware{

	private static final Logger logger = Logger.getLogger(MapBookmarkTimeline.class);
	private User user;
	private User loggedInUser;
	private UserManager userManager;
	private BookmarkPager bookmarkPager;
	private int perPageCount;
	private int totalPageNumber;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -628051056679303400L;

	/**
	 * @throws NoSuchUserException 
	 * 
	 */
	public String execute() throws NoSuchUserException{
		logger.debug("MapBookmarkTimeline.execute()");
		if(user == null){
			if(loggedInUser != null){
				logger.debug("no user defined. use loggedInUser as user");
				user = new User(loggedInUser);
			}else{
				logger.debug("both user and loggedInUser is NULL. return INPUT");
				return INPUT;
			}
		}
		if(user.getId() <= 0){
			if(user.getUsername() != null){		
				try{
					user = userManager.getUser(user.getUsername());
				}catch(Exception e){
					logger.debug("no such user: "+user+". return INPUT");
					return INPUT;
				}
			}else{
				logger.error("error: user.id <= 0 and no defined user.username.");
				return INPUT;
			}
		}
		
		totalPageNumber = bookmarkPager.getMaxPageNumber(user,getPerPageCount());
		
		return SUCCESS;
	}

	/**
	 * Set the currently logged in user as the user to be associated with
	 * this action. Either
	 * <code>user.getId()</code> or <code>user.getUsername()</code> must be fully
	 * instantiated. 
	 */
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;
	}

	/**
	 * Returns the user whose bookmarks will be mapped onto the timeline.
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * Sets the user whose bookmarks will be mapped onto the timeline. Either
	 * <code>user.getId()</code> or <code>user.getUsername()</code> must be fully
	 * instantiated.
	 * 
	 * @param user a user object.
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the userManager
	 */
	public UserManager getUserManager() {
		return userManager;
	}

	/**
	 * @return the loggedInUser
	 */
	public User getLoggedInUser() {
		return loggedInUser;
	}

	/**
	 * @param userManager the userManager to set
	 */
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	/**
	 * @return the bookmarkPager
	 */
	public BookmarkPager getBookmarkPager() {
		return bookmarkPager;
	}

	/**
	 * @param bookmarkPager the bookmarkPager to set
	 */
	public void setBookmarkPager(BookmarkPager bookmarkPager) {
		this.bookmarkPager = bookmarkPager;
	}

	/**
	 * @return the perPageCount
	 */
	public int getPerPageCount() {
		return perPageCount;
	}

	/**
	 * @param perPageCount the perPageCount to set
	 */
	public void setPerPageCount(int perPageCount) {
		this.perPageCount = perPageCount;
	}

	/**
	 * @return the totalPageNumber
	 */
	public int getTotalPageNumber() {
		return totalPageNumber;
	}

	/**
	 * @param totalPageNumber the totalPageNumber to set
	 */
	public void setTotalPageNumber(int totalPageNumber) {
		this.totalPageNumber = totalPageNumber;
	}

}
