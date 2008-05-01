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

/**
 * <p>This class provides the representation for a tag assertion. Tag assertion is a statement made by a user to describe a tagging relation
 * between two <code>UserTag</code>. For example, the user can made assertions to state that a given tag related to, narrower than, 
 * broader than, or is type of another tag.</p>
 * <p>Relationships are defined by <code>TagProperty</code>. Gnizr comes with a set of predefined <code>TagProperty</code>, which 
 * is defined by the gnizr database schema. Developers
 * can create new property by introduce new tag property records in the database schema.</p>
 * <p>The ID of the <code>TagProperty</code> uniquely identifies a <code>TagProperty</code>. The ID is usually assigned
 * by the database system when a <code>TagProperty</code> is created for the first time.</p>
 * @author Harry Chen
 * @since 2.3
 *
 */
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
	
	/**
	 * Creates a new instance of this class.
	 */
	public TagAssertion(){
		// no code
	}
	
	/**
	 * Creates a new instance of this class with a defined ID.
	 * @param id this tag assertion ID
	 */
	public TagAssertion(int id){
		this.id = id;
	}
	
	/**
	 * Creates a new instance of this class with defined properties
	 * @param subject the subject <code>UserTag</code> in this tag assertion
	 * @param property the property that relates the subject and the object
	 * @param object the object <code>UserTag</code> in this tag assertion
	 * @param user
	 */
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
	
	/**
	 * Copy constructor for this class
	 * @param ta object to copy from
	 */
	public TagAssertion(TagAssertion ta){
		this(ta.getSubject(),ta.getProperty(),ta.getObject(),ta.getUser());
		this.id = ta.id;						
	}
	
	/**
	 * Returns the ID of this tag assertion.
	 * @return an ID
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the ID of this tag assertion
	 * @param id an ID
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the object <code>UserTag</code> in this tag assertion
	 * @return the object value of this assertion
	 */
	public UserTag getObject() {
		return object;
	}
	
	/**
	 * Sets the object <code>UserTag</code> in this tag assertion
	 * @param object the object value of this assertion
	 */
	public void setObject(UserTag object) {
		this.object = object;
	}
	
	/**
	 * Returns the property of this tag assertion. 
	 * 
	 * @return the property value of this assertion.
	 */
	public TagProperty getProperty() {
		return property;
	}
	
	/**
	 * Sets the property of this tag assertion
	 * 
	 * @param property the property value of this assertion
	 */
	public void setProperty(TagProperty property) {
		this.property = property;
	}
	
	/**
	 * Gets the subject value of this tag assertion
	 * 
	 * @return the subject value of this assertion
	 */
	public UserTag getSubject() {
		return subject;
	}
	
	/**
	 * Sets the subject value of this tag assertion
	 * 
	 * @param subject the subjeect value of this assertion
	 */
	public void setSubject(UserTag subject) {
		this.subject = subject;
	}
	
	/**
	 * Gets the user who made this tag assertion.
	 * @return the <code>User</code> who made this assertion
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Sets the user who made this tag assertion
	 * @param user who makes the assertion
	 */
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
