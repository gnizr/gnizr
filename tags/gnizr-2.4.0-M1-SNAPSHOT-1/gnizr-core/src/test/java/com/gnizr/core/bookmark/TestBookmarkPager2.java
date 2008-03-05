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
package com.gnizr.core.bookmark;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;

public class TestBookmarkPager2 extends GnizrCoreTestBase {

	//private TagDao tagDao;
	private BookmarkPager bookmarkPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		//tagDao = getGnizrDao().getTagDao();
		bookmarkPager = new BookmarkPager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkPager.class
				.getResourceAsStream("/TestBookmarkPager2-input.xml"));
	}
	
	public void testPageBookmarkByTag() throws Exception{
		Tag webTag = new Tag("web");
		List<Bookmark> results = bookmarkPager.pageBookmark(webTag, 0,100);
		assertEquals(2,results.size());
		Bookmark bookmark = results.get(0);
		assertEquals("http://www.housingmaps.com/",bookmark.getLink().getUrl());
	}
	
	
	public void testPageAllBookmarkUser() throws Exception{
		User user = new User("hchen1");
		List<Bookmark> results = bookmarkPager.pageAllBookmark(user).getResult();
		assertEquals(3,results.size());
		
		
		user = new User("hchen2");
		results = bookmarkPager.pageAllBookmark(user).getResult();
		assertEquals(1,results.size());
	}
	
	
	public void testPageAllBookmarkLink() throws Exception{
		Link link = new Link("http://www.csee.umbc.edu/~finin/");
		List<Bookmark> results = bookmarkPager.pageAllBookmark(link).getResult();
		assertEquals(1,results.size());
	}

	public void testPageAllBookmarkUserTag() throws Exception{
		User user = new User("hchen1");
		Tag tag = new Tag("web");
		List<Bookmark> results = bookmarkPager.pageAllBookmark(user, tag).getResult();
		assertEquals(2,results.size());
	}
	
	public void testPageAllBookmarkTag() throws Exception{
		Tag tag = new Tag("timfinin");
		List<Bookmark> results = bookmarkPager.pageAllBookmark(tag).getResult();
		assertEquals(1,results.size());
	}
}
