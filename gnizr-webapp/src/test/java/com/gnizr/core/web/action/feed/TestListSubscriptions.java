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

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestListSubscriptions extends GnizrWebappTestBase {

	private ListSubscriptions action;
	private FeedSubscriptionManager feedManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		feedManager = new FeedSubscriptionManager(getGnizrDao());
		action = new ListSubscriptions();
		action.setFeedSubscriptionManager(feedManager);
		action.setUserManager(new UserManager(getGnizrDao()));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestListSubscriptions.class.getResourceAsStream("/TestListSubscriptions-input.xml"));
	}
	
	public void testGo() throws Exception{
		action.setLoggedInUser(new User("hchen1"));		
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		List<FeedSubscription> subs = action.getSubscriptions();
		assertEquals(1,subs.size());
		assertEquals(20,subs.get(0).getId());
	}

	
	public void testGo2() throws Exception{
		action.setLoggedInUser(new User("hchen1"));
		action.setUsername("gnizr");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		List<FeedSubscription> subs = action.getSubscriptions();
		assertEquals(0,subs.size());		
	}
}
