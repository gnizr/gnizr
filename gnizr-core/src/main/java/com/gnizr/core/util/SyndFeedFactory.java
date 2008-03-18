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
package com.gnizr.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.search.BookmarkDoc;
import com.gnizr.db.dao.Bookmark;
import com.sun.syndication.feed.atom.Link;
import com.sun.syndication.feed.module.Module;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.module.opensearch.impl.OpenSearchModuleImpl;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;

/**
 * An utility class for creating ROME {@link SyndFeed}} object from a list of {@link Bookmark} or 
 * {@link BookmarkDoc}
 * 
 * @author Harry Chen
 *
 */
public class SyndFeedFactory {

	private static final Logger logger = Logger.getLogger(SyndFeedFactory.class);
	
	
	public static SyndFeed createFromBookmarkDoc(List<BookmarkDoc> bookmarks, String author, String title, String link, Date pubDate, String feedUri){
		logger.debug("SyndFeedFactory create method called");
		logger.debug("bookmarks="+bookmarks);
		logger.debug("author="+author);
		logger.debug("title="+title);
		logger.debug("link="+link);
		logger.debug("pubDate="+pubDate);
		logger.debug("feedUri="+feedUri);
		SyndFeed syndFeed = new SyndFeedImpl();
		syndFeed.setAuthor(author);
		syndFeed.setTitle(title);
		syndFeed.setUri(feedUri);
		syndFeed.setLink(link);
		syndFeed.setPublishedDate(pubDate);
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		for(BookmarkDoc bmark : bookmarks){
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(bmark.getTitle());
			entry.setAuthor(bmark.getUsername());
			entry.setLink(bmark.getUrl());
			entry.setUri("urn-x:gnizr:bookmark:"+bmark.getBookmarkId());		
			SyndContent cnt = new SyndContentImpl();
			cnt.setType("text/html");
			if(bmark.getNotes() != null){
				cnt.setValue(bmark.getNotes());
				entry.setDescription(cnt);
			}		
			entries.add(entry);			
		}
		syndFeed.setEntries(entries);
		syndFeed.setEncoding("UTF-8");
		logger.debug("done initializing syndFeed object");
		return syndFeed;
	}
	
	public static SyndFeed create(List<Bookmark> bookmarks, String author, String title, String link, Date pubDate, String feedUri){
		logger.debug("SyndFeedFactory create method called");
		logger.debug("bookmarks="+bookmarks);
		logger.debug("author="+author);
		logger.debug("title="+title);
		logger.debug("link="+link);
		logger.debug("pubDate="+pubDate);
		logger.debug("feedUri="+feedUri);
		SyndFeed syndFeed = new SyndFeedImpl();
		syndFeed.setAuthor(author);
		syndFeed.setTitle(title);
		syndFeed.setUri(feedUri);
		syndFeed.setLink(link);
		syndFeed.setPublishedDate(pubDate);
		List<SyndEntry> entries = new ArrayList<SyndEntry>();
		for(Bookmark bmark : bookmarks){
			SyndEntry entry = new SyndEntryImpl();
			entry.setTitle(bmark.getTitle());
			entry.setAuthor(bmark.getUser().getUsername());
			entry.setLink(bmark.getLink().getUrl());
			entry.setUri("urn:bookmark:"+bmark.getId());
			entry.setUpdatedDate(bmark.getLastUpdated());
			List<String> tags = bmark.getTagList();
			List<SyndCategory> cats = new ArrayList<SyndCategory>();
			for(String aTag : tags){
				SyndCategory aCat = new SyndCategoryImpl();
				aCat.setName(aTag);
				cats.add(aCat);
			}
			if(cats.isEmpty() == false){
				entry.setCategories(cats);
			}
			SyndContent cnt = new SyndContentImpl();
			cnt.setType("text/html");
			cnt.setValue(bmark.getNotes());
			entry.setDescription(cnt);
			entries.add(entry);			
		}
		syndFeed.setEntries(entries);
		syndFeed.setEncoding("UTF-8");
		logger.debug("done initializing syndFeed object");
		return syndFeed;
	}
	
	
	@SuppressWarnings("unchecked")
	public static SyndFeed addOpenSearchModule(SyndFeed feed, int itemsPerPage, int startIdx, int totalResult, String searchDescriptionUrl){
		if(feed == null){
			throw new NullPointerException("feed is NULL");
		}
		List<Module> mods = null;
		mods = feed.getModules();
		if(mods == null){
			mods = new ArrayList<Module>();
		}
		OpenSearchModule osm = new OpenSearchModuleImpl();
		osm.setItemsPerPage(itemsPerPage);
		osm.setStartIndex(startIdx);
		osm.setTotalResults(totalResult);
		if(searchDescriptionUrl != null){
			Link link = new Link();
			link.setHref(searchDescriptionUrl);
			link.setType("application/opensearchdescription+xml");
			osm.setLink(link);
		}
		mods.add(osm);
		feed.setModules(mods);
		return feed;
	}
	

}
