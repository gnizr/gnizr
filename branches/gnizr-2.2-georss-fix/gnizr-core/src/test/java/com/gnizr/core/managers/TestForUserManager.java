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
package com.gnizr.core.managers;

import java.util.Date;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.User;

public class TestForUserManager extends GnizrCoreTestBase {

	private ForUserManager manager;
	
	protected void setUp() throws Exception {
		super.setUp();
		manager = new ForUserManager(getGnizrDao());		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestForUserManager.class
				.getResourceAsStream("/TestForUserManager-input.xml"));
	}
	
	public void testAddForUser() throws Exception{
		User jsmith = new User(2);
		Bookmark bm = new Bookmark(300);
		Date date = GnizrDaoUtil.getNow();
		ForUser forUser = new ForUser(jsmith,bm,"helloworld",date);
		int id = manager.addForUser(forUser);
		assertTrue((id > 0));
	}
	
	public void testHasForUser() throws Exception{
		User jsmith = new User(2);
		Bookmark bm = new Bookmark(301);
		assertTrue(manager.hasForUser(bm, jsmith));
		assertFalse(manager.hasForUser(new Bookmark(4000), jsmith));
	}
	
	public void testGetForUserCount() throws Exception{
		int cnt = manager.getForUserCount(new User(2));
		assertEquals(2,cnt);
	}
	
	public void testGetForUserById() throws Exception{
		ForUser forUser = manager.getForUser(401);
		assertNotNull(forUser);
		assertNotNull(forUser.getForUser().getUsername());
		assertNotNull(forUser.getBookmark().getLink().getUrl());
	}
	
	public void testPageForUser() throws Exception{
		DaoResult<ForUser> result = manager.pageForUser(new User("jsmith"), new User("hchen1"), 0, 10);
		assertEquals(2,result.getSize());
	}
	
	public void testDeleteAllForUser() throws Exception{
		assertTrue(manager.deleteAllForUser(new User("jsmith")));
		
		DaoResult<ForUser> result = manager.pageForUser(new User("jsmith"), new User("hchen1"), 0, 10);
		assertEquals(0,result.getSize());		
	}
	
	public void testDeleteForUserByIds() throws Exception{
		assertTrue(manager.deleteForUserById(new User("jsmith"), new int[]{401}));
		
		DaoResult<ForUser> result = manager.pageForUser(new User("jsmith"), new User("hchen1"), 0, 10);
		assertEquals(1,result.getSize());	
	}
	
	public void testListForUserSenders() throws Exception{
		List<User> senders = manager.listForUserSenders(new User("jsmith"));
		assertEquals(1,senders.size());
		assertEquals("hchen1",senders.get(0).getUsername());
	}
}
