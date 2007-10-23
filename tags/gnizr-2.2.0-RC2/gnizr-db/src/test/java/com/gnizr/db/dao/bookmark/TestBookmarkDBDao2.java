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
package com.gnizr.db.dao.bookmark;

import java.util.Calendar;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.user.TestUserDBDao1;

public class TestBookmarkDBDao2 extends GnizrDBTestBase {

	private BookmarkDBDao bmDao;
	
	public TestBookmarkDBDao2(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		bmDao = new BookmarkDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserDBDao1.class.getResourceAsStream("/dbunit/bmarkdbdao/TestBookmarkDBDao2-input.xml"));
	}

	public void testGetBookmark() throws Exception{
		Bookmark bm = bmDao.getBookmark(300);		
		String title = bm.getTitle();
		String expectedTitle = "Yahoo! Hong Kong - 雅虎香港";
		assertEquals(expectedTitle,title);
		
		bm = bmDao.getBookmark(301);
		title = bm.getTitle();
		expectedTitle = "زيباري يرفض تدويل الملف العراقي والهاشمي يصل دمشق";
		assertEquals(expectedTitle,title);
	}
	
	public void testCreateBookmark() throws Exception{
		Link link  = new Link(202);
		Bookmark bm = new Bookmark();
		bm.setUser(new User(1));
		bm.setLink(link);
		bm.setTitle("Yahoo! Hong Kong - 雅虎香港");
		bm.setCreatedOn(Calendar.getInstance().getTime());
		bm.setLastUpdated(Calendar.getInstance().getTime());
		int id = bmDao.createBookmark(bm);
		assertTrue((id > 0));
	}
	 
}
