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
package com.gnizr.core.web.action.link;

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.managers.LinkManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

public class GetLinkHistory extends AbstractAction implements LoggedInUserAware{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6243525372368427607L;

	private static final Logger logger = Logger.getLogger(GetLinkHistory.class);
	
	private User loggedInUser;
	private String urlHash;
	private LinkManager linkManager;
	private List<Bookmark> bookmarks;

	public LinkManager getLinkManager() {
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager) {
		this.linkManager = linkManager;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("GetLinkHistory.go(): urlHash="+urlHash);
		if(urlHash != null){
			Link link = linkManager.getInfo(urlHash);
			if(link != null){
				bookmarks = linkManager.getHistory(link);							
			}else{
				addActionError("No recorded link history of this URL.");
				return ERROR;
			}
		}		
		return SUCCESS;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}


	public User getLoggedInUser() {
		return loggedInUser;
	}

	public String getUrlHash() {
		return urlHash;
	}

	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}
}
