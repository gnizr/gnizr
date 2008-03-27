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

import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.UsernameTakenException;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.SessionConstants;
import com.opensymphony.webwork.interceptor.SessionAware;

/**
 * @author Torey
 *
 */
public class Registration extends AbstractAction implements SessionAware{


	/**
	 * 
	 */
	private static final long serialVersionUID = 3116256941098182762L;

	private static final Logger logger = Logger.getLogger(Registration.class.getName());

	private User user;
	private UserManager userManager;
	private Map session;
	
	private String checkPassword;
	
	public String getCheckPassword() {
		return checkPassword;
	}

	public void setCheckPassword(String checkPassword) {
		this.checkPassword = checkPassword;
	}
	
	private boolean isOpenRegistrationPolicy(){
		String policy = (String)session.get(SessionConstants.REGISTRATION_POLICY);
		if(policy != null){
			if(policy.equalsIgnoreCase("open")){
				return true;
			}
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see com.gnizr.web.action.AbstractAction#go()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected String go() throws Exception {	
		logger.debug("user="+user);
		if(isOpenRegistrationPolicy() == false){
			addActionError("Sorry! New user registration is currently closed.");
			return ERROR;
		}
		if(user == null){			
			return ERROR;
		}
		if (userManager.hasUser(user)) {
			addActionError ("Username '" + user.getUsername() + "' already exists.");
			return INPUT;
		} else {
			if(user.getPassword().equals(getCheckPassword()) == false){
				addActionError("'confirmed password' doesn't match 'password'.");
				return INPUT;
			}			
			user.setAccountStatus(0);
			user.setCreatedOn(new Date());
			try {
				if(userManager.createUser(user)){		
					User newUser = userManager.getUser(user.getUsername());
					if(newUser != null){
						session.put(SessionConstants.LOGGED_IN_USER, newUser);						
						return SUCCESS;
					}
				}
				addActionError("Failed to user a new user account.");
				return ERROR;				
			} catch (UsernameTakenException ute) {
				addActionError ("Username '" + user.getUsername() + "' already exists.");				
			} catch (Exception e) {
				addActionError ("Database error. Please contact administrator.");				
			}
		}		
		return INPUT;
	}

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

	public void setSession(Map session) {
		this.session = session;		
	}
}
