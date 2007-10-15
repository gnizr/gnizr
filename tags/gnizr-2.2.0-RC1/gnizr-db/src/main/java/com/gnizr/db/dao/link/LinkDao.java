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
package com.gnizr.db.dao.link;

import java.io.Serializable;
import java.util.List;

import com.gnizr.db.dao.Link;


public interface LinkDao extends Serializable{
	public int createLink(Link link);
	public Link getLink(int id);
	public boolean deleteLink(int id);
	public boolean updateLink(Link link);
	public List<Link> findLink(String url);
	public List<Link> findLinkByUrlHash(String urlHash);
	
	/*
	public List<Link> pageLink(Tag tag, int offset, int count);
		
	public int getLinkCount(Tag tag);	
	public Link getLinkViewRecord(int id);
	
	public int getTextSearchResultCount(String searchQuery);
	public int getTagSearchResultCount(String searchQuery);
	public List<Link> pageTextSearch(String searchQuery, int offset, int count);
	public List<Link> pageTagSearch(String searchQuery, int offset, int count);
	*/
}
