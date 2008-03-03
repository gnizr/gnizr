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
package com.gnizr.core.bookmark;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import com.gnizr.core.search.DocumentCreator;
import com.gnizr.core.search.SearchIndexManager;
import com.gnizr.db.dao.Bookmark;

/**
 * <p>This listener provides the support for indexing bookmark for search.</p>
 * @author Harry Chen
 *
 */
public class IndexBookmarkListener implements BookmarkListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6294234317241894725L;

	private static final Logger logger = Logger.getLogger(IndexBookmarkListener.class);
	
	private SearchIndexManager searchIndexManager;
	
	public IndexBookmarkListener(SearchIndexManager searchIndexManager){
		this.searchIndexManager = searchIndexManager;
	}

	/* (non-Javadoc)
	 * @see com.gnizr.core.bookmark.BookmarkListener#notifyAdded(com.gnizr.core.bookmark.BookmarkManager, com.gnizr.db.dao.Bookmark)
	 */
	public void notifyAdded(BookmarkManager manager, Bookmark bookmark)
			throws Exception {	
		Document doc = DocumentCreator.createDocument(bookmark);
		if(doc != null){
			logger.debug("notify SearchIndexManager to addIndex: " + bookmark.toString());
			searchIndexManager.addIndex(doc);
		}
	}

	/* (non-Javadoc)
	 * @see com.gnizr.core.bookmark.BookmarkListener#notifyDeleted(com.gnizr.core.bookmark.BookmarkManager, com.gnizr.db.dao.Bookmark)
	 */
	public void notifyDeleted(BookmarkManager manager, Bookmark bookmark)
			throws Exception {
		Document doc = DocumentCreator.createDocument(bookmark);
		if(doc != null){
			logger.debug("notify SearchIndexManager to deleteIndex: " + bookmark.toString());
			searchIndexManager.deleteIndex(doc);
		}

	}

	/* (non-Javadoc)
	 * @see com.gnizr.core.bookmark.BookmarkListener#notifyUpdated(com.gnizr.core.bookmark.BookmarkManager, com.gnizr.db.dao.Bookmark, com.gnizr.db.dao.Bookmark)
	 */
	public void notifyUpdated(BookmarkManager manager, Bookmark oldBookmark,
			Bookmark newBookmark) throws Exception {
		Document doc = DocumentCreator.createDocument(newBookmark);
		if(doc != null){
			logger.debug("notify SearchIndexManager to updateIndex: " + newBookmark.toString());
			searchIndexManager.addIndex(doc);
		}

	}

}
