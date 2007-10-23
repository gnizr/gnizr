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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.User;

public class ManageBookmarks extends AbstractAction implements LoggedInUserAware{

	private static final long serialVersionUID = -5898863541246318604L;
	private static final Logger logger = Logger.getLogger(ManageBookmarks.class);
	
	public static final String OP_DELETE_BOOKMARKS = "delete";
	public static final String OP_ADD_TO_FOLDER = "addFolder";
	public static final String OP_REMOVE_FROM_FOLDER= "removeFolder";
	public static final String OP_ADD_TAG = "addTag";
	public static final String OP_REMOVE_TAG = "removeTag";
	
	// action parameter
	private int[] bookmarkId;
	private String[] op;
	private String tag;
	private String folder;
	private User loggedInUser;
	
	// data access object
	private BookmarkManager bookmarkManager;
	private FolderManager folderManager;
	
	
	public int[] getBookmarkId() {
		return bookmarkId;
	}

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public void setBookmarkId(int[] bookmarkId) {
		this.bookmarkId = bookmarkId;
	}
	
	@Override
	protected String go() throws Exception {		
		logger.debug("ManageBookmarks: op="+getOp());
		String aOp = "";
		if(getOp() != null && getOp().length>0){
			aOp = getOp()[0];
		}
		if(OP_DELETE_BOOKMARKS.equalsIgnoreCase(aOp)){
			return doDeleteBookmarks();
		}else if(OP_REMOVE_FROM_FOLDER.equalsIgnoreCase(aOp)){
			return doRemoveFromFolder();
		}else if(OP_ADD_TO_FOLDER.equalsIgnoreCase(aOp)){
			return doAddToFolder();
		}
		return INPUT;
	}
	
	private List<Bookmark> createBookmarkObjects(boolean instantiateId){
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		if(bookmarkId != null){
			for(int id : bookmarkId){
				if(instantiateId == true){
					bmarks.add(bookmarkManager.getBookmark(id));
				}else{
					bmarks.add(new Bookmark(id));
				}
			}
		}
		return bmarks;
	}
	
	public String doDeleteBookmarks() throws Exception {
		List<Bookmark> delBmarks = createBookmarkObjects(true);			
		for (Bookmark bmark : delBmarks) {
			try {
				if(bmark.getUser().getId() == getLoggedInUser().getId()){
					boolean okay = bookmarkManager.deleteBookmark(bmark);
					if (okay == false) {
						addActionError("Unable to delete bookmark: "
							+ bmark.getLink().getUrl());
						return ERROR;
					}
				}
			} catch (Exception e) {
				addActionError("Error deleting bookmarks");
				return ERROR;
			}
		}		
		return SUCCESS;
	}

	public String doAddToFolder() throws Exception{
		List<Bookmark> addBmarks = createBookmarkObjects(false);
		try{
			if(getFolder() != null){
				folderManager.addBookmarks(getLoggedInUser(),getFolder(),addBmarks);
			}
		}catch(Exception e){
			addActionError("Error adding bookmarks to a folder");
			logger.error("add bookmarks to folder:" + getFolder(),e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String doAddToMyBookmarks() throws Exception{
		List<Bookmark> addBmarks = createBookmarkObjects(false);
		try{		
			folderManager.addBookmarks(getLoggedInUser(),FolderManager.MY_BOOKMARKS_LABEL,addBmarks);			
		}catch(Exception e){
			addActionError("Error adding bookmarks to a folder");
			logger.error("add bookmarks to folder:" + FolderManager.MY_BOOKMARKS_LABEL,e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String doRemoveFromMyBookmarks() throws Exception{
		List<Bookmark> delBmarks = createBookmarkObjects(false);
		try{		
			folderManager.removeBookmarks(getLoggedInUser(),FolderManager.MY_BOOKMARKS_LABEL,delBmarks);			
		}catch(Exception e){
			addActionError("Error removing bookmarks from a folder" );
			logger.error("remove bookmarks from folder:" + FolderManager.MY_BOOKMARKS_LABEL,e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String doAddToMyRssImported() throws Exception{
		List<Bookmark> addBmarks = createBookmarkObjects(false);
		try{		
			folderManager.addBookmarks(getLoggedInUser(),FolderManager.IMPORTED_BOOKMARKS_LABEL,addBmarks);			
		}catch(Exception e){
			addActionError("Error adding bookmarks to a folder");
			logger.error("add bookmarks to folder:" + FolderManager.IMPORTED_BOOKMARKS_LABEL,e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String doRemoveFromMyRssImported() throws Exception{
		List<Bookmark> delBmarks = createBookmarkObjects(false);
		try{		
			folderManager.removeBookmarks(getLoggedInUser(),FolderManager.IMPORTED_BOOKMARKS_LABEL,delBmarks);			
		}catch(Exception e){
			addActionError("Error removing bookmarks from a folder" );
			logger.error("remove bookmarks from folder:" + FolderManager.MY_BOOKMARKS_LABEL,e);
			return ERROR;
		}
		return SUCCESS;
	}
	
	public String doRemoveFromFolder() throws Exception{
		List<Bookmark> rmBmarks = createBookmarkObjects(false);
		try{	
			if(getFolder() != null){
				folderManager.removeBookmarks(getLoggedInUser(),getFolder(),rmBmarks);
			}
		}catch(Exception e){
			addActionError("Error removing bookmarks from a folder");
			logger.error("remove bookmarks from folder:" + getFolder(),e);
			return ERROR;
		}
		return SUCCESS;
	}
	
		
	
	public String[] getOp() {
		return op;
	}

	public void setOp(String[] op) {
		this.op = op;
	}

	public void setLoggedInUser(User loggedInUser) {
		this.loggedInUser = loggedInUser;
	}

	public User getLoggedInUser(){
		return this.loggedInUser;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

}
