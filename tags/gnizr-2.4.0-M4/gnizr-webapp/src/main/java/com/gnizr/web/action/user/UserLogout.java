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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.SessionConstants;
import com.gnizr.web.util.ServletUtilities;
import com.opensymphony.webwork.interceptor.SessionAware;

public class UserLogout extends AbstractAction implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4156151960134815199L;
	private static final Logger logger = Logger.getLogger(UserLogout.class.getName());
	private Map session;
	
	@Override
	protected String go() throws Exception {
		if(session == null){
			logger.error("session is NULL");
			return ERROR;
		}
		if(session.containsKey(SessionConstants.LOGGED_IN_USER)){
			if(logger.isEnabledFor(Level.DEBUG)){
				User user = (User)session.get(SessionConstants.LOGGED_IN_USER);
				logger.debug("signing out user: "+ user);
			}
			session.remove(SessionConstants.LOGGED_IN_USER);
		}		
		session.clear();
		// on logout, delete the cookie that serves the "remember me" function
		ServletUtilities.deleteCookie(getServletResponse(),SessionConstants.REMEMBER_ME);
		return SUCCESS;
	}

	public void setSession(Map session) {
		this.session = session;		
	}

	
	
}
