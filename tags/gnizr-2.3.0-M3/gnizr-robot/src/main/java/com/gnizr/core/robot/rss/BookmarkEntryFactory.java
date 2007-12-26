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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.vocab.MachineTags;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.tag.TagsParser;
import com.sun.syndication.feed.module.georss.GeoRSSModule;
import com.sun.syndication.feed.module.georss.GeoRSSUtils;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class BookmarkEntryFactory {

	private static final Logger logger = Logger.getLogger(BookmarkEntryFactory.class);
	
	public BookmarkEntry createEntry(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		String title = createTitle(entry, fromFeed, fromSubscription);
		String notes = createNotes(entry, fromFeed, fromSubscription);
		Date createdOn = createDateCreatedOn(entry, fromFeed, fromSubscription);
		Date lastUpdated = createDateLastUpdated(entry, fromFeed, fromSubscription);
		List<String> tags = createTags(entry, fromFeed, fromSubscription);
		List<MachineTag> machineTags = createMachineTags(entry, fromFeed, fromSubscription);
		User user = createUser(entry, fromFeed, fromSubscription);
		Link link = createLink(entry, fromFeed, fromSubscription);
		List<PointMarker> pm = createPointMarkers(entry, fromFeed, fromSubscription);

		BookmarkEntry newBmEntry = null;
		if(title != null && user != null && link != null){
			Bookmark bm = new Bookmark(user,link);
			if(tags != null && tags.isEmpty() == false){
				StringBuffer sb = new StringBuffer();
				for(String t : tags){
					sb.append(t.trim());
					sb.append(' ');
				}
				bm.setTags(sb.toString().trim());
			}
			if(machineTags != null && machineTags.isEmpty() == false){
				StringBuffer sb = new StringBuffer();
				for(MachineTag mt : machineTags){
					sb.append(mt.toString().trim());
					sb.append(' ');
				}
				String tagline = bm.getTags();
				if(tagline != null){
					sb.append(tagline);
				}
				bm.setTags(sb.toString().trim());
			}
			bm.setTitle(title);
			if(notes != null){
				bm.setNotes(notes);
			}else{
				bm.setNotes("");
			}
			bm.setCreatedOn(createdOn);
			bm.setLastUpdated(lastUpdated);
			newBmEntry = new BookmarkEntry();
			newBmEntry.setBookmark(bm);
			if(pm != null && pm.isEmpty() == false){
				newBmEntry.setPointMarkers(pm);
			}
		}		
		return newBmEntry;
	}
	
	protected String createTitle(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		return entry.getTitle();
	}
	
	protected String createNotes(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		SyndContent content = entry.getDescription();
		if(content != null){
			return content.getValue();
		}
		return "";
	}
	
	protected List<String> createTags(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		List<String> tags = new ArrayList<String>();
		List categories = entry.getCategories();
		for (Iterator it = categories.iterator(); it.hasNext();) {
			SyndCategory cat = (SyndCategory) it.next();
			String catStr = cat.getName();			
			if (catStr != null && catStr.length() > 0) {
				TagsParser parser = null;
				if(catStr.contains(",") == true){
					parser = new TagsParser(catStr,"[,]+");
					List<String> parsedTags = parser.getTags();
					for(String t: parsedTags){
						t = t.trim();
						if(t.length() > 0){
							tags.add(t);
						}
					}
				}else{
					catStr = catStr.replaceAll("[\\s]+","");
					tags.add(catStr);
				}
			}
		}
		List<MachineTag> machineTags = fromSubscription.getBookmark().getMachineTagList();
		for(MachineTag mt : machineTags){
			if(MachineTags.TAG_PRED.equals(mt.getPredicate()) == true &&
			  (mt.getNsPrefix() == null || mt.getNsPrefix().equals(MachineTags.NS_GNIZR))){
				String taglabel = mt.getValue();
				taglabel = taglabel.trim();
				if(taglabel != null && (taglabel.length() > 0) && 
				   tags.contains(taglabel) == false){
					tags.add(taglabel);
				}
			}
		}
		return tags;
	}
	
	protected List<MachineTag> createMachineTags(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		List<MachineTag> mtags = new ArrayList<MachineTag>();
		List<String> folders = fromSubscription.getImportFolders();
		if(folders.size() > 0){
			for(String fldr : folders){
				mtags.add(MachineTags.GN_FOLDER(fldr));
			}
		}else{
			mtags.add(MachineTags.GN_FOLDER(FolderManager.IMPORTED_BOOKMARKS_LABEL));			
		}
		return mtags;
	}
		
	protected User createUser(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		User subscriber = fromSubscription.getBookmark().getUser();
		return new User(subscriber);
	}
	
	protected Link createLink(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		String linkUrl = entry.getLink();
		if(linkUrl != null){
			return new Link(entry.getLink());
		}else{
			logger.debug("SyndEntry getLink() returns null. Entry: " + entry);
		}
		return null;
	}
	
	protected List<PointMarker> createPointMarkers(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		List<PointMarker> pm = new ArrayList<PointMarker>();
		GeoRSSModule geoRSSModule = GeoRSSUtils.getGeoRSS(entry);
		if(geoRSSModule != null){
			double lat = geoRSSModule.getPosition().getLatitude();
			double lng = geoRSSModule.getPosition().getLongitude();
			PointMarker p = new PointMarker();
			p.setNotes("lat/lon: " + lat+","+lng);
			p.setPoint(lng,lat);
			pm.add(p);
		}
		return pm;
	}
	
	protected Date createDateCreatedOn(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		Date d = entry.getPublishedDate();
		if(d == null){
			return GnizrDaoUtil.getNow();
		}
		return d;
	}
	
	protected Date createDateLastUpdated(SyndEntry entry, SyndFeed fromFeed, FeedSubscription fromSubscription){
		Date d = entry.getUpdatedDate();
		if(d == null){
			return GnizrDaoUtil.getNow();
		}
		return d;
	}
}
