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
package com.gnizr.core.web.action.folder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;

public class ListUserFolders extends AbstractAction implements
		LoggedInUserAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4239420574088500210L;

	private static final Logger logger = Logger
			.getLogger(ListUserFolders.class);

	// action parameters
	private String username;

	private User loggedInUser;

	private String url;

	private int offset;

	private int count;

	// read-only data
	private User user;

	private List<Folder> folders;

	private int numberOfFolders;

	// data access object
	private FolderManager folderManager;

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Folder> getFolders() {
		return folders;
	}

	public User getUser() {
		return user;
	}

	private void resolveUser() {
		if (username != null) {
			user = new User(username);
		} else if (loggedInUser != null) {
			user = new User(loggedInUser);
		}
	}

	public String doListBookmarkFolders() throws Exception {
		logger.debug("doListBookmarkFolders");
		resolveUser();
		if(user == null){
			return LOGIN;
		}
		if (url != null) {
			try {
				if (getOffset() < 0) {
					setOffset(0);
				}
				if (getCount() <= 0) {
					int t = folderManager
							.pageContainedInFolder(user, url, 0, 0).getSize();
					setCount(t);
				}
				DaoResult<Folder> result = folderManager.pageContainedInFolder(
						user, url, getOffset(), getCount());
				numberOfFolders = result.getSize();
				folders = result.getResult();
			} catch (Exception e) {
				logger.debug(e);
				folders = new ArrayList<Folder>();
				numberOfFolders = 0;
			}
		}
		return SUCCESS;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("ListUserFolders.go()");
		resolveUser();
		try {
			if (getUser() == null) {
				logger.error("user is undefined");
				return INPUT;
			}
			if (getOffset() < 0) {
				setOffset(0);
			}
			if (getCount() <= 0) {
				int t = folderManager.pageUserFolders(user, 0, 0).getSize();
				setCount(t);
			}
			DaoResult<Folder> result = folderManager.pageUserFolders(user,
					getOffset(), getCount());
			numberOfFolders = result.getSize();
			folders = result.getResult();
		} catch (Exception e) {
			logger.debug(e);
			folders = new ArrayList<Folder>();
			numberOfFolders = 0;
		}
		return SUCCESS;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getNumberOfFolders() {
		return numberOfFolders;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
