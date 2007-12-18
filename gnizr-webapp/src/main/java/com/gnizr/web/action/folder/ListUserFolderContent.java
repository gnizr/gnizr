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
package com.gnizr.web.action.folder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractPagingAction;
import com.gnizr.web.action.LoggedInUserAware;

public class ListUserFolderContent extends AbstractPagingAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5840539687808914729L;

	private static final Logger logger = Logger
			.getLogger(ListUserFolderContent.class);

	// action parameters
	private String username;

	private String folderName;
	
	private String tag;

	private User loggedInUser;

	// read-only data
	private int folderSize = 0;

	private List<Bookmark> bookmarks = new ArrayList<Bookmark>();

	private Folder folder;

	private User user;

	// data access objects
	private FolderManager folderManager;

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public Folder getFolder() {
		return folder;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("ListUserFolderContent: go()");		
		resolveUser();
		if(user == null){
			return LOGIN;
		}
		resolveFolder();		
		if (getFolder() == null) {
			logger.debug("folder doesn't exist: folder="+getFolderName());
			return INPUT;
		}
		
		initPagingAction();		
		int offset = computeOffset(getPage());
		int ppc = getPerPageCount();
		
		DaoResult<Bookmark> result;
		if(tag != null){
			try{
				result = folderManager.pageFolderContent(user, folderName, tag, offset, ppc);
			}catch(Exception e){
				logger.debug("no such tag, user or folder: " + e);
				result = new DaoResult<Bookmark>(new ArrayList<Bookmark>(),0);
			}
		}else{
			result = folderManager.pageFolderContent(getUser(),getFolderName(), offset, ppc);	
		}
				
		// folder size == the size of paging result without using LIMIT
		folderSize = result.getSize(); 
		bookmarks = result.getResult();
		
		int maxNumOfPages = computeMaxPageNumber(ppc,folderSize);
		setTotalNumOfPages(maxNumOfPages);
		setPreviousPageNum(getPage(),maxNumOfPages);
		setNextPageNum(getPage(),maxNumOfPages);
		
		return SUCCESS;
	}

	

	private void resolveUser() {
		if (username != null) {
			user = new User(username);
		} else if (loggedInUser != null) {
			user = new User(loggedInUser);
		}
	}

	private void resolveFolder(){
		if(folder == null){
			if(folderName != null){
				try {
					folder = folderManager.getUserFolder(getUser(),folderName);
				} catch (NoSuchUserException e) {
					logger.debug(e);
				}
			}
		}
	}
	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public User getUser() {
		return user;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
