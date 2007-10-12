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
package com.gnizr.core.web.action.clustermap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.Tag;

public class ClusterMapTool {

	public static void clusterBookmarkTags(List<BookmarkTag> bookmarkTags, Map<String, List<Integer>> cluster, List<Bookmark> bookmarks, List<Tag> tags){
		if(cluster == null){
			throw new NullPointerException("cluster is null");
		}
		if(bookmarks == null){
			throw new NullPointerException("bookmarks is null");		
		}
		if(tags == null){
			throw new NullPointerException("tags is null");
		}
		
		List<Integer> rootList = cluster.get("root");
		if(rootList == null){
			rootList = new ArrayList<Integer>();
			cluster.put("root", rootList);
		}
		
		Tag curTag = null;
	
		List<Integer> bmIdSeen = new ArrayList<Integer>();
		List<Integer> curBmList = null;
		
		for(BookmarkTag bt : bookmarkTags){
			if(curTag == null || curTag.getId() != bt.getTag().getId()){
				curTag = bt.getTag();
				curBmList = new ArrayList<Integer>();
				cluster.put(String.valueOf(curTag.getId()),curBmList);
				tags.add(curTag);
			}				
			int bmId = bt.getBookmark().getId();
			curBmList.add(bmId);			
			if(bmIdSeen.contains(bmId) == false){
				bmIdSeen.add(bmId);
				rootList.add(bmId);
				bookmarks.add(bt.getBookmark());					
			}						
		}				
	}
}
