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
package com.gnizr.core.feed;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;

public class TestFeedSubscriptionManager extends GnizrCoreTestBase {

	
	private FeedSubscriptionManager feedManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		feedManager = new FeedSubscriptionManager(getGnizrDao());		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestFeedSubscriptionManager.class
				.getResourceAsStream("/TestFeedSubscriptionManager-input.xml"));
	}
	
	public void testGetSubscriptionById() throws Exception{
		FeedSubscription feed = feedManager.getSubscription(20);
		assertNotNull(feed);
		assertEquals(301,feed.getBookmark().getId());
		assertEquals("foobar stuff",feed.getMatchText());
	}
	
	public void testGetSubscriptionByOwnerIdFeedUrl() throws Exception{
		FeedSubscription feed = feedManager.getSubscription(new User("hchen1"), "http://rss.slashdot.org/Slashdot/slashdot");
		assertEquals(301,feed.getBookmark().getId());
		assertEquals(20,feed.getId());
		
		feed = feedManager.getSubscription(new User("jsmith"), "http://rss.cnn.com/rss/cnn_topstories.rss");
		assertEquals(10,feed.getId());
		assertEquals(300,feed.getBookmark().getId());
	}
	
	public void testCreateSubscription() throws Exception{
		User user = new User("jsmith");
		String feedUrl = "http://foo.com/rss";
		String feedTitle = "hello foo.com rss";
		FeedSubscription feed = feedManager.createSubscription(user, feedUrl, feedTitle);
		assertNotNull(feed);
		assertEquals(feedUrl,feed.getBookmark().getLink().getUrl());
		assertEquals(feedTitle, feed.getBookmark().getTitle());
		assertEquals(2,feed.getBookmark().getUser().getId());		
		
		user = new User("hchen1");
		feedUrl = "http://rss.slashdot.org/Slashdot/slashdot";
		feedTitle = "xyz";
		feed = feedManager.createSubscription(user, feedUrl, feedTitle);
		assertNotNull(feed);
		assertEquals(feedUrl,feed.getBookmark().getLink().getUrl());
		assertEquals("Slashdot Feed",feed.getBookmark().getTitle());
		assertEquals(20,feed.getId());
	}
	
	public void testDeleteSubscription() throws Exception{
		FeedSubscription feed = feedManager.getSubscription(20);
		assertNotNull(feed);
		
		boolean okay = feedManager.deleteSubscription(new User("hchen1"), 
				"http://rss.slashdot.org/Slashdot/slashdot");
		assertTrue(okay);
		
		feed = feedManager.getSubscription(20);
		assertNull(feed);
	}

	public void testUpdateSubscription() throws Exception{
		FeedSubscription feed = feedManager.getSubscription(20);
		assertNotNull(feed);
		assertFalse(feed.isAutoImport());
		assertEquals("foobar stuff",feed.getMatchText());	
		
		feed.setAutoImport(true);
		feed.setMatchText("abcdefg");
		
		boolean okay = feedManager.updateSubscription(feed);
		assertTrue(okay);
		
		feed =  feedManager.getSubscription(20);
		assertNotNull(feed);
		assertTrue(feed.isAutoImport());
		assertEquals("abcdefg",feed.getMatchText());	
	}
	
	public void testPageSubscription() throws Exception{
		User user = new User("hchen1");
		DaoResult<FeedSubscription> result = feedManager.pageSubscription(user,0,10);
		assertEquals(1,result.getSize());
		assertEquals(1,result.getResult().size());
		assertNotNull(result.getResult().get(0).getBookmark().getUser().getFullname());
		assertNotNull(result.getResult().get(0).getBookmark().getLink().getUrlHash());			
	}
	
	public void testListSubscription() throws Exception{
		User user = new User("jsmith");
		DaoResult<FeedSubscription> result = feedManager.listSubscription(user);
		assertEquals(1,result.getSize());
		assertEquals(1,result.getResult().size());
		assertNotNull(result.getResult().get(0).getBookmark().getUser().getFullname());
		assertNotNull(result.getResult().get(0).getBookmark().getLink().getUrlHash());	
	}
	
	public void testListImportFolder() throws Exception{
		User feedOwner = new User("hchen1");
		String feedUrl = "http://rss.slashdot.org/Slashdot/slashdot";
		
		List<Folder> folders = feedManager.listImportFolder(feedOwner, feedUrl);
		assertEquals(3,folders.size());
		
		feedOwner = new User("jsmith");
		feedUrl = "http://rss.cnn.com/rss/cnn_topstories.rss";
		folders = feedManager.listImportFolder(feedOwner, feedUrl);
		assertEquals(2,folders.size());
	}
	
	public void testRemoveImportFolder() throws Exception{
		User feedOwner = new User("hchen1");
		String feedUrl = "http://rss.slashdot.org/Slashdot/slashdot";
		
		List<Folder> folders = feedManager.listImportFolder(feedOwner, feedUrl);
		assertEquals(3,folders.size());
		
		List<String> folder2rm = new ArrayList<String>();		
		folder2rm.add("MY FOLDER1");	
		int cnt = feedManager.removeImportFolders(feedOwner,feedUrl,feedOwner,folder2rm);
		assertEquals(1,cnt);
		
		folder2rm.add("my folder1");
		cnt = feedManager.removeImportFolders(feedOwner,feedUrl,new User("jsmith"),folder2rm);
		assertEquals(1,cnt);
		
		folders = feedManager.listImportFolder(feedOwner, feedUrl);
		assertEquals(1,folders.size());
		Folder f =folders.get(0);
		assertEquals(2,f.getId());
		assertEquals("my folder2", f.getName());
	}
	
	public void testAddImportFolder() throws Exception{
		User feedOwner = new User(2);
		String feedUrl = "http://rss.cnn.com/rss/cnn_topstories.rss";
		
		List<Folder> folders = feedManager.listImportFolder(feedOwner, feedUrl);
		assertEquals(2,folders.size());
		
		List<String> folder2add = new ArrayList<String>();
		folder2add.add("f1");
		folder2add.add("f2");
		folder2add.add("f3");
		int cnt = feedManager.addImportFolders(feedOwner, feedUrl,null,feedOwner, folder2add);
		assertEquals(3,cnt);
		
		folders = feedManager.listImportFolder(feedOwner, feedUrl);
		assertEquals(5,folders.size());
	}
	
	public void testAddImportFolder2() throws Exception{
		User feedOwner = new User(2);
		String feedUrl = "http://foobar/rss";
		String feedTitle = "foobar feed";

		List<String> folder2add = new ArrayList<String>();
		folder2add.add("f1");
		folder2add.add("f2");
		folder2add.add("f3");
		int cnt = feedManager.addImportFolders(feedOwner, feedUrl,feedTitle,feedOwner, folder2add);
		assertEquals(3,cnt);
		
		List<Folder> folders = feedManager.listImportFolder(feedOwner, feedUrl);
		assertEquals(3,folders.size());
		
		FeedSubscription feed = feedManager.getSubscription(feedOwner, feedUrl);
		assertEquals(feedUrl,feed.getBookmark().getLink().getUrl());
		assertEquals(feedTitle,feed.getBookmark().getTitle());
	}
	
}
