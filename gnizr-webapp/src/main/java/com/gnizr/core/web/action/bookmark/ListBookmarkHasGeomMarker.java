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

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.web.action.AbstractLoggedInUserAction;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.PointMarker;

public class ListBookmarkHasGeomMarker extends AbstractLoggedInUserAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4210030346402221407L;

	private static final int PER_PAGE_COUNT = 250;
	
	// read-only objects
	private List<? extends Bookmark> bookmarks;
	private List<Folder> userFolders;
	private int maxPageNumber;
	private int totalNumOfBookmark;
	
	// action parameters
	private String folder;
	private int page;

	
	private BookmarkManager bookmarkManager;
	private FolderManager folderManager;
	
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

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public List<? extends Bookmark> getBookmarks() {
		return bookmarks;
	}

	@Override
	protected String go() throws Exception {
		try{
			super.resolveUser();
		}catch(Exception e){
			return INPUT;
		}
		userFolders = getAllUserFolders();
		if(folder != null && folder.length() > 0){
			return doListBookmarksInFolder();
		}else{
			return doListBookmarksInArchive();
		}
	}
	
	private List<Folder> getAllUserFolders() throws NoSuchUserException {
		int count = 10;
		DaoResult<Folder> result = folderManager.pageUserFolders(getUser(),0, count);
		if(result.getSize()>10){
			count = result.getSize();
			result = folderManager.pageUserFolders(getUser(),0, count);
		}
		return result.getResult();
	}
	
	private int computeOffset(){
		if(page <= 0){
			return 0;
		}else{
			return (page - 1) * PER_PAGE_COUNT;
		}
	}
	
	private int computeMaxPageNumber(int numTotal){
		if(numTotal <= PER_PAGE_COUNT){
			return 1;
		}else{
			int tc = numTotal / PER_PAGE_COUNT;
			if((numTotal % PER_PAGE_COUNT) > 0){
				tc++;
			}
			return tc;
		}
	}
	
	public String doListBookmarksInFolder() throws Exception{
		try{
			Folder f = folderManager.getUserFolder(getUser(),getFolder());
			if(f == null){
				addActionError("no such folder: " + getFolder());
				return ERROR;
			}else{
				int offset = computeOffset();
				DaoResult<Bookmark> result = folderManager.pageBookmarkHasGeomMarker(f,offset,PER_PAGE_COUNT);
				bookmarks = result.getResult();
				totalNumOfBookmark = result.getSize();
				maxPageNumber = computeMaxPageNumber(totalNumOfBookmark);
			}
			return SUCCESS;
		}catch(Exception e){
			return ERROR;
		}	
	}
	
	public String doListBookmarksInArchive() throws Exception{
		try{
			int offset = computeOffset();
			DaoResult<Bookmark> result = bookmarkManager.pageBookmarkHasGeomMarker(getUser(),offset,PER_PAGE_COUNT);
			bookmarks = result.getResult();
			totalNumOfBookmark = result.getSize();
			maxPageNumber = computeMaxPageNumber(totalNumOfBookmark);				
			return SUCCESS;
		}catch(Exception e){
			return ERROR;
		}	
	}
	
	public String doListAllGBookmarks() throws Exception{
		try{
			super.resolveUser();
		}catch(Exception e){
			return INPUT;
		}
		try{
			//DaoResult<Bookmark> result = bookmarkManager.pageBookmarkHasGeomMarker(getUser(),0,0);
			//int maxSize = result.getSize();
			DaoResult<Bookmark> result = bookmarkManager.pageBookmarkHasGeomMarker(getUser(),0,200);
			bookmarks = result.getResult();
			totalNumOfBookmark = result.getSize();
			maxPageNumber = 1;
			List<GeoBookmark> geobmarks = new ArrayList<GeoBookmark>();
			for(Bookmark b : bookmarks){
				List<PointMarker> ptms = bookmarkManager.getPointMarkers(b);
				GeoBookmark aGeoBmark = new GeoBookmark(b);
				aGeoBmark.setPointMarkers(ptms);
				geobmarks.add(aGeoBmark);
			}		
			bookmarks = geobmarks;
			return SUCCESS;
		}catch(Exception e){
			return ERROR;
		}

	}
	
	@Override
	protected boolean isStrictLoggedInUserMode() {
		return false;
	}

	public List<Folder> getUserFolders() {
		return userFolders;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getMaxPageNumber() {
		return maxPageNumber;
	}

	public int getTotalNumOfBookmark() {
		return totalNumOfBookmark;
	}

	
}
