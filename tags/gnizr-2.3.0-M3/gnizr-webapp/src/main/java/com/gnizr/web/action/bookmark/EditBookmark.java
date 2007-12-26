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
package com.gnizr.web.action.bookmark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.link.LinkManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.LoggedInUserAware;

public class EditBookmark extends AbstractAction implements LoggedInUserAware {

	private static final long serialVersionUID = -2523321948850517639L;

	private static final Logger logger = Logger.getLogger(EditBookmark.class);
	
	// action paramaters
	private int id;	
	private boolean redirect;
	private boolean saveAndClose;
	private String title;
	private String notes;
	private String url;
	private String tags;	
	private String oldUrl;		
	private User loggedInUser;
	private Bookmark editBookmark;
	private PointMarker[] pointMarkers;	
	private String saveAndContinueEdit;
	
	// data access objects
	private BookmarkManager bookmarkManager;
	private LinkManager linkManager;
	private FolderManager folderManager;	


	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public boolean isRedirect() {
		return redirect;
	}

	public void setRedirect(boolean redirect) {
		this.redirect = redirect;
	}

	public String getTags() {
		return tags;
	}

	public void setTags(String tags) {
		this.tags = tags;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public String doFetchBookmark() {
		logger.debug("doFetchBookmark: url=" + url);
		if (url != null) {
			try {
				int bmid = bookmarkManager.getBookmarkId(loggedInUser, url);
				if (bmid > 0) {
					editBookmark = bookmarkManager.getBookmark(bmid);
				}else {
					if(getTitle() == null){
						setTitle(guessLinkTitle(url));
					}
					editBookmark = new Bookmark();					
					editBookmark.setTags(getTags());				
					editBookmark.setLink(new Link(getUrl()));
					String st = GnizrDaoUtil.stripNonPrintableChar(getTitle());
					editBookmark.setTitle(st);	
					String sn = GnizrDaoUtil.stripNonPrintableChar(getNotes());
					editBookmark.setNotes(sn);
				}				
				fetchGeometryMarkers(editBookmark);
			} catch (Exception e) {
				logger.error(e);
				return ERROR;
			}
			return SUCCESS;
		}else{
			return INPUT;
		}
	}
	
	public String doFetchGeometryMarkersById() throws Exception{
		Bookmark bm = null;
		if(id > 0){
			bm = new Bookmark(id);
			if(fetchGeometryMarkers(bm) == true){
				return SUCCESS;
			}else{
				return ERROR;
			}
		}
		return INPUT;
	}
	
	private boolean fetchGeometryMarkers(Bookmark bm){
		try{
			if(bm != null && bm.getId() > 0){
				pointMarkers = bookmarkManager.getPointMarkers(bm).toArray(new PointMarker[0]);
			}else{
				pointMarkers = new PointMarker[0];
			}
			return true;
		}catch(Exception e){
			logger.error("doFetchGeometryMarkers failed on bm="+bm,e);
		}
		return false;
	}

	private boolean doUpdateGeometryMarkers(Bookmark bm){
		logger.debug("doUpdateGeometryMarkers: bm="+bm);
		try{
			List<Integer> addedPtMarkerIds = new ArrayList<Integer>();			
			List<PointMarker> rmvPtMarkers = new ArrayList<PointMarker>();
			List<PointMarker> curPtMarkers = bookmarkManager.getPointMarkers(bm);
			if(pointMarkers != null){
				if(pointMarkers.length > 0){
					List<PointMarker>  addedPt = bookmarkManager.addPointMarkers(bm, Arrays.asList(pointMarkers));			
					for(PointMarker pt : addedPt){
						addedPtMarkerIds.add(pt.getId());
					}
					pointMarkers = addedPt.toArray(new PointMarker[0]);
				}				
			}	
			for(PointMarker cpt: curPtMarkers){
				if(addedPtMarkerIds.contains(cpt.getId()) == false){
					rmvPtMarkers.add(cpt);
				}
			}
			if(rmvPtMarkers.isEmpty() == false){
				bookmarkManager.removePointMarkers(bm,rmvPtMarkers);
			}
			return true;
		}catch(Exception e){
			logger.error("doUpdateGeometryMarkers failed on bm="+bm,e);
		}
		return false;
	}
	
	private String guessLinkTitle(String url){	
		Bookmark bmark = linkManager.getFirstMatchedBookmark(url);
		if(bmark != null && bmark.getTitle() != null){
			return bmark.getTitle();
		}
		return null;
	}
	
	public String doDeleteBookmark() {
		logger.debug("doDeleteBookmark: url=" + url);
		String result = doFetchBookmark();
		if (result == SUCCESS) {
			if (editBookmark != null && editBookmark.getId() > 0) {
				try {
					if (bookmarkManager.deleteBookmark(editBookmark) == false) {
						addActionError("Unable to delete bookmark: " + url);
						return ERROR;
					}
				} catch (Exception e) {
					addActionError("Unable to delete bookmark: " + url);
					logger.error(e);
					return ERROR;
				}
			}
		}
		return result;
	}

	private String updateBookmark() {
		editBookmark = bookmarkManager.getBookmark(getId());
		if (editBookmark != null) {
			User author = editBookmark.getUser();
			if (author.getId() != loggedInUser.getId()) {
				addActionMessage("You don't have the permission to edit this bookmark!");
				return INPUT;
			} else {
				if(getTags() != null){
					editBookmark.setTags(getTags());
				}else{
					editBookmark.setTags("");
				}		
				
				editBookmark.setLastUpdated(GnizrDaoUtil.getNow());
				editBookmark.setLink(new Link(getUrl()));
				
				String st = GnizrDaoUtil.stripNonPrintableChar(getTitle());
				editBookmark.setTitle(st);	
				
				String sn = GnizrDaoUtil.stripNonPrintableChar(getNotes());
				editBookmark.setNotes(sn);	
				try {
					boolean isOkay = bookmarkManager
							.updateBookmark(editBookmark);
					if (isOkay == false) {
						return ERROR;
					}		
					editBookmark = bookmarkManager.getBookmark(editBookmark.getId());
				} catch (Exception e) {
					logger.error(e);
					return ERROR;
				}
			}
		}
		return SUCCESS;
	}
	
	private String addBookmark() {
		editBookmark = new Bookmark();
		editBookmark.setUser(loggedInUser);
		editBookmark.setLink(new Link(getUrl()));
		editBookmark.setCreatedOn(GnizrDaoUtil.getNow());
		editBookmark.setLastUpdated(GnizrDaoUtil.getNow());
		editBookmark.setTags(getTags());
		
		String st = GnizrDaoUtil.stripNonPrintableChar(getTitle());
		editBookmark.setTitle(st);	
		
		String sn = GnizrDaoUtil.stripNonPrintableChar(getNotes());
		editBookmark.setNotes(sn);		
		try {
			int bmid = bookmarkManager.addBookmark(editBookmark);
			if (bmid <= 0) {
				return ERROR;
			}
			
			// gets a fresh copy of the bookmark that has just been saved 
			editBookmark = bookmarkManager.getBookmark(bmid);
			boolean ok = folderManager.addToMyBookmarks(loggedInUser,editBookmark);
			if(ok == false){
				logger.error("unable add bookmark to my bookmarks folder. bmark:" + editBookmark);
			}
		} catch (Exception e) {
			logger.error(e);
			return ERROR;
		}	
		return SUCCESS;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("go()");
		String result = ERROR;
		// if ID is defined, it's an UPDATE request
		if (getId() > 0) {
			result = updateBookmark();			
		// if ID is undefined and URL is not NULL			
		}else if(getUrl() != null){
			try{
				// if there is an existing bookmark, we update that bookmark.
				if(getOldUrl() != null){					
					this.id = bookmarkManager.getBookmarkId(loggedInUser, getOldUrl());					
				}else{
					this.id = bookmarkManager.getBookmarkId(loggedInUser, getUrl());
				}
				if(this.id > 0){
					result = updateBookmark();
				}else{					
					// if no matching bookmark can be found,
					// process ADD new bookmark
					result = addBookmark();
				}
			}catch(Exception e){
				logger.error(e);
				return ERROR;
			}
		}else{
			addActionMessage("URL is not defined.");
			logger.error("Missing URL.");
			return INPUT;
		}
		
		if(result == SUCCESS){
			if(doUpdateGeometryMarkers(getEditBookmark()) == false){
				result = ERROR;
			}
		}
		
		if(getSaveAndContinueEdit() != null){
			return INPUT;
		}else if(result == SUCCESS && isRedirect() == true){
			return REDIRECT;
		}else if(result == SUCCESS && isSaveAndClose() == true){
			return "close";
		}else{
			return result;
		}
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public Bookmark getEditBookmark() {
		return editBookmark;
	}

	public void setEditBookmark(Bookmark bookmark) {
		this.editBookmark = bookmark;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOldUrl() {
		return oldUrl;
	}

	public void setOldUrl(String oldUrl) {
		this.oldUrl = oldUrl;
	}

	public LinkManager getLinkManager() {
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager) {
		this.linkManager = linkManager;
	}

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public PointMarker[] getPointMarkers() {
		return pointMarkers;
	}

	public void setPointMarkers(PointMarker[] pointMarkers) {
		this.pointMarkers = pointMarkers;
	}

	public String getSaveAndContinueEdit() {
		return saveAndContinueEdit;
	}

	public void setSaveAndContinueEdit(String saveAndContinueEdit) {
		this.saveAndContinueEdit = saveAndContinueEdit;
	}

	public boolean isSaveAndClose() {
		return saveAndClose;
	}

	public void setSaveAndClose(boolean saveAndClose) {
		this.saveAndClose = saveAndClose;
	}
	
}
