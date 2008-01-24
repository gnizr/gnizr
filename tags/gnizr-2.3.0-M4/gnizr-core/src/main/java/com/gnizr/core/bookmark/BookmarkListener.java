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

import java.io.Serializable;

import com.gnizr.db.dao.Bookmark;

/**
 * An interface class for implementing listeners to monitor <code>Bookmark</code> changes. 
 * 
 * @author harryc
 *
 */
public interface BookmarkListener extends Serializable{

	/**
	 * Notifies this listener when a new <code>Bookmark</code> has been 
	 * added to the persistent store. The object <code>bookmark</code> contains
	 * information about the new bookmark. 
	 * 
	 * @param manager the <code>BookmarkManager</code> that calls this method.
	 * @param bookmark the newly added bookmark
	 * @throws Exception an error occured in this class
	 */
	public void notifyAdded(BookmarkManager manager, Bookmark bookmark) throws Exception;
	
	/**
	 * Notifies this listener when an existing <code>Bookmark</code> has been
	 * deleted from the persistent store. The object <code>bookmark</code>
	 * contains infomration about the deleted bookmark. At the time when this
	 * method is called, <code>bookmark</code> has already been deleted
	 * from the persistent store.
	 * 
	 * @param manager the <code>BookmarkManager</code> that calls this method.
	 * @param bookmark the boomark that has been deleted from the persistent store
	 * @throws Exception an error occured in this class
	 */
	public void notifyDeleted(BookmarkManager manager,Bookmark bookmark) throws Exception;
	
	
	/**
	 * Notifies this listener when the information of an existing <code>Bookmark</code>
	 * has been updated. The object <code>oldBookmark</code> contains the old information
	 * of the bookmark, and the object <code>newBookmark</code> contains the new information
	 * of the bookmark. At the time when this method is called, <code>newBookmark</code> has 
	 * already been updated in the persistent store. 
	 * 
	 * @param manager the <code>BookmarkManager</code> that calls this method.
	 * @param oldBookmark old information about the bookmark
	 * @param newBookmark new information about the bookmark
	 * @throws Exception an error occured in this class
	 */
	public void notifyUpdated(BookmarkManager manager,Bookmark oldBookmark, Bookmark newBookmark) throws Exception;
	
}
