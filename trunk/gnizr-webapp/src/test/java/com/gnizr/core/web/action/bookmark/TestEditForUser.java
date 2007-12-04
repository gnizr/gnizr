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
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestEditForUser extends GnizrWebappTestBase {

	private EditForUser action;
	private ForUserManager forUserManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		forUserManager = new ForUserManager(getGnizrDao());
		action = new EditForUser();
		action.setForUserManager(forUserManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestEditForUser.class.getResourceAsStream("/TestEditForUser-input.xml"));
	}
	
	public void testDoPurgeForUser() throws Exception{
		DaoResult<ForUser> result = forUserManager.pageForUser(new User("jsmith"), new User("hchen1"), 0, 10);
		assertEquals(2,result.getSize());
		
		action.setLoggedInUser(new User(2));
		String r = action.doPurgeForUser();
		assertEquals(ActionSupport.SUCCESS,r);
		
		result = forUserManager.pageForUser(new User("jsmith"), new User("hchen1"), 0, 10);
		assertEquals(0,result.getSize());
	}

	
	public void testDoDeleteForUser() throws Exception{
		DaoResult<ForUser> result = forUserManager.pageForUser(new User("jsmith"), new User("hchen1"), 0, 10);
		assertEquals(2,result.getSize());
		
		action.setLoggedInUser(new User(2));
		action.setDeleteForUserId(new int[]{400, 500});
		String r = action.doDeleteForUser();
		assertEquals(ActionSupport.SUCCESS,r);
		
		result = forUserManager.pageForUser(new User("jsmith"), new User("hchen1"), 0, 10);
		assertEquals(1,result.getSize());
	}
}
