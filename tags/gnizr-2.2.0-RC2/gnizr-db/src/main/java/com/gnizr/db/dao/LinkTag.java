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


public class LinkTag implements Serializable, TagLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7871011373344014420L;
	private int id;
	private Link link;
	private Tag tag;
	private int count;	
	
	public LinkTag(){
		count = 0;
	}
	
	public LinkTag(int id){
		this();
		this.id = id;
	}
	
	public LinkTag(Link link, Tag tag){
		this();
		this.link = new Link(link);
		this.tag = new Tag(tag);
	}
	
	public LinkTag(String url, String tag){
		this();
		this.link = new Link(url);
		this.tag = new Tag(tag);
	}
	
	public LinkTag(LinkTag linkTag){
		this.id = linkTag.id;
		this.link = new Link(linkTag.link);
		this.tag = new Tag(linkTag.tag);
		this.count = linkTag.count;
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

	public Link getLink() {
		return link;
	}

	public void setLink(Link link) {
		this.link = link;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
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
		result = PRIME * result + ((link == null) ? 0 : link.hashCode());
		result = PRIME * result + ((tag == null) ? 0 : tag.hashCode());
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
		final LinkTag other = (LinkTag) obj;
		if (count != other.count)
			return false;
		if (id != other.id)
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
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
