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
package com.gnizr.db.dao;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>This class describes the bookmark recommendation send from one user to another. The sender of this
 * recommendation is the owner of the bookmark, and the receiver is defined by <code>forUser</code>.</p>
 * <p>
 * All <code>ForUser</code> have an unique ID, which is usually assigned by the database system
 * when the DB record is created for the first time. 
 * </p>
 * @author Torey
 *
 */
public class ForUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5381325748314239180L;

	private int id;
	
	private User forUser;
	
	private Bookmark bookmark;
	
	private String message;
	
	private Date createdOn;

	public ForUser ()
	{
		
	}
	
	public ForUser (int id)
	{
		this.id = id;
	}
	
	public ForUser (User forUser, Bookmark bookmark, String message)
	{
		this.forUser = forUser;
		this.bookmark = bookmark;
		this.message = message;
	}
	
	public ForUser (User forUser, Bookmark bookmark, String message, Date createdOn)
	{
		this.forUser = forUser;
		this.bookmark = bookmark;
		this.message = message;
		this.createdOn = createdOn;
	}
	
	public Bookmark getBookmark() {
		return bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public User getForUser() {
		return forUser;
	}

	public void setForUser(User forUser) {
		this.forUser = forUser;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
