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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.FeedSubscription;

public class TestDefaultFeedCrawler extends GnizrWebappTestBase {

	private DefaultFeedCrawler crawler;
	private BookmarkEntryFactory entryFactory;
	private FeedSubscriptionManager feedManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		entryFactory = new BookmarkEntryFactory();
		crawler = new DefaultFeedCrawler(entryFactory);
		feedManager = new FeedSubscriptionManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDoCrawl1() throws Exception{
		FeedSubscription slashgeo = feedManager.getSubscription(400);
		assertNotNull(slashgeo);
		FeedCrawlResult result = crawler.doCrawl(slashgeo);
		assertNotNull(result);		
		assertEquals(25,result.getEntries().size());
		boolean foundGeoRss = false;
		for(BookmarkEntry e : result.getEntries()){
			if(e.getPointMarkers().isEmpty() == false){
				foundGeoRss = true;
				break;
			}
		}
		assertTrue(foundGeoRss);
	}
	
	public void testDoCrawl2() throws Exception{
		FeedSubscription feed402 = feedManager.getSubscription(402);
		FeedCrawlResult result = crawler.doCrawl(feed402);
		assertEquals(159,result.getEntries().size());
		boolean foundGeoRss = false;
		for(BookmarkEntry e: result.getEntries()){
			if(e.getPointMarkers().isEmpty() == false){
				foundGeoRss = true;
				break;
			}
		}
		assertTrue(foundGeoRss);
	}


	public void testDoCrawl3() throws Exception{
		FeedSubscription feed403 = feedManager.getSubscription(403);
		FeedCrawlResult result = crawler.doCrawl(feed403);
		assertTrue(result.getEntries().size() > 0);
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestDefaultFeedCrawler.class
				.getResourceAsStream("/TestDefaultFeedCrawler-input.xml"));
	}
}
