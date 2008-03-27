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
package com.gnizr.core.search;

import java.io.Serializable;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.User;

public class Search implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5131043207555467304L;

	private static final Logger logger = Logger.getLogger(Search.class);
	
	private GnizrDao gnizrDao;
	
	public Search(GnizrDao gnizrDao){
		this.gnizrDao = gnizrDao;
	}
	

	public SearchResult<Bookmark> searchBookmarkCommunity(String textQuery){
		logger.debug("searchLink: textQuery="+textQuery);
		return new BookmarkSearchResult(gnizrDao, textQuery);		
	}
	
	public SearchResult<Bookmark> searchBookmarkUser(String textQuery, User user){
		logger.debug("searchLink: textQuery="+textQuery+",user="+user);
		return new BookmarkSearchResult(gnizrDao, textQuery, user);
	}
	
	public SearchResult<BookmarkTag> searchBookmarkTags(String textQuery){
		return new BookmarkTagSearchResult(gnizrDao,textQuery);
	}

}
