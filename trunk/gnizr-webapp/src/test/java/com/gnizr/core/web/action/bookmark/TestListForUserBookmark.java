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
package com.gnizr.core.web.action.bookmark;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.foruser.ForUserManager;
import com.gnizr.core.foruser.ForUserPager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestListForUserBookmark extends GnizrWebappTestBase {

	private ForUserManager forUserManager;
	private ForUserPager forUserPager;
	private ListForUserBookmark action;
	
	protected void setUp() throws Exception {
		super.setUp();
		forUserManager = new ForUserManager(getGnizrDao());
		forUserPager = new ForUserPager(getGnizrDao());
		action = new ListForUserBookmark();
		action.setForUserManager(forUserManager);
		action.setForUserPager(forUserPager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestListForUserBookmark.class
				.getResourceAsStream("/TestListForUserBookmark-input.xml"));
	}
	
	public void testDoGetTotalCount() throws Exception{
		action.setLoggedInUser(new User("hchen1"));
		String code = action.doGetTotalCount();
		assertEquals(ActionSupport.SUCCESS,code);
		assertEquals(8,action.getTotalCount());
	}

}
