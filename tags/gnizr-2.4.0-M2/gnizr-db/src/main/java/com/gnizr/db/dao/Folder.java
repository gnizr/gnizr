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
 * <p>This class defines the representation of a folder for storing bookmarks. Folders are usually created 
 * users to organize bookmarks. All folders have an unique ID, which is usually assigned by the database system
 * when the DB record for a folder is created for the first time. 
 * </p>
 * <p>
 * All <code>FolderTag</code> have an unique ID, which is usually assigned by the database system
 * when the DB record is created for the first time. 
 * </p>
 * @author Harry Chen
 * @since 2.2
 */
public class Folder implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4497008314940656859L;

	private int id;
	private String name;
	private User user;
	private String description;
	private Date lastUpdated;
	private int size;
	
	public Folder(){
		this(-1,null,null,null,null,0);
	}
	
	public Folder(int id){
		this(id,null,null,null,null,0);
	}
	
	public Folder(String name, User owner, String description, Date lastUpdated){
		this(-1,name,owner,description,lastUpdated,0);
	}
	
	public Folder(Folder copyObj){
		this(copyObj.id,copyObj.name,copyObj.user,copyObj.description,copyObj.lastUpdated,copyObj.size);
	}
	
	public Folder(int id, String name, User owner, String description, Date lastUpdated, int size){
		this.id = id;
		this.name = name;
		if(owner != null){
			this.user = new User(owner);
		}else{
			this.user = null;
		}	
		this.description = description;
		if(lastUpdated != null){
			this.lastUpdated = (Date)lastUpdated.clone();
		}
		this.size = size;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
