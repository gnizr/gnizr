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
package com.gnizr.core.web.action;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.managers.UserManager;
import com.gnizr.db.dao.User;

public abstract class AbstractLoggedInUserAction extends AbstractAction implements LoggedInUserAware{

	protected User loggedInUser;
	protected String username;
	
	protected User user;

	protected UserManager userManager;
	
	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	protected void resolveUser() throws NoSuchUserException{
		if(isStrictLoggedInUserMode() == false){
			if(user == null){
				if(username != null && username.length() > 0){
					user = userManager.getUser(username);
				}else{
					user = loggedInUser;
				}
			}
		}else{
			user = loggedInUser;
		}
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	abstract protected boolean isStrictLoggedInUserMode();

	
}
