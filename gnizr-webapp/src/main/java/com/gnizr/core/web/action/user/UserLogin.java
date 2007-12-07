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
package com.gnizr.core.web.action.user;


import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.core.web.action.SessionConstants;
import com.gnizr.core.web.util.ServletUtilities;
import com.gnizr.db.dao.User;
import com.opensymphony.webwork.interceptor.SessionAware;

public class UserLogin extends AbstractAction implements SessionAware, LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5235147479224877299L;

	private static final Logger logger = Logger.getLogger(UserLogin.class.getName());
	
	private User user;

	private User loggedInUser;
	
	private UserManager userManager;
	
	private Map session;
	
	private boolean rememberMe;
	

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	private void doRememberMe(User userRecord){
		if(rememberMe == true){
			Cookie passCookie = createPassCookie(userRecord);
			HttpServletResponse res = getServletResponse();
			res.addCookie(passCookie);
		}else{
			ServletUtilities.deleteCookie(getServletResponse(),SessionConstants.REMEMBER_ME);
		}
	}
	
	private Cookie createPassCookie(User userRecord){
		StringBuffer passKey = new StringBuffer();
		passKey.append(userRecord.getUsername());
		passKey.append(":");
		passKey.append(userRecord.getPassword());
		
		Cookie passCookie = new Cookie(SessionConstants.REMEMBER_ME,passKey.toString());
		passCookie.setPath("/");
		passCookie.setMaxAge(ServletUtilities.SECONDS_PER_YEAR);
		return passCookie;		
	}
	
	public String doDefault(){	
		if(loggedInUser != null){
			user = loggedInUser;
			return SUCCESS;
		}
		return INPUT;
	}
	
	@SuppressWarnings("unchecked")
	protected String go() throws Exception {
		logger.debug("user="+user);		
		if(loggedInUser != null){
			user = loggedInUser;
			return SUCCESS;
		}else{
			return doLogin();
		}		
	}	
	
	@SuppressWarnings("unchecked")
	private String doLogin() throws Exception{
		User userRecord = null;
		if(user != null && userManager.isAuthorized(user)){			
			userRecord = userManager.getUser(user.getUsername());
			if(userRecord != null){
				session.put(SessionConstants.LOGGED_IN_USER,userRecord);
				doRememberMe(userRecord);
				return SUCCESS;
			}
		}else{
			addActionError("password incorrect or account does not exist");			
		}
		session.put(SessionConstants.LOGGED_IN_USER,null);
		return INPUT;
	}
	
	public void setSession(Map session) {
		this.session = session;		
	}
	
	public boolean isRememberMe() {
		return rememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.rememberMe = rememberMe;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

}
