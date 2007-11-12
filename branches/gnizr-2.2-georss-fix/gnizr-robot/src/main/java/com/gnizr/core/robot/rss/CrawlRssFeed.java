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
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.PointMarker;

public class CrawlRssFeed extends TimerTask{

	private static final Logger logger = Logger.getLogger(CrawlRssFeed.class);
	
	private FeedSubscriptionManager feedSubscriptionManager;
	private BookmarkManager bookmarkManager;
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	private FeedCrawlerFactory crawlerFactory;
	private ExecutorService saveBookmarkEntriesExecutor;
	private boolean serviceEnabled;
	private int ageHour;

	public CrawlRssFeed(){
		saveBookmarkEntriesExecutor = Executors.newSingleThreadExecutor();
		serviceEnabled = false;
		ageHour = 2;
	}
	
	public FeedCrawlerFactory getCrawlerFactory() {
		return crawlerFactory;
	}

	public void setCrawlerFactory(FeedCrawlerFactory crawlerFactory) {
		this.crawlerFactory = crawlerFactory;
	}

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public FeedSubscriptionManager getFeedSubscriptionManager() {
		return feedSubscriptionManager;
	}

	public void setFeedSubscriptionManager(
			FeedSubscriptionManager feedSubscriptionManager) {
		this.feedSubscriptionManager = feedSubscriptionManager;
	}

	public ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
		return threadPoolTaskExecutor;
	}

	public void setThreadPoolTaskExecutor(ThreadPoolTaskExecutor taskExecutor) {
		this.threadPoolTaskExecutor = taskExecutor;
	}
	
	public void shutdown() {
		logger.info("CrawlRssFeed shutdown.");
		saveBookmarkEntriesExecutor.shutdownNow();
	}
	
	public void awaitAndShutdown(long time, TimeUnit timeUnit){
		if(saveBookmarkEntriesExecutor != null){
			try {
				saveBookmarkEntriesExecutor.awaitTermination(time,TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				logger.error("error shutdown saveBookmarkEntriesExecutor.",e);
			}
		}
	}
	
	@Override
	public void run() {		
		logger.debug("CrawlRssFeed run() starts.");
		if(threadPoolTaskExecutor == null){
			logger.error("ThreadPoolTaskExecutor is not defined.");
			return;
		}
		int curActiveThread = threadPoolTaskExecutor.getActiveCount();
		boolean isInitOkay = isInitialized() ;
		logger.info("Total number of active TaskExecutor thread: " + curActiveThread);
		logger.info("Is initialized properly: " + isInitOkay);
		logger.info("Is service enabled: " + serviceEnabled);
		if(isInitOkay == true && serviceEnabled == true && curActiveThread == 0){
			logger.info("Crawling starts... ThreadId=" + Thread.currentThread().getId());
			List<FeedSubscription> feeds = feedSubscriptionManager.listAutoImportSubscription(getAgeHour());
			logger.info("Total number of subscriptions to process: " + feeds.size());
			FeedCrawlerFactory factory = getDefaultFactoryIfNull();
			for(FeedSubscription aFeed : feeds){
				FeedCrawler crawler = factory.createFeedCrawler();
				threadPoolTaskExecutor.execute(new FeedCrawlerRunnable(crawler,aFeed));
			}
			logger.info("Crawling ends... ThreadId=" + Thread.currentThread().getId());
		}else if(serviceEnabled == false){
			logger.debug("CrawlRSSFeed service is currently turned off.");
		}else{
			logger.info("Skip this run of CrawlRssFeed.");
		}
		logger.debug("CrawlRssFeed run() ends.");
	}
	
	public boolean isServiceEnabled() {
		return serviceEnabled;
	}

	public void setServiceEnabled(boolean serviceEnabled) {
		this.serviceEnabled = serviceEnabled;
		logger.debug("CrawlRSSFeed setServiceEnabled: " + serviceEnabled);
	}

	private void processDoCrawlResult(FeedCrawlResult result, FeedSubscription srcFeed){
		try{
			saveBookmarkEntriesExecutor.execute(new SaveBookmarkEntryRunnable(result,srcFeed));
		}catch(Exception e){
			logger.error("can't submit new tasks to saveBookmarkEntriesExecutor",e);
		}
	}
	
	private class SaveBookmarkEntryRunnable implements Runnable{
		private List<BookmarkEntry> entries;
		private FeedSubscription srcFeed;
		private Date feedLastUpdated;
		private Date feedPubDate;
		
		public SaveBookmarkEntryRunnable(FeedCrawlResult result,FeedSubscription srcFeed){
			if(result.getEntries() != null){
				this.entries = result.getEntries();
			}else{
				this.entries = new ArrayList<BookmarkEntry>();
			}
			this.feedLastUpdated = result.getFeedLastUpdated();
			this.feedPubDate = result.getFeedPubDate();
			this.srcFeed = srcFeed;
		}
		
		public void run() {
			logger.debug("SaveBookmarkEntryRunnable run() starts");
			try{				
				boolean doReplace = shouldRepalceOnChange(srcFeed);
				logger.debug("On Change Do Repalce: " + doReplace + " feed: " + srcFeed.getBookmark().getLink().getUrl());
				for(BookmarkEntry entry : entries){
					Bookmark bm = entry.getBookmark();
					int oldBmId = bookmarkManager.getBookmarkId(bm.getUser(),bm.getLink().getUrl());	
					if(oldBmId > 0 && doReplace == true){
						logger.debug("old bookmark exists.");
						Bookmark oldBm = bookmarkManager.getBookmark(oldBmId);
						Date oldBmLastUpdated = oldBm.getLastUpdated();
						Date newBmLastUpdated = bm.getLastUpdated();
						if(oldBmLastUpdated != null && newBmLastUpdated != null &&
						   oldBmLastUpdated.before(newBmLastUpdated) == true){
							if(bookmarkManager.deleteBookmark(new Bookmark(oldBmId)) == false){
								logger.error("unable to delete bookmark of id: " + oldBmId);
								continue;
							}	
							logger.debug("deleted on old bookmark before add it as a new");
							oldBmId = 0;
						}
					}
					if(oldBmId <= 0){
						logger.debug("adding bookmark: " + bm);
						int newBmId = bookmarkManager.addBookmark(bm);
						if(newBmId > 0){
							logger.debug("add succeed!");
							bm.setId(newBmId);
							List<PointMarker> pm = entry.getPointMarkers();
							if(pm != null && pm.isEmpty() == false){
								logger.debug("adding # PointMarkers: " + pm.size());
								bookmarkManager.addPointMarkers(bm,pm);
							}
						}
					}										
				}
				if(feedLastUpdated != null){
					FeedSubscription feed = new FeedSubscription(srcFeed);
					feed.setLastSync(feedLastUpdated);
					feed.setPubDate(feedPubDate);
					feedSubscriptionManager.updateSubscription(feed);
					logger.debug("set FeedSubscription lastUpdate to: " + feedLastUpdated);
				}else{
					logger.error("feedLastUpdate is NULL");
				}
			}catch(Exception e){
				logger.error("error saving bookmark entries: " + entries,e);
			}
			logger.debug("SaveBookmarkEntryRunnable run() ends");
		}
		
		private boolean shouldRepalceOnChange(FeedSubscription feed){
			boolean doReplace = false;
			List<MachineTag> machineTags = feed.getBookmark().getMachineTagList();
			for(MachineTag mt : machineTags){
				if(mt.getPredicate().equalsIgnoreCase("onchange") == true &&
				   mt.getValue().equalsIgnoreCase("replace")){
					doReplace = true;
					break;
				}
			}	
			return doReplace;
		}
	}
	
	private class FeedCrawlerRunnable implements Runnable{
		private FeedCrawler crawler;
		private FeedSubscription feed2crawl;
		
		public FeedCrawlerRunnable(FeedCrawler crawler, FeedSubscription feed){
			this.crawler = crawler;
			this.feed2crawl = feed;
		}
		
		public void run() {
			logger.debug("FeedCrawlerRunnable run() starts...");
			FeedCrawlResult result = this.crawler.doCrawl(feed2crawl);
			processDoCrawlResult(result,feed2crawl);
			logger.debug("FeedCrawlerRunnable run() ends.");
		}
	}
	
	private FeedCrawlerFactory getDefaultFactoryIfNull(){
		if(getCrawlerFactory() == null){
			logger.warn("No FeedCrawlerFactory is defined. Use DefaultFeedCrawlerFactory.");
			crawlerFactory = new DefaultFeedCrawlerFactory();
		}
		return crawlerFactory;
	}

	private boolean isInitialized() {
		if(getBookmarkManager() == null){
			logger.error("BookmarkManager is not initialized.");
			return false;
		}
		if(getFeedSubscriptionManager() == null){
			logger.error("FeedSubscriptionManager is not initialized.");
			return false;
		}
		if(getThreadPoolTaskExecutor() == null){
			logger.error("TaskExecutor is not initialized.");
			return false;
		}
		return true;
	}
	
	public int getAgeHour() {
		return ageHour;
	}

	public void setAgeHour(int ageHour) {
		this.ageHour = ageHour;
	}

}
