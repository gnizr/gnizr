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

import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.User;

public class UserBookmarkTimeline extends AbstractAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4244183540446606995L;
	private static final Logger logger = Logger.getLogger(UserBookmarkTimeline.class);
	private String username;
	private User loggedInUser;	
	private UserManager userManager;
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("UserBookmarkTimeline.go()");
		if(username != null){
			try{
				user = new User(username);
				user = userManager.getUser(user.getUsername());
			}catch(Exception e){
				logger.debug(e);
				return INPUT;
			}
		}else if(username == null && loggedInUser != null){
			user = new User(loggedInUser);
		}else{
			return LOGIN;
		}		
		return SUCCESS;
	}
	
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}
		
	
	


}
