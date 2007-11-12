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

import org.apache.commons.codec.digest.DigestUtils;

public class Link implements Serializable{

	@Override
	public String toString() {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id",getId());
		map.put("url", getUrl());
		map.put("urlHash", getUrlHash());
		map.put("mimeTypeId", getMimeTypeId());
		map.put("count", getCount());
		return map.toString();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -313034364360342931L;
	
	public static final String computeUrlHash(String url){
		return DigestUtils.md5Hex(url);		
	}
	
	private int id;
	private int mimeTypeId;
	private String url;
	private String urlHash;
	private int count;

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count the count to set
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the mimeTypeId
	 */
	public int getMimeTypeId() {
		return mimeTypeId;
	}

	/**
	 * @param mimeTypeId the mimeTypeId to set
	 */
	public void setMimeTypeId(int mimeTypeId) {
		this.mimeTypeId = mimeTypeId;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the urlHash
	 */
	public String getUrlHash() {
		return urlHash;
	}

	/**
	 * @param urlHash the urlHash to set
	 */
	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}

	public Link(){
		this.count = 0;
		this.id = -1;
	}

	public Link(int id){
		this.id = id;
	}
	
	public Link(Link link){		
		this.id = link.id;
		this.mimeTypeId = link.mimeTypeId;		
		this.url = link.url;	
		this.urlHash = link.urlHash;	
		this.count = link.count;
	}
	
	public Link(String url){
		this();
		this.url = url;
	}

	
	public Link(String url, int mimeTypeId){
		this(url);
		this.mimeTypeId = mimeTypeId;
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
		result = PRIME * result + mimeTypeId;
		result = PRIME * result + ((url == null) ? 0 : url.hashCode());
		result = PRIME * result + ((urlHash == null) ? 0 : urlHash.hashCode());
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
		final Link other = (Link) obj;
		if (count != other.count)
			return false;
		if (id != other.id)
			return false;
		if (mimeTypeId != other.mimeTypeId)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (urlHash == null) {
			if (other.urlHash != null)
				return false;
		} else if (!urlHash.equals(other.urlHash))
			return false;
		return true;
	}
	
	
	
}
