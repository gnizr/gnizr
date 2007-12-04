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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;
import com.gnizr.db.vocab.MIMEType;

public class TestFeedSubscribeListener extends GnizrCoreTestBase {

	private FeedSubscribeListener listener;
	private BookmarkManager bookmarkManager;
	private FeedSubscriptionManager feedManager;
	
	protected void setUp() throws Exception {
		super.setUp();		
		bookmarkManager = new BookmarkManager(getGnizrDao());			
		feedManager = new FeedSubscriptionManager(getGnizrDao());				
		
		listener = new FeedSubscribeListener(feedManager);				
		bookmarkManager.addBookmarkListener(listener);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		bookmarkManager.shutdown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestFeedSubscribeListener.class.getResourceAsStream("/TestFeedSubscribeListener-input.xml"));
	}

	public void testDeleteBookmark() throws Exception{
		FeedSubscription sub = feedManager.getSubscription(400);
		assertNotNull(sub);
		
		boolean deleted = bookmarkManager.deleteBookmark(new Bookmark(300));
		assertTrue(deleted);
		
		bookmarkManager.shutdown();
		
		sub = feedManager.getSubscription(400);
		assertNull(sub);
	}
	
	public void testAddBookmark() throws Exception{
		Bookmark b = new Bookmark();
		b.setTitle("some feed");
		b.setLink(new Link("http://link.com/rss",MIMEType.APP_RSS_XML));
		b.setTags("123 subscribe:this foo bar");
		b.setUser(new User(1));
		
		int id = bookmarkManager.addBookmark(b);
		assertTrue((id >0));
		bookmarkManager.shutdown();
	
		FeedSubscription sub = feedManager.getSubscription(new User(1),"http://link.com/rss");
		assertNotNull(sub);
	}
	
	public void testUpdateBookmark() throws Exception{		
		Bookmark bm = bookmarkManager.getBookmark(301);
		bm.setTags("subscribe:this abc");
		bookmarkManager.updateBookmark(bm);
		bookmarkManager.shutdown();
		
		FeedSubscription feed = feedManager.getSubscription(new User(1), "http://rss.slashdot.org/Slashdot/slashdot");
		assertNotNull(feed);
	}
	
}
