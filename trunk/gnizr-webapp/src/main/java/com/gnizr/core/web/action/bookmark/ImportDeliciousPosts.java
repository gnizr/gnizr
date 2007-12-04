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

import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.delicious.DeliciousImport;
import com.gnizr.core.delicious.ImportStatus;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.User;

import del.icio.us.DeliciousException;
import del.icio.us.DeliciousNotAuthorizedException;

public class ImportDeliciousPosts extends AbstractAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4013326528318622669L;
	private static final Logger logger = Logger.getLogger(Logger.class);
	
	private String deliciousUsername;
	private String deliciousPassword;
	private User loggedInUser;
	private UserManager userManager;
	private BookmarkManager bookmarkManager;
	private FolderManager folderManager;

	@SuppressWarnings("unused")
	private Map session;
	private ImportStatus status;
	
	public ImportStatus getStatus() {
		return status;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String go() throws Exception {
		DeliciousImport importService = new DeliciousImport(deliciousUsername,deliciousPassword,
				loggedInUser,userManager,bookmarkManager,folderManager,false);
		try{
			status = importService.doImport();
		}catch(DeliciousNotAuthorizedException e){
			addActionMessage("del.icio.us authorization error. either your username or password is incorrect.");
			return INPUT;
		}catch(DeliciousException e){
			addActionMessage("del.icio.us web service is currently busy. try again later.");
			return INPUT;
		}catch(Exception e){
			logger.error(e);
			addActionError(e.toString());
			return ERROR;
		}	
		return SUCCESS;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setDeliciousPassword(String deliciousPassword) {
		this.deliciousPassword = deliciousPassword;
	}

	public void setDeliciousUsername(String deliciousUsername) {
		this.deliciousUsername = deliciousUsername;
	}

	public void setSession(Map session) {
		this.session = session;		
	}

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public String getDeliciousPassword() {
		return deliciousPassword;
	}

	public String getDeliciousUsername() {
		return deliciousUsername;
	}

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}
	
	

}
