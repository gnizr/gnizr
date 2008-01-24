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


public class TagAssertion implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3040119310015855533L;
	private int id;
	private UserTag subject;
	private UserTag object;
	private TagProperty property;
	private User user;
	
	public TagAssertion(){
		// no code
	}
	
	public TagAssertion(int id){
		this.id = id;
	}
	
	public TagAssertion(UserTag subject, TagProperty property, UserTag object, User user){
		if(subject != null){
			this.subject = new UserTag(subject);
		}
		if(property != null){
			this.property = new TagProperty(property);
		}
		if(object != null){
			this.object = new UserTag(object);
		}
		if(user != null){
			this.user = new User(user);
		}
	}
	
	public TagAssertion(TagAssertion ta){
		this(ta.getSubject(),ta.getProperty(),ta.getObject(),ta.getUser());
		this.id = ta.id;						
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public UserTag getObject() {
		return object;
	}
	public void setObject(UserTag object) {
		this.object = object;
	}
	public TagProperty getProperty() {
		return property;
	}
	public void setProperty(TagProperty property) {
		this.property = property;
	}
	public UserTag getSubject() {
		return subject;
	}
	public void setSubject(UserTag subject) {
		this.subject = subject;
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
		result = PRIME * result + id;
		result = PRIME * result + ((object == null) ? 0 : object.hashCode());
		result = PRIME * result + ((property == null) ? 0 : property.hashCode());
		result = PRIME * result + ((subject == null) ? 0 : subject.hashCode());
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
		final TagAssertion other = (TagAssertion) obj;
		if (id != other.id)
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

	
	
}
