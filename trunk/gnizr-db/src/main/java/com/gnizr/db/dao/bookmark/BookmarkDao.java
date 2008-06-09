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
package com.gnizr.db.dao.bookmark;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;

public interface BookmarkDao extends Serializable{	
	
	public static final int SORT_BY_CREATED_ON = 1;
	public static final int SORT_BY_LAST_UPDATED = 2;
	public static final int DESCENDING = 1;
	public static final int ASCENDING = 2;
	
	public int createBookmark(Bookmark bm);
	public boolean deleteBookmark(int id);
	
	public List<Bookmark> findBookmark(User user,Link link);
	public Bookmark getBookmark(int id);
	
	/**
	 * Returns the total number of bookmarks that reference
	 * a particular URL.
	 * 
	 * @param link a <code>Link</code> object and call to <code>link.getId()</code>
	 * returns a valid id value. 
	 * @return total number of bookmarks that refers a common <code>Link</code>
	 */
	public int getBookmarkCount(Link link);
	
	
	/**
	 * Returns the total number of bookmarks that share a common <code>Tag</code>.
	 * This method
	 * will not count bookmarks of the same <code>Link</code> multiple times -- i.e., 
	 * the all <code>Link</code> objects associated matching <code>Bookmark</code> 
	 * list is guaranteed to be unique. 
	 * @param tag a <code>Tag</code> object and call to <code>tag.getId()</code> returns
	 * a valid id value.
	 * @return the number of bookmarks that are tagged <code>tag</code>
	 */
	public int getBookmarkCount(Tag tag);
	
	/**
	 * Returns the total number of bookmarks of a user.
	 * 
	 * @param user an instantiated <code>User</code>
	 * @return total number of bookmarks of <code>user</code>
	 */
	public int getBookmarkCount(User user);	
	
	
	/**
	 * Paging saved <code>Bookmarks</code> of a given <code>Link</code>. 
	 *  
	 * @param link a <code>Link</code> object with a valid <code>id</code>
	 * @param offset the index where the paging begins
	 * @param count the maximum number of <code>Bookmarks</code> to page
	 * @return paging result contains no more <code>count</code> of <code>Bookmark</code>
	 */
	public DaoResult<Bookmark> pageBookmarks(Link link, int offset, int count);
	
	
	/**
	 * Returns a list of <code>Bookmark</code> objects for a given <code>Tag</code> with paging support.
	 * The paging begins at the <code>offset</code> and retuns no more than <code>count</code> number 
	 * of <code>Bookmark</code> objects. This method
	 * will not include bookmarks of the same <code>Link</code> multiple times -- i.e., 
	 * all <code>Link</code> objects associated the resulting <code>Bookmark</code> 
	 * list is guaranteed to be unique. 
	 * 
	 * @param tag a <code>Tag</code> object and call to <code>tag.getId()</code> returns
	 * a valid id value.
	 * @param offset starting index of the paging
	 * @param count the maximum number of matching objects
	 * @return
	 */
	public DaoResult<Bookmark> pageBookmarks(Tag tag, int offset, int count);
	
	public DaoResult<Bookmark> pageBookmarks(User user, int offset, int count);
	
	public DaoResult<Bookmark> pageBookmarks(User user, int offset, int count, int sortBy, int order);
	
	public DaoResult<Bookmark> pageBookmarks(User user, Tag tag, int offset, int count);
	
	public DaoResult<Bookmark> pageBookmarks(User user, Tag tag, int offset, int count, int sortBy, int order);
	
	public DaoResult<Bookmark> searchCommunityBookmarks(String searchQuery, int offset, int count);
	
	public DaoResult<Bookmark> searchUserBookmarks(String searchQuery, User user, int offset, int count);
	
	public boolean updateBookmark(Bookmark bm);	
	
	/**
	 * Returns popular bookmarks saved by users during a time interval 
	 * @param start the start date/time of the time interval
	 * @param end the end date/time of the time interval
	 * @param maxCount returns no more than this number of bookmarks
	 * @return a non-null list of bookmarks
	 */
	public List<Bookmark> getPopularCommunityBookmarks(Date start, Date end, int maxCount);
	
}
