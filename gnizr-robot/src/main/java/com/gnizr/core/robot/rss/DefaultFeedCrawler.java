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

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.FeedSubscription;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class DefaultFeedCrawler implements FeedCrawler{

	private static final Logger logger = Logger.getLogger(DefaultFeedCrawler.class);
	
	private BookmarkEntryFactory entryFactory;
	
	public DefaultFeedCrawler(){
		entryFactory = new BookmarkEntryFactory();
	}
	
	public DefaultFeedCrawler(BookmarkEntryFactory factory){
		this.entryFactory = factory;
	}
	
	public FeedCrawlResult doCrawl(FeedSubscription feed) {
		logger.info("Trying to crawl: " + feed.getBookmark().getLink().getUrl());
		List<BookmarkEntry> entries = null;
		Date feedLastUpdated = GnizrDaoUtil.getNow();
		Date feedPubDate = null;
		String feedUrl = feed.getBookmark().getLink().getUrl();
		SyndFeed syndFeed = fetchFeed(feedUrl);
		if (syndFeed != null) {
			logger.info("Successfully fetched: " + feedUrl);
			try {
				if(syndFeed.getPublishedDate() != null){				
					feedPubDate = syndFeed.getPublishedDate();
				}else{
					feedPubDate = feedLastUpdated;
				}
				if(feed.getLastSync() == null || feed.getPubDate() == null){
					logger.info("doCrawl for the very first time: " + feedUrl);
					entries = processSyndFeed(syndFeed, feed);
				}else if(feed.getPubDate() != null){										
					if(feedPubDate.after(feed.getPubDate())){
						logger.info("doCrawl because remote server pubDate is newer: " + feedUrl + ". Recorded PubDate="
								+feed.getPubDate() + " Server PubDate="+feedPubDate);
						entries = processSyndFeed(syndFeed, feed);
					}else{
						logger.info("doCrawl skip because pubDate is up-to-date: " + ". Recorded PubDate="
								+feed.getPubDate() + " Server PubDate="+feedPubDate);
					}
				}
			} catch (Exception e) {
				logger.error("Error processing feed: "
						+ feed.getBookmark().getLink().getUrl(), e);
			}
		}else{
			logger.debug("RSS feed doesn't seem to be valid: " + feed.getBookmark().getLink().getUrl());
		}
		if(entries != null){
			logger.info("Total number of BookmarkEntry created: " + entries.size());
		}
		return new FeedCrawlResult(entries,feedLastUpdated,feedPubDate);
	}
	
	private SyndFeed fetchFeed(String feedUrl){		
		SyndFeed feed = null;
		try{
			SyndFeedInput input = new SyndFeedInput();
			feed = input.build(new XmlReader(new URL(feedUrl)));
		}catch(Exception e){
			logger.error("error reading feed: " + feedUrl);
			logger.debug("error reading feed exception: " + feedUrl,e);
		}		
		return feed;
	}

	@SuppressWarnings("unchecked")
	private List<BookmarkEntry> processSyndFeed(SyndFeed syndFeed, FeedSubscription fromSubs){	
		List<BookmarkEntry> entries = new ArrayList<BookmarkEntry>();
		List<SyndEntry> syndEntries = syndFeed.getEntries();
		for(SyndEntry e : syndEntries){
			logger.debug("Creating BookmarkEntry for SyndEntry: " + e.getTitle());
			BookmarkEntry newEntry = entryFactory.createEntry(e, syndFeed, fromSubs);
			if(newEntry != null){
				entries.add(newEntry);
			}
		}
		return entries;
	}
}
