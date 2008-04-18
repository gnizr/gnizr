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
package com.gnizr.web.action.link;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.web.action.SessionConstants;
import com.gnizr.web.action.bookmark.TestListBookmark;
import com.gnizr.web.action.link.ListLink;
import com.opensymphony.xwork.ActionSupport;

public class TestListLink extends GnizrWebappTestBase {

	private BookmarkPager linkPager;
	private Map session;
	private ListLink listLinkAction;
	
	protected void setUp() throws Exception {
		super.setUp();
		linkPager = new BookmarkPager(getGnizrDao());
		session = new HashMap();
		listLinkAction = new ListLink();
		listLinkAction.setBookmarkPager(linkPager);
		listLinkAction.setSession(session);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		session.clear();
		listLinkAction = null;
		
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestListBookmark.class.getResourceAsStream("/TestListLink-input.xml"));
	}
	
	@SuppressWarnings("unchecked")
	public void testListLinkByTag1() throws Exception{
		listLinkAction.setTag("web");
		assertEquals(ActionSupport.SUCCESS,listLinkAction.execute());
		List<Bookmark> results = listLinkAction.getBookmarks();
		assertEquals(2,results.size());	
	}

	@SuppressWarnings("unchecked")
	public void testListLinkByTag2() throws Exception{
		listLinkAction.setTag("web");
		session.put(SessionConstants.PAGE_COUNT,1);
		assertEquals(ActionSupport.SUCCESS,listLinkAction.execute());
		List<Bookmark> results = listLinkAction.getBookmarks();
		assertEquals(1,results.size());
		
		Bookmark link1 = results.get(0);
		assertEquals("housingmaps",link1.getTitle());	
	}
	
	@SuppressWarnings("unchecked")
	public void testListLinkByTag3() throws Exception{
		listLinkAction.setTag("web");
		listLinkAction.setPage(2);
		session.put(SessionConstants.PAGE_COUNT,1);
		assertEquals(ActionSupport.SUCCESS,listLinkAction.execute());
		List<Bookmark> results = listLinkAction.getBookmarks();
		assertEquals(1,results.size());
		
		Bookmark link1 = results.get(0);
		assertEquals("morecols",link1.getTitle());	
	}
}
