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
package com.gnizr.db.dao.tag;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;

public class TestTagDBDao1 extends GnizrDBTestBase {

	private TagDao tagDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagDao = new TagDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagDBDao1.class.getResourceAsStream("/dbunit/tagdbdao/TestTagDBDao1-input.xml"));
	}
	
	public void testGetBookmarkTag() throws Exception{
		BookmarkTag bmTag = tagDao.getBookmarkTag(10);
		assertNotNull(bmTag);
		assertEquals(1,bmTag.getCount());
		
		Bookmark bm = bmTag.getBookmark();
		assertEquals(1,bm.getId());
		assertEquals("CNN",bm.getTitle());
		assertNotNull(bm.getCreatedOn());
		User user = bm.getUser();
		assertEquals(1,user.getId());
		assertEquals("hchen1",user.getUsername());
		assertNotNull(user.getCreatedOn());
		Link link = bm.getLink();
		assertEquals(202,link.getId());
		assertEquals(1003,link.getMimeTypeId());
		assertEquals("c50252f4f24784b5d368926df781ede9",link.getUrlHash());
		
		Tag tag = bmTag.getTag();
		assertEquals(2,tag.getId());
		assertEquals("news",tag.getLabel());
		assertEquals(10,tag.getCount());
	}
	
	public void testCreateBookmarkTag() throws Exception{
		BookmarkTag bmTag = new BookmarkTag();
		bmTag.setBookmark(new Bookmark(1));
		bmTag.setTag(new Tag(1));	
		
		int id = tagDao.createBookmarkTag(bmTag);
		assertTrue((id > 0));
		
		BookmarkTag bmTag2 = tagDao.getBookmarkTag(id);
		assertEquals(bmTag.getBookmark().getId(),bmTag2.getBookmark().getId());
		assertEquals(bmTag.getTag().getId(),bmTag2.getTag().getId());
		
		BookmarkTag bmFoo = new BookmarkTag();
		bmFoo.setBookmark(new Bookmark(1));
		bmFoo.setTag(new Tag(100));
		bmFoo.setPosition(40);
		
		id = tagDao.createBookmarkTag(bmFoo);
		assertTrue((id > 0));
		bmTag2 = tagDao.getBookmarkTag(id);
		assertEquals(40,bmTag2.getPosition());
	}
	
	public void testDeleteBookmarkTag() throws Exception{
		BookmarkTag bmTag = tagDao.getBookmarkTag(10);
		assertNotNull(bmTag);
		assertTrue(tagDao.deleteBookmarkTag(10));
		bmTag = tagDao.getBookmarkTag(10);
		assertNull(bmTag);
	}
	
	public void testUpdateBookmarkTag() throws Exception{
		BookmarkTag bmTag = tagDao.getBookmarkTag(10);
		bmTag.setTag(new Tag(100));
		bmTag.setBookmark(new Bookmark(2));
		bmTag.setPosition(1001);
		boolean okay = tagDao.updateBookmarkTag(bmTag);
		assertTrue(okay);
		
		bmTag = tagDao.getBookmarkTag(10);
		assertEquals(100,bmTag.getTag().getId());
		assertEquals(2,bmTag.getBookmark().getId());
		assertEquals(1001,bmTag.getPosition());
	}
	
	public void testGetBookmarkTagId() throws Exception{
		int id = tagDao.getBookmarkTagId(new Bookmark(1), new Tag(2));
		assertEquals(10,id);
	}
	
	public void testFindBookmarkTagUser() throws Exception{
		List<BookmarkTag> bmTags = tagDao.findBookmarkTag(new User(1));
		assertEquals(8,bmTags.size());
		assertNotNull(bmTags.get(0).getBookmark().getLink().getUrlHash());
		assertNotNull(bmTags.get(1).getTag().getLabel());
	}
	
	
	public void testFindBookmarkTagCommunitySearch() throws Exception{
		List<BookmarkTag> bmTags = tagDao.findBookmarkTagCommunitySearch("questions");
		assertEquals(5, bmTags.size());
		
		bmTags = tagDao.findBookmarkTagCommunitySearch("questions pokd");
		assertEquals(6, bmTags.size());
	}
		
}
