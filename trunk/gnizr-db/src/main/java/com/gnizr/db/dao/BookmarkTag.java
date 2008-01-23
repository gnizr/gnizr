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
 * This class represents the tagging relationship between a bookmark and a tag.
 * Every tagging relationship has a unique ID. This class also describes
 * the number of times the tag is used to label the bookmark (typically is 0 or 1), and 
 * the index position of the tag in the list of bookmark tags. 
 * 
 * @author Harry Chen
 *
 */
public class BookmarkTag implements Serializable, TagLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1900534310992576875L;

	private int id;
	private Bookmark bookmark;
	private Tag tag;
	private int count;
	private int position;
	
	/**
	 * Creates a new instance of this class.
	 */
	public BookmarkTag(){
		this(-1,null,null,0,0);
	}
	
	/**
	 * Creates a new instance of this class with a defined ID.
	 * @param id the ID of this bookmark/tag relation. 
	 */
	public BookmarkTag(int id){
		this(id,null,null,0,0);
	}
	
	/**
	 * Creates a new instance of this class and initializes 
	 * the class property values.
	 * 
	 * @param id the ID of this bookmark/tag relation.
	 * @param bookmark a bookmark 
	 * @param tag a tag
	 * @param count number of time bookmark is tagged
	 * @param position the index position of tag. 
	 */
	public BookmarkTag(int id, Bookmark bookmark, Tag tag, int count, int position){
		this.id = id;
		this.count = count;
		this.position = position;
		if(bookmark != null){
			this.bookmark = new Bookmark(bookmark);
		}
		if(tag != null){
			this.tag = new Tag(tag);
		}		
	}

	/**
	 * Returns the bookmark in this relation.
	 * @return bookmark object
	 */
	public Bookmark getBookmark() {
		return bookmark;
	}

	/**
	 * Sets the bookmark in this relation.
	 * @param bookmark bookmark object
	 */
	public void setBookmark(Bookmark bookmark) {
		if(bookmark != null){
			this.bookmark = new Bookmark(bookmark);
		}else{
			this.bookmark = null;
		}
	}

	/**
	 * Return this ID of this relation.
	 * 
	 * @return ID number. If less than or equals 0, the ID in the system is undefined. 
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of this relation.
	 * @param id ID number.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the tag in this relation.
	 * 
	 * @return tag object.
	 */
	public Tag getTag() {
		return tag;
	}

	/**
	 * Sets the tag in this relation.
	 * @param tag tag object.
	 */
	public void setTag(Tag tag) {
		if(tag != null){
			this.tag = new Tag(tag);
		}else{
			this.tag = null;
		}
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((bookmark == null) ? 0 : bookmark.hashCode());
		result = PRIME * result + count;
		result = PRIME * result + id;
		result = PRIME * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BookmarkTag other = (BookmarkTag) obj;
		if (bookmark == null) {
			if (other.bookmark != null)
				return false;
		} else if (!bookmark.equals(other.bookmark))
			return false;
		if (count != other.count)
			return false;
		if (id != other.id)
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}

	@Override
	public String toString() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("id",getId());
		map.put("bookmark",getBookmark());
		map.put("tag",getTag());
		map.put("count",getCount());
		return map.toString();
	}

	/**
	 * Returns the number of times that the bookmark is labelled with the tag.
	 * @return tagging count.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the number of times that bookmark is labelled with the tag.
	 * 
	 * @param count tagging count.
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Returns the index position of the associated tag in the bookmark's tag list. 
	 * @return index position, starting at 0. 
	 */
	public int getPosition() {
		return position;
	}

	/**
	 * Sets the index position of the associated tag in the bookmark's tag list.
	 * 
	 * @param position index position; should be great than 0.
	 */
	public void setPosition(int position) {
		this.position = position;
	}

	/**
	 * Returns the text label of the tag. If <code>getTag</code> is not <code>null</code>,
	 * then the return value is the same
	 * as the value by calling from <code>getTag().getLabel()</code>. 
	 * 
	 */
	public String getLabel() {
		if(tag != null){
			return tag.getLabel();
		}
		return null;
	}
}
