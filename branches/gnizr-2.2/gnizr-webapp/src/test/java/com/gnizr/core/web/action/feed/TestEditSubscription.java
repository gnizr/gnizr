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
package com.gnizr.core.web.action.feed;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestEditSubscription extends GnizrWebappTestBase {

	private EditSubscription action;
	private FeedSubscriptionManager feedManager;
	private UserManager userManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
		feedManager = new FeedSubscriptionManager(getGnizrDao());
		action = new EditSubscription();
		action.setFeedSubscriptionManager(feedManager);
		action.setUserManager(userManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestEditSubscription.class.getResourceAsStream("/TestEditSubscription-input.xml"));
	}

	public void testCreateFeed() throws Exception{
		String feedUrl = "http://java.com/rss";
		action.setLoggedInUser(new User("hchen1"));
		action.setFeedUrl(feedUrl);
		action.setFeedTitle("java feed");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS, code);
		
		FeedSubscription feed = feedManager.getSubscription(new User("hchen1"),feedUrl );
		assertNotNull(feed);
		assertEquals(feedUrl,feed.getBookmark().getLink().getUrl());
	}
	
	public void testCreateFeed2() throws Exception{
		String feedUrl = "http://rss.slashdot.org/Slashdot/slashdot";
		User user = new User(1);
		
		action.setLoggedInUser(user);
		action.setFeedUrl(feedUrl);
		action.setFeedTitle("fjdskla");

		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		FeedSubscription feed = action.getFeed();
		assertEquals(20,feed.getId());
		assertEquals("Slashdot Feed",feed.getBookmark().getTitle());		
	}
	
	public void testDeleteSubscription() throws Exception{
		String feedUrl = "http://rss.slashdot.org/Slashdot/slashdot";
		User user = new User(1);
		
		action.setLoggedInUser(user);
		action.setFeedUrl(feedUrl);
		action.setFeedTitle("fjdskla");
		
		String code = action.doDeleteSubscription();
		assertEquals(ActionSupport.SUCCESS,code);
		
		FeedSubscription feed = feedManager.getSubscription(new User("hchen1"),feedUrl );
		assertNull(feed);
	}
	
	
}
