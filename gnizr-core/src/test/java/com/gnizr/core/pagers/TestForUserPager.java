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
package com.gnizr.core.pagers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.managers.ForUserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.vocab.TimeRange;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.User;

public class TestForUserPager extends GnizrCoreTestBase {

	private ForUserPager forUserPager;
	private ForUserManager forUserManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		forUserPager = new ForUserPager(getGnizrDao());
		forUserManager = new ForUserManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestForUserPager.class.getResourceAsStream("/TestForUserPager-input.xml"));
	}
	
	public void testMaxPageNumberUser() throws Exception{
		User user = new User(2);
		assertEquals(1,forUserPager.getMaxPageNumber(user,10));
		assertEquals(8,forUserPager.getMaxPageNumber(user,1));
		assertEquals(3,forUserPager.getMaxPageNumber(user,3));
	}
	
	public void testPageForUser() throws Exception{
		User user = new User(2);
		List<ForUser> forUsers = forUserPager.pageForUser(user,0,2);
		assertEquals(2,forUsers.size());
		assertNotNull(forUsers.get(0).getForUser().getUsername());
		assertNotNull(forUsers.get(0).getBookmark().getLink());
		forUsers = forUserPager.pageForUser(user,50,1);
		assertEquals(0,forUsers.size());
		forUsers = forUserPager.pageForUser(user,5,10);
		assertEquals(3,forUsers.size());
		forUsers = forUserPager.pageForUser(user,-1,10);
		assertEquals(0,forUsers.size());		
	}
	
	private void addSampleForUserRecord() throws Exception{		
		Date d1 = GnizrDaoUtil.getNow();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(d1);
		cal.add(Calendar.DAY_OF_WEEK, -1);
		Date d2 = cal.getTime();
		
		cal.setTime(d1);
		cal.add(Calendar.MONTH,-1);
		Date d3 = cal.getTime();
		
		User user = new User(3);
		// created on "today"
		ForUser f1 = new ForUser(user,new Bookmark(300),"",d1);
		// created on "1 day ago"
		ForUser f2 = new ForUser(user,new Bookmark(301),"",d2);
		// created on "last month today"
		ForUser f3 = new ForUser(user,new Bookmark(302),"",d3);
		
		int id = -1;
		id = forUserManager.addForUser(f1);
		assertTrue((id >0));
		id = forUserManager.addForUser(f2);
		assertTrue((id >0));
		id = forUserManager.addForUser(f3);
		assertTrue((id >0));
	}
	
	public void testGetMaxNumberInTimeRange() throws Exception{
		addSampleForUserRecord();
		User user = new User(3);
		int cnt = forUserPager.getMaxPageNumber(user,TimeRange.TODAY,1);
		assertTrue((cnt == 1));
		
		cnt = forUserPager.getMaxPageNumber(user,TimeRange.YESTERDAY,1);
		assertTrue((cnt == 1));
		
		cnt = forUserPager.getMaxPageNumber(user,TimeRange.LAST_7_DAYS,1);
		assertTrue((cnt == 2));
		
		cnt = forUserPager.getMaxPageNumber(user,TimeRange.LAST_7_DAYS,10);
		assertTrue((cnt == 1));
		
		cnt = forUserPager.getMaxPageNumber(user,TimeRange.THIS_MONTH,10);
		assertTrue((cnt == 1));
		
		cnt = forUserPager.getMaxPageNumber(user,TimeRange.LAST_MONTH,10);
		assertTrue((cnt == 1));
	}
	
	public void testPageTimeRangeToday() throws Exception{
		addSampleForUserRecord();
		User user = new User(3);
		
		List<ForUser> results = forUserPager.pageForUser(user,TimeRange.TODAY,0,10);
		assertEquals(1,results.size());
		assertNotNull(results.get(0).getBookmark().getUser().getUsername());
		assertNotNull(results.get(0).getForUser().getUsername());
	}
	
	public void testPageTimeRangeYesterday() throws Exception{
		addSampleForUserRecord();
		User user = new User(3);
		
		List<ForUser> results = forUserPager.pageForUser(user,TimeRange.YESTERDAY,0,10);
		assertEquals(1,results.size());
		assertNotNull(results.get(0).getBookmark().getUser().getUsername());
		assertNotNull(results.get(0).getForUser().getUsername());
	}
	
	public void testPageTimeRangeLast7Days() throws Exception{
		addSampleForUserRecord();
		User user = new User(3);
		
		List<ForUser> results = forUserPager.pageForUser(user,TimeRange.LAST_7_DAYS,0,10);
		assertEquals(2,results.size());
		assertNotNull(results.get(0).getBookmark().getUser().getUsername());
		assertNotNull(results.get(1).getForUser().getUsername());
		
		Date d1 = results.get(0).getCreatedOn();
		Date d2 = results.get(1).getCreatedOn();
		assertTrue(d1.after(d2));
		
		results = forUserPager.pageForUser(user,TimeRange.LAST_7_DAYS,1,10);
		assertEquals(1,results.size());
		assertNotNull(results.get(0).getBookmark().getUser().getUsername());
		assertNotNull(results.get(0).getForUser().getUsername());
	}
	
	public void testPageTimeRangeThisMonth() throws Exception{
		addSampleForUserRecord();
		User user = new User(3);
		List<ForUser> results = forUserPager.pageForUser(user,TimeRange.THIS_MONTH,0,10);
		assertTrue((results.size()>=1));
	}
	
	public void testPageTimeRangeLastMonth() throws Exception{
		addSampleForUserRecord();
		User user = new User(3);
		List<ForUser> results = forUserPager.pageForUser(user,TimeRange.LAST_MONTH,0,10);
		assertTrue(results.size()>=1);
	}
	
	
}
