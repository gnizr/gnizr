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
package com.gnizr.core;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.TestBookmarkManager;
import com.gnizr.core.managers.LinkManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;

public class TestLinkHistory extends GnizrCoreTestBase {

	private LinkManager linkHistory;
	
	protected void setUp() throws Exception {
		super.setUp();
		linkHistory = new LinkManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkManager.class
				.getResourceAsStream("/TestLinkHistory-input.xml"));
	}
	
	public void testGetInfo() throws Exception{
		Link link = linkHistory.getInfo("97014960532642d4a2038b7f7361efab");
		assertEquals("http://www.springframework.org/docs/reference/beans.html",link.getUrl());
		//assertEquals(1,link.getUser().getId());
		//assertEquals("springframework java programming",link.getTags());
	}
	
	public void testGetHistory() throws Exception{
		Link link = new Link(208);
		List<Bookmark> result = linkHistory.getHistory(link);
		assertEquals(1,result.size());
		Bookmark bm = result.get(0);
		assertEquals("Wii Play: Cow Racing",bm.getTitle());
	}

}
