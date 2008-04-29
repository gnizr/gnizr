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
package com.gnizr.web.action.user;

import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.LoggedInUserAware;
import com.gnizr.web.action.SessionConstants;
import com.opensymphony.webwork.interceptor.SessionAware;

public class ChangeUserProfile extends AbstractAction implements LoggedInUserAware, SessionAware{

	
	private String password;
	
	private String passwordConfirm;
	
	private User loggedInUser;
	
	private UserManager userManager;
	
	private String email;
	
	private String fullname;
	
	@SuppressWarnings("unused")
	private Map session;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7253803491083898101L;
	private static final Logger logger = Logger.getLogger(ChangeUserProfile.class);

	@Override
	protected String go() throws Exception {
		return SUCCESS;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

	
	public String doUpdatePassword(){
		if(password.equals(passwordConfirm) == false){
			addActionMessage("The password values that you've entered are not the same.");
			return INPUT;
		}
		User profile = new User(loggedInUser);
		profile.setPassword(password);
		try {
			if(userManager.changePassword(profile) == false){
				addActionError("failed to change the user's password. user="+profile.getUsername());
				return ERROR;
			}			
			profile = userManager.getUser(profile.getUsername());
			if(profile != null){
				loggedInUser = profile;
				updateLoggedInUserObject();
				addActionMessage("Password changed successfully.");
			}else{
				addActionError("critical error occured after changing a user's profile.");
				return ERROR;
			}
		} catch (NoSuchUserException e) {
			addActionError("no such user: " + profile.getUsername());
			logger.error(e);
			return ERROR;
		}
		return SUCCESS;
		
	}
	
	@SuppressWarnings("unchecked")
	private void updateLoggedInUserObject(){
		session.put(SessionConstants.LOGGED_IN_USER,loggedInUser);
	}
	
	public String doUpdateProfile(){
		User profile = new User(loggedInUser);		
		if(email != null){
			profile.setEmail(email);
		}
		if(fullname != null){
			profile.setFullname(fullname);
		}
		profile.setPassword(null);
		try {
			if(userManager.changeProfile(profile) == false){
				addActionError("failed to change a user's profile. username="+profile.getUsername());
				return ERROR;
			}
			profile = userManager.getUser(profile.getUsername());
			if(profile != null){
				loggedInUser = profile;
				updateLoggedInUserObject();
				addActionMessage("Your profile has been successfully changed.");
			}else{
				addActionError("critical error occured after changing a user's profile.");
				return ERROR;
			}
		} catch (NoSuchUserException e) {
			addActionError("no such user: " + profile.getUsername());
			logger.error(e);
			return ERROR;
		}
		return SUCCESS;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setSession(Map session) {
		this.session = session;		
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}
	
}
