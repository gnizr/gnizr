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

import java.util.List;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.bookmark.FolderTagListener;
import com.gnizr.core.bookmark.ForUserListener;
import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.managers.ForUserManager;
import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;

public class TestCrawlRssFeed extends GnizrWebappTestBase {
	
	private BookmarkManager bookmarkManager;
	private FeedSubscriptionManager feedManager;
	private ThreadPoolTaskExecutor executor;
	private CrawlRssFeed crawlRssFeed;
	private FolderManager folderManager ;
	private ForUserManager forUserManager;
	private UserManager userManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		executor = new ThreadPoolTaskExecutor();
		executor.initialize();
		
		userManager = new UserManager(getGnizrDao());
		forUserManager = new ForUserManager(getGnizrDao());
		folderManager = new FolderManager(getGnizrDao());
		
		bookmarkManager = new BookmarkManager(getGnizrDao());
		bookmarkManager.addBookmarkListener(new ForUserListener(userManager,forUserManager));
		bookmarkManager.addBookmarkListener(new FolderTagListener(folderManager));
		feedManager = new FeedSubscriptionManager(getGnizrDao());
		crawlRssFeed = new CrawlRssFeed();
		crawlRssFeed.setServiceEnabled(true);
		crawlRssFeed.setBookmarkManager(bookmarkManager);
		crawlRssFeed.setFeedSubscriptionManager(feedManager);
		crawlRssFeed.setThreadPoolTaskExecutor(executor);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestCrawlRssFeed.class
				.getResourceAsStream("/TestCrawlRssFeed-input.xml"));
	}

	public void testRun() throws Exception{
		Timer timer = new Timer();
		timer.schedule(crawlRssFeed,0);
		crawlRssFeed.awaitAndShutdown(60,TimeUnit.SECONDS);
				
		Folder folder66 = folderManager.getUserFolder(new User(2), "latest news");
		Folder folder67 = folderManager.getUserFolder(new User(2), "news2");
		assertTrue(folder66.getSize()>0);
		assertEquals(folder66.getSize(),folder67.getSize());
		
		Folder importFolder = folderManager.getUserFolder(new User(2), FolderManager.IMPORTED_BOOKMARKS_LABEL);
		assertTrue(importFolder.getSize()>0);
		
		Folder folder69 = folderManager.getUserFolder(new User(2),"georss data");
		assertTrue(folder69.getSize()>0);
		DaoResult<Bookmark> result2 = folderManager.pageFolderContent(new User(2),"georss data",0,1);
		Bookmark bm = result2.getResult().get(0);
		List<PointMarker> markers =  bookmarkManager.getPointMarkers(bm);
		assertTrue(markers.size()>0);
	}
}
