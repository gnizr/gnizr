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

/**
 * <p>This class provides the representation of a URL link. This class
 * defines key properties of a URL: URL string, a MD5 has of the URL string, MIME-TYPE and 
 * the number of times the link is bookmarked.</p> 
 * <p>The ID of the class uniquely identifies a <code>Link</code>, which is usually created
 * by the database system when the <code>Link</code> is created for the first time.</p>
 * <h5>Gnizr MIME-TYPE ID</h5>
 * <pre>
 *    ID : MIME-TYPE
 *   0000: Unknown
 *   1001: text/xml
 *   1002: text/plain
 *   1003: text/html
 *   2001: image/jpeg
 *   2002: image/png
 *   2003: image/tiff
 *   2004: image/gif
 *   3001: application/rss+xml
 *   3002: application/rdf+xml
 *   3003: application/owl-xml
 * </pre>
 * @author Harry Chen
 *
 */
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
	
	/**
	 * Computes the MD5 hash of an input URL string. 
	 * 
	 * @param url a valid URL string
	 * @return MD5 of the input URL
	 */
	public static final String computeUrlHash(String url){
		return DigestUtils.md5Hex(url);		
	}
	
	private int id;
	private int mimeTypeId;
	private String url;
	private String urlHash;
	private int count;

	/**
	 * Returns the number of times this <code>Link</code> is bookmarked.
	 * 
	 * @return the count value
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Sets the number of times this <code>Link</code> is bookmarked.
	 * 
	 * @param count the count value 
	 */
	public void setCount(int count) {
		this.count = count;
	}

	/**
	 * Returns the ID of this <code>Link</code>.
	 * 
	 * @return the unique ID of this <code>Link</code>
	 */
	public int getId() {
		return id;
	}

	/**
	 * Sets the ID of this <code>Link</code>
	 * 
	 * @param id the unique ID of this <code>Link</code>
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Returns the ID of the MIME-TYPE of this <code>Link</code>.
	 *  
	 * @return the mimeTypeId
	 */
	public int getMimeTypeId() {
		return mimeTypeId;
	}

	/**
	 * Sets the MIME-TYPE ID of this <code>Link</code>
	 * 
	 * @param mimeTypeId MIME-TYPE ID.
	 */
	public void setMimeTypeId(int mimeTypeId) {
		this.mimeTypeId = mimeTypeId;
	}

	/**
	 * Returns the URL string of this <code>Link</code>
	 * 
	 * @return the url string
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL string of this <code>Link</code>
	 * @param url an URL tring
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Returns the MD5 hash of the URL of this <code>Link</code>
	 * @return a MD5 hash string
	 */
	public String getUrlHash() {
		return urlHash;
	}

	/**
	 * Sets the MD5 hash of the URL of this <code>Link</code>
	 * 
	 * @param urlHash the MD5 of the URL of this <code>Link</code>
	 */
	public void setUrlHash(String urlHash) {
		this.urlHash = urlHash;
	}
	
	/**
	 * Creates a new instance of this class
	 */
	public Link(){
		this.count = 0;
		this.id = -1;
	}

	/**
	 * Creates a new instance of this class with a defined <code>Link</code> ID
	 * @param id Link ID
	 */
	public Link(int id){
		this.id = id;
	}
	
	/**
	 * Copy constructor for <code>Link</code>
	 * @param link an object to copy from.
	 */
	public Link(Link link){		
		this.id = link.id;
		this.mimeTypeId = link.mimeTypeId;		
		this.url = link.url;	
		this.urlHash = link.urlHash;	
		this.count = link.count;
	}
	
	/**
	 * Creates a new instance of this class with a defined URL string
	 * @param url the URL string of this <code>Link</code>
	 */
	public Link(String url){
		this();
		this.url = url;
	}

	/**
	 * Creates a new instance of this class with a defined URL string and MIME-TYPE id.
	 * @param url the URL string of this <code>Link</code>
	 * @param mimeTypeId the MIME-TYPE ID of this <code>Link</code>
	 */
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
