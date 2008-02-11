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
package com.gnizr.db.dao.user;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.UserStat;

public class TestUserDBDao2 extends GnizrDBTestBase {

	private UserDao dao;
	
	public TestUserDBDao2(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		dao = new UserDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserDBDao2.class.getResourceAsStream("/dbunit/userdbdao/TestUserDBDao2-input.xml"));
	}
	
	public void testListUserStats() throws Exception{
		List<UserStat> stats = dao.listUserStats();
		assertEquals(2,stats.size());
		
		UserStat s1 = stats.get(0);
		UserStat s2 = stats.get(1);
		
		assertEquals("hchen1",s1.getUsername());
		assertEquals(3,s1.getNumOfBookmarks());
		assertEquals(1,s1.getNumOfTags());
		
		assertEquals("hchen2",s2.getUsername());
		assertEquals(1,s2.getNumOfBookmarks());
		assertEquals(1,s2.getNumOfTags());
	}
	
}
