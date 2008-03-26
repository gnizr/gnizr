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
package com.gnizr.core.search;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

public class TestBookmarkTagSearchResult extends GnizrCoreTestBase {

	private BookmarkManager bmManager;
	
	public TestBookmarkTagSearchResult(){
		bmManager = new BookmarkManager(getGnizrDao());
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		User user1 = new User(1);
		Link link1 = new Link(1);
		Link link2 = new Link(2);
		Link link3 = new Link(3);
		
		Bookmark bm1 = new Bookmark(user1,link1);
		bm1.setTags("tags1 tags2 tags3");
		bm1.setTitle("title foobar1");
		
		Bookmark bm2 = new Bookmark(user1,link2);
		bm2.setTags("tags4 tags5 tags6");
		bm2.setTitle("title foobar1");
		
		Bookmark bm3 = new Bookmark(user1,link3);
		bm3.setTags("tags1 tags");
		bm3.setTitle("title3 foobar2");
		
		assertTrue(bmManager.addBookmark(bm1)>0);
		assertTrue(bmManager.addBookmark(bm2)>0);
		assertTrue(bmManager.addBookmark(bm3)>0);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSearchTag() throws Exception{
		BookmarkTagSearchResult r = new BookmarkTagSearchResult(getGnizrDao(),"tags4 tags");
		assertEquals(5,r.length());
		BookmarkTag bt = r.getResult(4);
		assertNotNull(bt.getBookmark().getLink().getUrlHash());
		assertEquals("gnizr",bt.getBookmark().getUser().getUsername());
	}
	
	public void testSearchText() throws Exception{
		BookmarkTagSearchResult r = new BookmarkTagSearchResult(getGnizrDao(),"foobar2");
		assertEquals(2,r.length());	
		
		assertEquals(3,r.getResult(0).getBookmark().getLink().getId());
		assertEquals(3,r.getResult(1).getBookmark().getLink().getId());
	}
	

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkTagSearchResult.class
				.getResourceAsStream("/TestSearchBookmarkTagResult-input.xml"));
	}

}
