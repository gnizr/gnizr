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
import com.gnizr.core.util.TokenManager;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.SessionConstants;
import com.opensymphony.webwork.interceptor.SessionAware;

/**
 * An action implementation for verifying the email address ownership of
 * a new user account. If the input token (used for verification purpose)
 * is valid, then the user account's status will be changed to 
 * <code>AccountStatus.ACTIVE</code> 
 * 
 * @author Harry Chen
 * @since 2.4
 *
 */
public class ActivateUserAccount extends AbstractAction implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8096046803875789314L;
	private static final Logger logger = Logger.getLogger(ActivateUserAccount.class);
	
	private TokenManager tokenManager;
	private UserManager userManager;
	
	private String username;
	private String token;
	
	private Map<String, Object> session;
	
	@Override
	protected String go() throws Exception {
		logger.debug("ActiveUserAccount: username = " + username + ", token = " + token);
		if(token == null || username == null){
			return INPUT;
		}
		
		User user = null;
		try{
			user = userManager.getUser(username);			
		}catch(NoSuchUserException e){
			logger.debug("ActivateUserAccount: " + e);
			return INPUT;
		}
		try{
			if(tokenManager.isValidResetToken(token, user) == true){
				if(tokenManager.deleteToken(token, user) == true){
					if(userManager.activateUserAccount(user) == true){
						logger.debug("Activated account for " + user.getUsername());
						if(session != null){
							user = userManager.getUser(user.getUsername());
							session.put(SessionConstants.LOGGED_IN_USER,user);
							return SUCCESS;
						}else{
							logger.error("ActivateUserAccount: Missing session object is NULL");
							return ERROR;
						}					
					}else{
						logger.error("Unable activate user account!");
					}
				}else{
					logger.error("Unable delete token");
				}
			}else{
				if(logger.isDebugEnabled()){
					logger.debug("Invalid token: " + token);
					logger.debug("List of tokens: " + tokenManager.listOpenTickets().toString());
				}
			}
		}catch(Exception e){
			logger.debug("ActivateUserAccount: username = " 
					+ username + ", token = " + token + e);
			return ERROR;
		}		
		return INPUT;
	}
	
	public TokenManager getTokenManager() {
		return tokenManager;
	}

	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@SuppressWarnings("unchecked")
	public void setSession(Map session) {
		this.session = session;		
	}



}
