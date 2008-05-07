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

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.util.FormatUtil;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.LoggedInUserAware;

public class EditUserFolder extends AbstractAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2569728945542779993L;
	private static final Logger logger = Logger.getLogger(EditUserFolder.class);
	
	public static final String DUPLICATED_FOLDER_NAME = "DUPLICATED_FOLDER_NAME";
	public static final String CHAR_NOT_ALLOWED = "CHAR_NOT_ALLOWED";
	public static final String DEL_FOLDER_FAILED = "DEL_FOLDER_FAILED";
	public static final String PURGE_OKAY = "PURGE_OKAY";
	
	// action parameter
	private User loggedInUser;
	private String url; // bookmark/link url to be remove from this folder (folderName)
	private String owner; // owner of the bookmark (optional). if not defined, loggerInUser
	                      // is assumed to be the owner.
	private String folderName;
	private Folder folder;
	
	// data access object
	private FolderManager folderManager;
	private BookmarkManager bookmarkManager;

	@Override
	protected String go() throws Exception {
		if(folderName != null && folderName.length() > 0){			
			if(GnizrDaoUtil.isLegalFolderName(folderName)){
				folder = folderManager.createFolder(loggedInUser,folderName,"");
			}else{
				addActionMessage(CHAR_NOT_ALLOWED);
				return INPUT;
			}
		}else{
			return INPUT;
		}
		return SUCCESS;
	}
	
	public String doCreateMyBookmarksFolder() throws Exception {
		logger.debug("EditUserFolder: doCreateMyBookmarksFolder for user = " + getLoggedInUser());
		setFolderName(FolderManager.MY_BOOKMARKS_LABEL);
		return go();
	}
	
	public String doUpdateFolder() {
		Folder existingFolder = null;
		if(folderName != null && folderName.length() > 0){
			try{
				existingFolder = folderManager.getUserFolder(loggedInUser,folderName);
			}catch(Exception e){			
				logger.debug("no exisitng folder="+folderName+",loggedInUser="+loggedInUser);
				return ERROR;
			}
		}
		boolean isOkay = false;
		if(existingFolder != null && folder != null){
			String fname = folder.getName().trim();
			if(fname.length() > 0){
				if(GnizrDaoUtil.isLegalFolderName(fname)){
					existingFolder.setName(folder.getName());
				}else{
					addActionMessage(CHAR_NOT_ALLOWED);
					return INPUT;
				}
			}
			existingFolder.setDescription(folder.getDescription());
			try{
				isOkay = folderManager.updateFolder(loggedInUser,existingFolder);
			}catch(Exception e){
				logger.debug("unable to update folder:" + existingFolder);
				return ERROR;
			}
		}
		if(isOkay == false){
			addActionMessage(DUPLICATED_FOLDER_NAME);
			return INPUT;
		}
		return SUCCESS;
	}

	public String doDeleteFolder() throws Exception{
		boolean isOkay = false;
		if(folderName != null){
			isOkay = folderManager.deleteUserFolder(loggedInUser, folderName);
		}
		if(isOkay == false){
			addActionMessage(DEL_FOLDER_FAILED);
			return INPUT;
		}
		return SUCCESS;
	}
	
	public String doRemoveBookmark() throws Exception{		
		int bmId = -1;
		User bmarkOwner = loggedInUser;
		if(owner != null){
			bmarkOwner = new User(owner);
		}		
		if(getUrl() != null){
			try{
				bmId = bookmarkManager.getBookmarkId(bmarkOwner, getUrl());
			}catch(Exception e){
				logger.debug(e);
			}
		}
		if(bmId > 0 && getFolderName() != null){			
			List<Bookmark> bmarks2remove = new ArrayList<Bookmark>();		
			bmarks2remove.add(new Bookmark(bmId));
			folderManager.removeBookmarks(loggedInUser,getFolderName(), bmarks2remove);
		}		
	
		return SUCCESS;
	}
	
	public String doPurgeFolder() throws Exception{		
		int numPurged = 0;
		if(folderName != null){
			folder = folderManager.getUserFolder(loggedInUser, folderName);
			if(folder != null){
				numPurged = folderManager.purgeFolder(loggedInUser, folderName);
				if(numPurged != folder.getSize()){
					logger.error("total number of bookmarks purged doesn't equals to folder size.");
					return ERROR;
				}else{
					folder.setSize(0);
				}
				addActionMessage(PURGE_OKAY);
				return SUCCESS;
			}
		}
		return INPUT;
	}

	
	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;		
	}

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Folder getFolder() {
		return folder;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	/**
	 * Sets the folder to edit. Cleans the description text of
	 * this folder description -- remove all HTML tags. 
	 * 
	 * @param folder an instance of <code>Folder</code> to be edited.
	 */
	public void setFolder(Folder folder) {
		this.folder = folder;		
		if(folder != null && folder.getDescription() != null){
			String tmpStr = folder.getDescription();
			tmpStr = FormatUtil.tidyAndExtractTextFromHtml(tmpStr);			
			this.folder.setDescription(tmpStr);
		}
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

}
