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
import java.util.HashMap;
import java.util.Map;

/**
 * <p>This class provides a representation of a tag. A tag is consists of three parts:
 * the ID of tag, a <code>String</code> representation of the tag, and the number of 
 * times the tag is used to label gnizr bookmarks.</p>
 * <p>The ID of the tag is usually assigned by the database system when the tag
 * is created for the first time. 
 * </p>
 * @author Harry Chen
 * @since 2.2
 */
public class Tag implements Serializable, TagLabel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6336641814532727078L;
	private int id;
	private String label;
	private int count;
	
	/**
	 * Creates a new instance of this class and tag count is set to 0.
	 */
	public Tag(){
		count = 0;
	}
	
	/**
	 * Creates a new instance of this class with defind tag ID.
	 * @param id tag ID
	 */
	public Tag(int id){
		this();
		this.id = id;
	}
	
	/**
	 * Creates a new instance of this class with a defined tag label.
	 * @param label the string representation of the tag.
	 */
	public Tag(String label){
		this();
		this.label = label;
	}
	
	/**
	 * Creates a new instance of this class with defined tag label and
	 * tag count.
	 * @param label the string representation of the tag
	 * @param count the number of times the tag is used
	 */
	public Tag(String label, int count){
		this();
		this.label = label;
		this.count = count;		
	}
	
	/**
	 * Copy constructor for this class.
	 * 
	 * @param tag object to copy from.
	 */
	public Tag(Tag tag){
		this.id = tag.id;		
		this.label = tag.label;
		this.count = tag.count;		
	}

	/**
	 * Returns the number of times this tag is used to label gnizr bookmarks.
	 * @return tag usage count.
	 */
	public int getCount() {
		return count;
	}
	
	/**
	 * Sets the number of times this tag is used to label gnizr bookmarks.
	 * @param count tag usage count
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * Returns the ID of this tag object
	 * 
	 * @return tag ID
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * Sets the ID of this tag object
	 * 
	 * @param id tag ID
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Returns the string representation of this tag.
	 * @return tag label
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Sets the string representation of this tag.
	 * 
	 * @param tag the string representation of this tag
	 */
	public void setLabel(String tag) {
		this.label = tag;
	}

	@Override
	public String toString() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id",getId());
		map.put("label",getLabel());
		map.put("count",getCount());
		return map.toString();
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
		result = PRIME * result + ((label == null) ? 0 : label.hashCode());
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
		final Tag other = (Tag) obj;
		if (count != other.count)
			return false;
		if (id != other.id)
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		return true;
	}
	
}
