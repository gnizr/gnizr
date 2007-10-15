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
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class TestUserDBDao3 extends GnizrDBTestBase {

	private UserDBDao userDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		userDao = new UserDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserDBDao3.class.getResourceAsStream("/dbunit/userdbdao/TestUserDBDao3-input.xml"));
	}
	
	
	public void testListTagGroups1() throws Exception{
		Map<String, List<UserTag>> tagGroups = userDao.listTagGroups(new User(1), 1, UserDao.SORT_BY_TAG_ALPHA, UserDao.ASCENDING);
		assertEquals(2,tagGroups.size());
		
		List<UserTag> g1 = tagGroups.get("g1");
		assertEquals(2,g1.size());	
		assertEquals("webwork",g1.get(0).getLabel());
		assertEquals("wii",g1.get(1).getLabel());
		
		List<UserTag> g2 = tagGroups.get("g2");
		assertEquals(2,g2.size());
		assertEquals("webwork",g2.get(0).getLabel());
		assertEquals("wii",g2.get(1).getLabel());
		
	}
	
	public void testListTagGroups2() throws Exception{
		Map<String, List<UserTag>> tagGroups = userDao.listTagGroups(new User(1), 0, UserDao.SORT_BY_TAG_ALPHA, UserDao.ASCENDING);
		assertEquals(3,tagGroups.size());
		
		List<UserTag> g1 = tagGroups.get("g1");
		assertEquals(4,g1.size());	
		assertEquals("games",g1.get(0).getLabel());
		assertEquals("videogame",g1.get(1).getLabel());
		assertEquals("webwork",g1.get(2).getLabel());
		assertEquals("wii",g1.get(3).getLabel());
		
		List<UserTag> g2 = tagGroups.get("g2");
		assertEquals(2,g2.size());
		assertEquals("webwork",g2.get(0).getLabel());
		assertEquals("wii",g2.get(1).getLabel());
		
		List<UserTag> nogroup = tagGroups.get("");
		assertEquals(2,nogroup.size());
	}
	
	public void testListTagGroups3() throws Exception{
		Map<String, List<UserTag>> tagGroups = userDao.listTagGroups(new User(1), 0, UserDao.SORT_BY_TAG_ALPHA, UserDao.DESCENDING);
		assertEquals(3,tagGroups.size());
		
		List<UserTag> g1 = tagGroups.get("g1");
		assertEquals(4,g1.size());	
		assertEquals("wii",g1.get(0).getLabel());
		assertEquals("webwork",g1.get(1).getLabel());
		assertEquals("videogame",g1.get(2).getLabel());
		assertEquals("games",g1.get(3).getLabel());
		
		List<UserTag> g2 = tagGroups.get("g2");
		assertEquals(2,g2.size());
		assertEquals("wii",g2.get(0).getLabel());
		assertEquals("webwork",g2.get(1).getLabel());
	}
	

	public void testListTagGroups4() throws Exception{
		Map<String, List<UserTag>> tagGroups = userDao.listTagGroups(new User(1), 1, UserDao.SORT_BY_TAG_USAGE_FREQ, UserDao.ASCENDING);
		assertEquals(2,tagGroups.size());
		
		List<UserTag> g1 = tagGroups.get("g1");
		assertEquals(2,g1.size());	
		assertEquals("wii",g1.get(0).getLabel());
		assertEquals(1,g1.get(0).getCount());
		assertEquals("webwork",g1.get(1).getLabel());
		assertEquals(2,g1.get(1).getCount());
		
		List<UserTag> g2 = tagGroups.get("g2");
		assertEquals(2,g2.size());
		assertEquals("wii",g2.get(0).getLabel());
		assertEquals("webwork",g2.get(1).getLabel());
	}
	
}
