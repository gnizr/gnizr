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
package com.gnizr.core.bookmark;

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.util.Geo;
import com.gnizr.core.util.GeonamesSearch;
import com.gnizr.core.util.TagUtil;
import com.gnizr.core.vocab.MachineTags;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.PointMarker;

public class GeonamesTagListener implements BookmarkListener{

	private static final Logger logger = Logger.getLogger(GeonamesSearch.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 5219906939098644756L;
	
	public GeonamesTagListener(){
	
	}

	public void notifyAdded(BookmarkManager bookmarkManager,Bookmark bookmark) throws Exception {
		List<MachineTag> geonamesTags = 
			bookmark.getMachineTagList(MachineTags.NS_GNIZR,
					MachineTags.GEONAMES_PRED);
		for(MachineTag mt : geonamesTags){
			try{
				addGeonamesPointMarker(bookmarkManager,bookmark, mt);
			}catch(Exception e){
				logger.error("error creating PointMarker using geonames service: " + mt, e);
			}			
		}
	}

	private void addGeonamesPointMarker(BookmarkManager bookmarkManager,Bookmark bookmark, MachineTag mt) throws NoSuchUserException, NoSuchLinkException, MissingIdException, NoSuchBookmarkException{
		String placename = TagUtil.mapUnderscoreToSpace(mt.getValue());
		Geo geo = GeonamesSearch.searchGeonames(placename);
		if(geo != null){
			PointMarker pm = new PointMarker();
			pm.setPoint(geo.getLongitude(), geo.getLatitude());
			pm.setNotes(placename);				
			bookmarkManager.addPointMarker(bookmark,pm);
		}
	}
		
	public void notifyDeleted(BookmarkManager bookmarkManager,Bookmark bookmark) throws Exception {
		// do nothing, let the DB to clean up on any geometry markers that
		// have been created previously.
	}

	public void notifyUpdated(BookmarkManager bookmarkManager,Bookmark oldBookmark, Bookmark newBookmark) throws Exception {
		List<MachineTag> oldTags = 
			oldBookmark.getMachineTagList(MachineTags.NS_GNIZR,
					MachineTags.GEONAMES_PRED);
		List<MachineTag> newTags = newBookmark.getMachineTagList(MachineTags.NS_GNIZR,
				MachineTags.GEONAMES_PRED);
		for(MachineTag newMT : newTags){
			if(oldTags.contains(newMT) == false){
				try{
					addGeonamesPointMarker(bookmarkManager,newBookmark,newMT);
				}catch(Exception e){
					logger.error("error creating PointMarker using geonames service: " + newMT, e);
				}	
			}
		}
	}

}
