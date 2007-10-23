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
package com.gnizr.core.web.action.bookmark;

import org.apache.log4j.Logger;

import com.gnizr.core.managers.ForUserManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.User;

public class EditForUser extends AbstractAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6120918127228734296L;
	private static final Logger logger = Logger.getLogger(EditForUser.class);
	
	public static final String DELETE_FOR_USER_DB_ERROR = "DELETE_FOR_USER_DB_ERROR";
	
	
	private ForUserManager forUserManager;
	private User loggedInUser;
	private int[] deleteForUserId;
	
	@Override
	protected String go() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String doPurgeForUser() throws Exception{
		logger.debug("doPurgeForUser called");
		if(loggedInUser == null){
			logger.debug("Missing defined loggedInUser. Return INPUT.");
			return INPUT;
		}
		if(forUserManager.getForUserCount(loggedInUser) > 0){
			boolean isOkay = forUserManager.deleteAllForUser(loggedInUser);
			if(isOkay == false){
				addActionError(DELETE_FOR_USER_DB_ERROR);
				logger.debug("deleteForUser op returns false");
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	public String doDeleteForUser() throws Exception{
		logger.debug("doDeleteForUser");
		if(loggedInUser == null){
			logger.debug("Missing defined loggedInUser. Return INPUT.");
			return INPUT;
		}
		if(forUserManager.getForUserCount(loggedInUser) > 0 && deleteForUserId.length > 0){
			boolean isOkay = forUserManager.deleteForUserById(loggedInUser,deleteForUserId);
			if(isOkay == false){
				addActionError(DELETE_FOR_USER_DB_ERROR);
				logger.debug("deleteForUserById op returns false");
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	public User getLoggedInUser() {
		return loggedInUser;
	}


	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}


	public ForUserManager getForUserManager() {
		return forUserManager;
	}


	public void setForUserManager(ForUserManager forUserManager) {
		this.forUserManager = forUserManager;
	}

	public int[] getDeleteForUserId() {
		return deleteForUserId;
	}


	public void setDeleteForUserId(int[] deleteForUserId) {
		this.deleteForUserId = deleteForUserId;
	}
	
}
