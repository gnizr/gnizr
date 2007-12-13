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
package com.gnizr.db.dao.subscription;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.User;

public class TestFeedSubscriptionDBDao extends GnizrDBTestBase {
	
	private FeedSubscriptionDBDao feedDao;
	
	public TestFeedSubscriptionDBDao (String name)
	{
		super (name);
	}
	
	protected void setUp () throws Exception {
		super.setUp();
		feedDao = new FeedSubscriptionDBDao (getDataSource());
	}

	protected void tearDown () throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestFeedSubscriptionDBDao.class.getResourceAsStream("/dbunit/feeddbdao/TestFeedSubscriptionDBDao1-input.xml"));
	}
	
	public void testGetFeedSubscriptionById() throws Exception{
		FeedSubscription feed = feedDao.getSubscription(10);
		assertNotNull(feed);
		assertEquals(10,feed.getId());
		assertEquals(2,feed.getBookmark().getUser().getId());
		assertEquals(202,feed.getBookmark().getLink().getId());
		List<String> tags = feed.getBookmark().getTagList();
		assertTrue(tags.contains("news"));
		assertTrue(tags.contains("cnn"));
		
		assertEquals(true, feed.isAutoImport());
		assertEquals("",feed.getMatchText());
	
		assertEquals(2,feed.getImportFolders().size());
		assertTrue(feed.getImportFolders().contains("my folder1"));
		assertTrue(feed.getImportFolders().contains("my folder2"));
		
		feed = feedDao.getSubscription(30);
		assertNull(feed);
		
		feed = feedDao.getSubscription(20);
		assertNotNull(feed);
		assertEquals(false, feed.isAutoImport());
		assertEquals("foobar stuff",feed.getMatchText());
	}
	
	public void testGetFeedSubscriptionByUserIdFeedUrl() throws Exception{
		String url = "http://rss.slashdot.org/Slashdot/slashdot";
		FeedSubscription feed = feedDao.getSubscription(new User(1),url);
		assertNotNull(feed);
		assertEquals(20,feed.getId());
		assertEquals(1,feed.getBookmark().getUser().getId());
		assertEquals(203,feed.getBookmark().getLink().getId());
		assertEquals(3,feed.getImportFolders().size());
			
		assertEquals(false, feed.isAutoImport());
		assertEquals("foobar stuff",feed.getMatchText());	
	}

	public void testCreateFeedSubscription() throws Exception{
		FeedSubscription feed = new FeedSubscription();
		feed.setBookmark(new Bookmark(302));
		feed.setAutoImport(true);
		feed.setLastSync(null);
		feed.setMatchText("pop");
		
		int id = feedDao.createSubscription(feed);
		assertTrue(id>0);
		feed = feedDao.getSubscription(id);
		assertEquals(302,feed.getBookmark().getId());
		assertEquals("pop",feed.getMatchText());
		
		int id2 = feedDao.createSubscription(feed);
		assertEquals(id,id2);
	}
	
	public void testDeleteFeedSubscription() throws Exception{
		FeedSubscription f = feedDao.getSubscription(new User(1), "http://rss.slashdot.org/Slashdot/slashdot");
		assertNotNull(f);
		
		boolean okay = feedDao.deleteSubscription(new User(1), "http://rss.slashdot.org/Slashdot/slashdot");
		assertTrue(okay);
		 
		f = feedDao.getSubscription(new User(1), "http://rss.slashdot.org/Slashdot/slashdot");
		assertNull(f);
	}
	
	public void testUpdateFeedSubscription() throws Exception{
		Date now = GregorianCalendar.getInstance().getTime(); 
		
		FeedSubscription feed = feedDao.getSubscription(10);
		assertEquals(300,feed.getBookmark().getId());
		assertEquals("",feed.getMatchText());
		assertEquals(true,feed.isAutoImport());
		assertTrue(now.after(feed.getLastSync()));
		assertNull(feed.getPubDate());
		
		feed.setBookmark(new Bookmark(302));
		feed.setMatchText("abcd 123");
		feed.setAutoImport(false);
		feed.setLastSync(now);
		feed.setPubDate(now);
		
		boolean okay = feedDao.updateSubscription(feed);
		assertTrue(okay);
		
		feed = feedDao.getSubscription(10);
		assertEquals(302,feed.getBookmark().getId());
		assertEquals("abcd 123",feed.getMatchText());
		assertEquals(false,feed.isAutoImport());
		assertNotNull(feed.getPubDate());	
	}
	
	public void testListImportFolders() throws Exception{
		List<Folder> folders = feedDao.listImportFolder(new FeedSubscription(10));
		assertEquals(2,folders.size());
		
		folders = feedDao.listImportFolder(new FeedSubscription(20));
		assertEquals(3,folders.size());
		
		for(Folder f : folders){
			if(f.getId() == 1){
				assertEquals(2,f.getUser().getId());
				assertEquals(0,f.getSize());
			}else if(f.getId() == 2){
				assertEquals(2,f.getUser().getId());
				assertEquals(0,f.getSize());
			}else if(f.getId() == 3){
				assertEquals(1,f.getUser().getId());
				assertEquals(1,f.getSize());
			}
		}
	}
	
	public void testAddImportFolder() throws Exception{
		List<Folder> folders = feedDao.listImportFolder(new FeedSubscription(10));
		assertEquals(2,folders.size());
		
		List<Folder> f2add = new ArrayList<Folder>();
		f2add.add(new Folder(3));
		
		int cnt = feedDao.addImportFolders(new FeedSubscription(10), f2add);
		assertEquals(1,cnt);
		
		folders = feedDao.listImportFolder(new FeedSubscription(10));
		assertEquals(3,folders.size());	
		
		cnt = feedDao.addImportFolders(new FeedSubscription(10), f2add);
		folders = feedDao.listImportFolder(new FeedSubscription(10));
		assertEquals(3,folders.size());	
	}
	
	public void testRemoveImportFolder() throws Exception{
		List<Folder> folders = feedDao.listImportFolder(new FeedSubscription(20));
		assertEquals(3,folders.size());
		
		List<Folder> f2rm = new ArrayList<Folder>();
		f2rm.add(new Folder(3));
		
		int cnt = feedDao.removeImportFolders(new FeedSubscription(20), f2rm);
		assertEquals(1,cnt);
		
		folders = feedDao.listImportFolder(new FeedSubscription(20));
		assertEquals(2,folders.size());		
		f2rm.add(new Folder(1));
		f2rm.add(new Folder(2));
		
		cnt = feedDao.removeImportFolders(new FeedSubscription(20), f2rm);
		assertEquals(2,cnt);
		
		folders = feedDao.listImportFolder(new FeedSubscription(20));
		assertEquals(0,folders.size());		
	}
	
	public void testPageSubscriptionByOwnerId() throws Exception{
		DaoResult<FeedSubscription> result = feedDao.pageSubscription(new User(1),0,10);
		assertEquals(1,result.getSize());
		assertEquals(1,result.getResult().size());
		
		FeedSubscription feed = result.getResult().get(0);
		assertEquals(3,feed.getImportFolders().size());
		
		
		FeedSubscription newFeed = new FeedSubscription();
		newFeed.setBookmark(new Bookmark(302));
		newFeed.setAutoImport(false);
		newFeed.setMatchText("999");
		
		int id = feedDao.createSubscription(newFeed);
		assertTrue(id > 0);
		
		result = feedDao.pageSubscription(new User(1),0,10);
		assertEquals(2,result.getSize());
		assertEquals(2,result.getResult().size());
		
		result = feedDao.pageSubscription(new User(1),1,1);
		assertEquals(2,result.getSize());
		assertEquals(1,result.getResult().size());
		
		result = feedDao.pageSubscription(new User(1),10,1000);
		assertEquals(2,result.getSize());
		assertEquals(0,result.getResult().size());
	}
	
	public void testListAutoImportSubscription() throws Exception{
		List<FeedSubscription> feeds = feedDao.listAutoImportSubscription(2);
		assertEquals(1,feeds.size());
		assertEquals(10,feeds.get(0).getId());
		assertNotNull(feeds.get(0).getBookmark().getUser().getFullname());
		assertNotNull(feeds.get(0).getBookmark().getLink().getUrlHash());
	}
	
}
