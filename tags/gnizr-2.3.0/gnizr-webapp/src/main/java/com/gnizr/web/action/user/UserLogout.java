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

import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.SessionConstants;
import com.gnizr.web.util.ServletUtilities;
import com.opensymphony.webwork.interceptor.SessionAware;

/**
 * This class implements the user logout function. 
 * 
 * @author Harry Chen
 * @since 2.2
 */
public class UserLogout extends AbstractAction implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4156151960134815199L;
	private static final Logger logger = Logger.getLogger(UserLogout.class.getName());
	@SuppressWarnings("unchecked")
	private Map session;
	
	/**
	 * Clears the content of the current session, including the user authentication object. 
	 * If there is a "Remember Me" cookie set on the client browser, this action will attempt
	 * to delete that cookie. 
	 * @return returns <code>SUCCESS</code> if the user can be successfully logged out. 
	 */
	@Override
	protected String go() throws Exception {
		if(session == null){
			logger.error("session is NULL");
			return ERROR;
		}
		User user = (User)session.get(SessionConstants.LOGGED_IN_USER);
		if(user != null){
			logger.debug("signing out user: "+ user.getUsername());
		}
		try{
			session.clear();
		}catch(Exception e){
			logger.debug("Unable to clear user session. Exception: " + e);
		}
		try{
			// on logout, delete the cookie that serves the "remember me" function
			ServletUtilities.deleteCookie(getServletResponse(),SessionConstants.REMEMBER_ME);
		}catch(Exception e){
			logger.error("Error occured when trying to delete the REMEMBER_ME cookie. Exception: " + e);
		}
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public void setSession(Map session) {
		this.session = session;		
	}
}
