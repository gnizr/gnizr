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



public class UserTag implements Serializable, TagLabel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8317648945700959907L;
	private int id;
	private int count;
	private User user;
	private Tag tag;

	public UserTag(){
		count = 0;
	}
	
	public UserTag(int id){
		this();
		this.id = id;
	}
	
	public UserTag(User user, Tag tag){
		this();
		this.user = new User(user);
		this.tag = new Tag(tag);
	}
	
	public UserTag(String username, String tag){
		this();
		this.user = new User(username);
		this.tag = new Tag(tag);
	}
	public UserTag(UserTag tag){
		this.id = tag.id;		
		this.count = tag.count;		
		if(tag.user != null){
			this.user = new User(tag.user);
		}
		if(tag.tag != null){
			this.tag = new Tag(tag.tag);
		}
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public Tag getTag() {
		return tag;
	}
	public void setTag(Tag tag) {
		this.tag = tag;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + count;
		result = PRIME * result + id;
		result = PRIME * result + ((tag == null) ? 0 : tag.hashCode());
		result = PRIME * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final UserTag other = (UserTag) obj;
		if (count != other.count)
			return false;
		if (id != other.id)
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	public String getLabel() {
		if(tag != null){
			return tag.getLabel();
		}
		return null;
	}

	
	
}
