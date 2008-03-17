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

import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.util.FormatUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.LoggedInUserAware;

public class FetchBookmark extends AbstractAction implements LoggedInUserAware{

	private static final Logger logger = Logger.getLogger(FetchBookmark.class);

	private static final long serialVersionUID = 2657480501192557947L;
	
	private int bookmarkId;
	private Bookmark bookmark;
	private BookmarkManager bookmarkManager;
	private User loggedInUser;	
	
	/**
	 * @return the bookmark
	 */
	public Bookmark getBookmark() {
		return bookmark;
	}

	/**
	 * @return the bookmarkId
	 */
	public int getBookmarkId() {
		return bookmarkId;
	}

	/**
	 * @param bookmarkId the bookmarkId to set
	 */
	public void setBookmarkId(int bookmarkId) {
		this.bookmarkId = bookmarkId;
	}

	/**
	 * @return the bookmarkManager
	 */
	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	/**
	 * @param bookmarkManager the bookmarkManager to set
	 */
	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	/**
	 * @return the loggedInUser
	 */
	public User getLoggedInUser() {
		return loggedInUser;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("FetchBookmark.go()");
		if(getBookmarkId() > 0){
			bookmark = bookmarkManager.getBookmark(getBookmarkId());
		}
		return SUCCESS;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

	public JSON getJsonResult(){
		if(bookmark == null){
			return new JSONObject();			
		}else{
			Map<String,Object> bookmarkMap = FormatUtil.getBookmarkAsMap(bookmark);
			return JSONSerializer.toJSON(bookmarkMap);
		}
	}
	
}
