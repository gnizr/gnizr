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

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.tag.TagDao;

public class BookmarkTagSearchResult implements SearchResult<BookmarkTag>{

	private Logger logger = Logger.getLogger(BookmarkSearchResult.class);
	
	private TagDao tagDao;	
	private String searchQuery;
	private List<BookmarkTag> result;
	
	public BookmarkTagSearchResult(GnizrDao gnizrDao, String searchQuery){
		this.tagDao = gnizrDao.getTagDao();
		this.searchQuery = searchQuery;
		doSearch(0,10);
	}
	
	private void doSearch(int offset, int count) {	
		result = tagDao.findBookmarkTagCommunitySearch(searchQuery);
		logger.debug("found # of bookmarkTags: "+ result.size());
	}
	
	public BookmarkTag getResult(int n) {
		return result.get(n);
	}

	public List<BookmarkTag> getResults(int offset, int count) {
		return result.subList(offset, count);
	}

	public int length() {
		return result.size();
	}

}
