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
package com.gnizr.db.dao.foruser;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.User;

/**
 * A Data Access Object interface for all classes 
 * that provide persistent data access to <code>ForUser</code> records. 
 */
public interface ForUserDao extends Serializable {
	
	/**
	 * Creates a new record of <code>ForUser</code> in the persistent store and returns
	 * its assigned record ID. 
	 *  
	 * @param forUser the data value to be created 
	 * @return a positive <code>int</code> if the record is sucessfully created. Otherwise,
	 * returns a non-positive <code>int<code>.
	 * 
	 * @since 2.0
	 */
	public int createForUser (ForUser forUser);
	
	/**
	 * Deletes a <code>ForUser</code> record of a given <code>id</code> from the
	 * persistent store. 
	 * @param id the <code>ForUser</code> ID in the persitent store
	 * @return <code>true</code> if the record is successfully deleted. Otherwise,
	 * returns <code>false</code>.
	 * 
 	 * @since 2.0 
	 */
	public boolean deleteForUser (int id);
	
	/**
	 * Returns the <code>ForUser</code> record of a given <code>id</code> from the
	 * persistent store. 
	 * 
	 * @param id the <code>ForUser</code> ID in the persitent store
	 * @return an instantiated <code>ForUser</code> record whose ID equals 
	 * to <code>id</code>. Returns <code>null</code> if no record of this <code>id</code>
	 * exists in the persistent store.
	 * @since 2.0
	 */
	public ForUser getForUser (int id);
	
	/**
	 * Returns the total number of <code>ForUser</code> records of a given <code>user</code>. 
	 * A <code>ForUser</code> record is counted if <code>ForUser.getUser().getId()</code> 
	 * equals to <code>user.getId()</code> 
	 * 
	 * @param user a <code>User</code> in the persistent store
	 * @return the total number of <code>ForUser</code> records of <code>user</code>
	 * @since 2.0
	 */	
	public int getForUserCount (User user);	
	
	
	/**
	 * Returns the total number of <code>ForUser</code> records of a given <code>user</code> 
	 * that are created during a specific time interval.
	 * A <code>ForUser</code> record is counted if <code>ForUser.getUser().getId()</code> 
	 * equals to <code>user.getId()</code> 
	 * 
	 * @param user a <code>User</code> in the persistent store
	 * @param start the start of the time interval
	 * @param end the end of the time interval
	 * @return the total number of <code>ForUser</code> records of <code>user</code>
	 * @since 2.0
	 */	
	public int getForUserCount(User user, Date start, Date end);
	
	/**
	 * Returns <code>true</code> if the persistent store has a <code>ForUser</code>
	 * record that matches <code>bookmark</code> and <code>user</code>. A <code>ForUser</code>
	 * record <code>r</code> is a matching record if <code>r.getUser()</code> equals to
	 * <code>user</code> and <code>r.getBookmark()</code> equals to <code>bookmark</code>
	 *  
	 * @param bookmark a <code>Bookmark</code> object with instantiated <code>Bookmark.getId()</code> 
	 * @param user a <code>User</code> object with instantiated <code>User.getId</code>
	 * @return <code>true</code> a matching record is found. Otherwise, returns <code>false</code>.
	 * @since 2.0
	 */
	public boolean hasForUser (Bookmark bookmark, User user);
	
	/**
	 * Returns the list of <code>ForUser</code> records of a given <code>bookmark</code>. 
	 * 
	 * @param bookmark a <code>Bookmark</code> object with instantiated <code>Bookmark.getId()</code>
	 * @return a list of <code>ForUser</code> objects
	 */
	public List<ForUser> findForUserFromBookmark (Bookmark bookmark);
	
	/**
	 * Returns at most <code>count</code> number of <code>ForUser</code> records of a given <code>user</code> 
	 * in reverse chronological order, starting at record index <code>offset</code>.
	 * 
	 * @param user a <code>User</code> object with instantiated <code>User.getId()</code>  
	 * @param offset paging offset
	 * @param count max count
	 * @return a list of instantiated <code>ForUser</code> objects.
	 * @since 2.0
	 */
	public List<ForUser> pageForUser (User user, int offset, int count);
	
	/**
	 * Updates a <code>ForUser</code> record in the persistent store. In the persistent
	 * store, a record of the same ID as the <code>forUser</code> is updated. All properties
	 * are updated, except the <code>ForUser.getId()</code> property.
	 * 
	 * @param forUser a record to be updated.
	 * @return <code>true</code> if an update succeed. Otherwise, returns <code>false</code>. 
	 * @since 2.0
	 */
	public boolean updateForUser (ForUser forUser);
	
	/**
     * Pages a user's <code>ForUser</code> records created during a specific time internval. 
     * Records are sorted by <code>ForUser.getCreatedOn()</code> in reverse chronological order.
	 *   
	 * @param user a <code>User</code> object with instantiated <code>User.getId()</code>
	 * @param start the starting time of the time interval
	 * @param end the ending time of the time interval
 	 * @param offset paging offset
	 * @param count max count
	 * @return a list of instantiated <code>ForUser</code> objects
	 * @since 2.0
	 */
	public List<ForUser> pageForUser(User user, Date start, Date end, int offset, int count);
	
	
	/**
	 * Pages the <code>ForUser</code> bookmarks sent by a specific user. 
	 * 
	 * @param user bookmarks suggested to this user
	 * @param sender the sender of the suggest bookmarks
	 * @param offset paging offset
	 * @param count paging count index
	 * @return a list of bookmarks suggested to <code>user</code> by <code>sender</code>
	 */
	public DaoResult<ForUser> pageForUser(User user, User sender, int offset, int count);
	
	/**
	 * Deletes a list of <code>ForUser</code> bookmarks suggested to a user. The list of 
	 * <code>ForUser</code> bookmarks to delete is specified by <code>ids</code>.
	 * 
	 * @param user the <code>ForUser</code> bookmarks that are suggest to this user
	 * @param ids the id of the <code>ForUser</code> bookmarks to delete
	 * @return returns <code>true</code> if bookmarks are deleted successfully. Returns <code>false</code>
	 * otherwise.
	 */
	public boolean deleteForUser(User user, int[] ids);
	
	/**
	 * Deletes all bookmarks that are suggested to a user.
	 * 
	 * @param user the <code>ForUser</code> bookmarks that are suggest to this user
	 * @return returns <code>true</code> if bookmarks are deleted successfully. Returns <code>false</code>
	 * otherwise.
	 */
	public boolean deleteAllForUser(User user);
	
	public List<User> listForUserSenders(User user);
	
}
