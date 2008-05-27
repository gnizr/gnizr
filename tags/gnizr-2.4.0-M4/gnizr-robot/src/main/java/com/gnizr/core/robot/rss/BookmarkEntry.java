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
package com.gnizr.core.robot.rss;

import java.util.ArrayList;
import java.util.List;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.PointMarker;


public class BookmarkEntry {

	private Bookmark bookmark;
	private List<PointMarker> pointMarkers;
	
	public BookmarkEntry(){
		this.bookmark = null;
		this.pointMarkers = new ArrayList<PointMarker>();
	}
	
	public BookmarkEntry(Bookmark bookmark, List<PointMarker> pointMarkers){
		this.bookmark = bookmark;
		this.pointMarkers = pointMarkers;
	}
	
	public BookmarkEntry(BookmarkEntry bmEntry){
		this();
		if(bmEntry != null){
			this.bookmark = new Bookmark(bmEntry.bookmark);
			if(bmEntry.pointMarkers != null){
				for(PointMarker pm : bmEntry.pointMarkers){
					this.pointMarkers.add(new PointMarker(pm));
				}
			}
		}
	}

	public Bookmark getBookmark() {
		return bookmark;
	}

	public void setBookmark(Bookmark bookmark) {
		this.bookmark = bookmark;
	}

	public List<PointMarker> getPointMarkers() {
		return pointMarkers;
	}

	public void setPointMarkers(List<PointMarker> pointMarkers) {
		this.pointMarkers = pointMarkers;
	}
	
}
