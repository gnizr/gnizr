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

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;

public class EditUser extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5568453303140180078L;

	private static final Logger logger = Logger.getLogger(EditUser.class);

	private List<User> gnizrUsers;

	private String username;

	private User editUser;

	private UserManager userManager;
	
	private FolderManager folderManager;

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
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

	public List<User> getGnizrUsers() {
		return gnizrUsers;
	}

	@Override
	protected String go() throws Exception {
		return SUCCESS;
	}

	public String fetchEditData() {
		try {
			gnizrUsers = userManager.listUsers();
			return SUCCESS;
		} catch (Exception e) {
			logger.error("error fetching user account info", e);
		}
		return ERROR;
	}

	public String doChangePassword() {
		String op = INPUT;
		try{
			if(editUser.getPassword() != null){
				User u = userManager.getUser(editUser.getUsername());
				u.setPassword(editUser.getPassword());
				if(userManager.changePassword(u) == true){
					addActionMessage("Successfully changed user password!");
					op = SUCCESS;					
				}else{
					addActionError("unable to change password for user: " + editUser.getUsername());
					op = ERROR;
				}
			}
			editUser = userManager.getUser(editUser.getUsername());
		}catch(Exception e){
			addActionMessage("No such user: " + editUser.getUsername());
			op = INPUT;
		}		
		return op;
	}
	
	
	public String doUpdate() {
		String op = INPUT;
		try {
			if (editUser == null) {
				editUser = userManager.getUser(username);
				op = SUCCESS;
			} else {
				boolean isOkay = userManager.changeProfile(editUser);
				if(isOkay == true){
					addActionMessage("Successfully changed user profile");
					op = SUCCESS;
				}else{
					op = ERROR;
				}
			}
			editUser = userManager.getUser(editUser.getUsername());
		} catch (NoSuchUserException e) {
			logger.debug(e);
			addActionMessage("No such user: " + username);
			op = INPUT;
		}	
		return op;
	}

	public String doDelete() {
		String op = INPUT;
		if (username != null) {
			try {
				boolean isOkay = userManager.deleteUser(new User(username));
				if (isOkay == false) {
					addActionError("unable to delete user account: " + username);
					op = ERROR;
				} else {
					addActionMessage("Successfully deleted user account: "
							+ username);
					op = SUCCESS;
				}
			} catch (Exception e) {
				logger.debug(e);
				addActionMessage("No such user: " + username);
				op = INPUT;
			}
		}
		fetchEditData();
		return op;
	}

	public User getEditUser() {
		return editUser;
	}

	public void setEditUser(User editUser) {
		this.editUser = editUser;
	}

	
	public String doAddNewUser() {
		String op = INPUT;
		if(editUser != null){
			try{
				editUser.setCreatedOn(GnizrDaoUtil.getNow());
				boolean isOkay = userManager.createUser(editUser);
				if(isOkay == true){
					Folder myf = folderManager.createFolder(editUser,FolderManager.MY_BOOKMARKS_LABEL,"");
					if(myf == null){
						logger.error("Unable to create My Bookmarks folder");
						return ERROR;
					}
					op = SUCCESS;
					addActionMessage("Successfully added a new user account!");
				}else{
					addActionError("Unable to create a new user account");
					logger.error("unable to create a new user account: user=" + editUser);
					op = ERROR;
				}
			}catch(Exception e){
				addActionMessage("An user account of the same username already exists.");
				op = INPUT;
			}
		}
		return op;
	}
}
