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
	
	public BookmarkTag(){
		this(-1,null,null,0,0);
	}
	
	public BookmarkTag(int id){
		this(id,null,null,0,0);
	}
	
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

	public Bookmark getBookmark() {
		return bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		if(bookmark != null){
			this.bookmark = bookmark;
		}else{
			this.bookmark = null;
		}
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public String getLabel() {
		if(tag != null){
			return tag.getLabel();
		}
		return null;
	}
}
