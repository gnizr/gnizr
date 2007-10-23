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

public class UserStat extends User{

	/**
	 * 
	 */
	private static final long serialVersionUID = -625773407714243401L;

	private int numOfBookmarks;
	private int numOfTags;
	
	public UserStat(){
		super();
		this.numOfBookmarks = 0;
		this.numOfTags = 0;
	}
	
	public UserStat(User user){
		super(user);
		this.numOfBookmarks = 0;
		this.numOfTags = 0;
	}
	
	public UserStat(UserStat userStat){
		super((User)userStat);
		this.numOfBookmarks = userStat.numOfBookmarks;
		this.numOfTags = userStat.numOfTags;
	}

	public int getNumOfBookmarks() {
		return numOfBookmarks;
	}

	public void setNumOfBookmarks(int numOfBookmarks) {
		this.numOfBookmarks = numOfBookmarks;
	}

	public int getNumOfTags() {
		return numOfTags;
	}

	public void setNumOfTags(int numOfTags) {
		this.numOfTags = numOfTags;
	}
	
	
}
