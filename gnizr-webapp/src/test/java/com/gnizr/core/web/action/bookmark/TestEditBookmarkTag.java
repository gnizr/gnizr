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
package com.gnizr.core.web.action.bookmark;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.tag.TagDao;
import com.opensymphony.xwork.ActionSupport;

public class TestEditBookmarkTag extends GnizrWebappTestBase{

	private EditBookmarkTag action;
	private BookmarkManager manager;
	private BookmarkDao bookmarkDao;
	private TagDao tagDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		bookmarkDao = getGnizrDao().getBookmarkDao();
		tagDao = getGnizrDao().getTagDao();
		manager = new BookmarkManager(getGnizrDao());
		action = new EditBookmarkTag();
		action.setUserManager(new UserManager(getGnizrDao()));
		action.setBookmarkManager(manager);
		action.setLoggedInUser(new User(1));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestEditBookmarkTag.class.getResourceAsStream("/TestEditBookmarkTag-input.xml"));	
	}
	
	public void testRenameTag1() throws Exception{
		List<Bookmark> bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());		
		bmarks = bookmarkDao.pageBookmarks(new User(1), new Tag(2),0,10).getResult();
		assertEquals(0,bmarks.size());
		Tag tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		Tag tagB = tagDao.getTag(2);
		assertEquals(0,tagB.getCount());
		LinkTag linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		LinkTag linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		LinkTag linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		Bookmark bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		Bookmark bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		Bookmark bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		Bookmark bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		
		action.setTag("A");
		action.setNewTag("B");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(0,bmarks.size());
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(2),0,10).getResult();
		assertEquals(3,bmarks.size());
		
		tagA = tagDao.getTag(1);
		assertEquals(1,tagA.getCount());
		tagB = tagDao.getTag(2);
		assertEquals(3,tagB.getCount());
		
		linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(0,linkTag1000_A.getCount());
		linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(0,linkTag1001_A.getCount());
		linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(1,linkTag1002_A.getCount());
		
		LinkTag linkTag1000_B = tagDao.findLinkTag(new Link(1000),new Tag(2)).get(0);
		assertEquals(1,linkTag1000_B.getCount());
		LinkTag linkTag1001_B = tagDao.findLinkTag(new Link(1001),new Tag(2)).get(0);
		assertEquals(1,linkTag1001_B.getCount());
		LinkTag linkTag1002_B = tagDao.findLinkTag(new Link(1002),new Tag(2)).get(0);
		assertEquals(1,linkTag1002_B.getCount());
		
		bm301 = bookmarkDao.getBookmark(301);
		assertFalse(bm301.getTagList().contains("A"));
		assertTrue(bm301.getTagList().contains("B"));
		bm302 = bookmarkDao.getBookmark(302);
		assertFalse(bm302.getTagList().contains("A"));
		assertTrue(bm302.getTagList().contains("B"));
		bm303 = bookmarkDao.getBookmark(303);
		assertFalse(bm303.getTagList().contains("A"));
		assertTrue(bm303.getTagList().contains("B"));
		bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		assertFalse(bm304.getTagList().contains("B"));
	}
	
	public void testRenameTag2() throws Exception{
		List<Bookmark> bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());		
		bmarks = bookmarkDao.pageBookmarks(new User(1), new Tag(2),0,10).getResult();
		assertEquals(0,bmarks.size());
		Tag tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		Tag tagB = tagDao.getTag(2);
		assertEquals(0,tagB.getCount());
		Tag tagC = tagDao.getTag(3);
		assertEquals(0,tagC.getCount());
		LinkTag linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		LinkTag linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		LinkTag linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		Bookmark bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		Bookmark bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		Bookmark bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		Bookmark bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		
		action.setTag("A");
		action.setNewTag("B C");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(0,bmarks.size());
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(2),0,10).getResult();
		assertEquals(3,bmarks.size());
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(3),0,10).getResult();
		assertEquals(3,bmarks.size());
		
		tagA = tagDao.getTag(1);
		assertEquals(1,tagA.getCount());
		tagB = tagDao.getTag(2);
		assertEquals(3,tagB.getCount());
		tagC = tagDao.getTag(3);
		assertEquals(3,tagC.getCount());
		
		linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(0,linkTag1000_A.getCount());
		linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(0,linkTag1001_A.getCount());
		linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(1,linkTag1002_A.getCount());
		
		LinkTag linkTag1000_B = tagDao.findLinkTag(new Link(1000),new Tag(2)).get(0);
		assertEquals(1,linkTag1000_B.getCount());
		LinkTag linkTag1001_B = tagDao.findLinkTag(new Link(1001),new Tag(2)).get(0);
		assertEquals(1,linkTag1001_B.getCount());
		LinkTag linkTag1002_B = tagDao.findLinkTag(new Link(1002),new Tag(2)).get(0);
		assertEquals(1,linkTag1002_B.getCount());
		
		LinkTag linkTag1000_C = tagDao.findLinkTag(new Link(1000),new Tag(3)).get(0);
		assertEquals(1,linkTag1000_C.getCount());
		LinkTag linkTag1001_C = tagDao.findLinkTag(new Link(1001),new Tag(3)).get(0);
		assertEquals(1,linkTag1001_C.getCount());
		LinkTag linkTag1002_C = tagDao.findLinkTag(new Link(1002),new Tag(3)).get(0);
		assertEquals(1,linkTag1002_C.getCount());
		
		bm301 = bookmarkDao.getBookmark(301);
		assertFalse(bm301.getTagList().contains("A"));
		assertTrue(bm301.getTagList().contains("B"));
		assertTrue(bm301.getTagList().contains("C"));
		bm302 = bookmarkDao.getBookmark(302);
		assertFalse(bm302.getTagList().contains("A"));
		assertTrue(bm302.getTagList().contains("B"));
		assertTrue(bm302.getTagList().contains("C"));
		bm303 = bookmarkDao.getBookmark(303);
		assertFalse(bm303.getTagList().contains("A"));
		assertTrue(bm303.getTagList().contains("B"));
		assertTrue(bm303.getTagList().contains("C"));
		bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		assertFalse(bm304.getTagList().contains("B"));
		assertFalse(bm304.getTagList().contains("C"));
	}
	
	public void testRenameTag3() throws Exception{
		List<Bookmark> bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());		
		bmarks = bookmarkDao.pageBookmarks(new User(1), new Tag(2),0,10).getResult();
		assertEquals(0,bmarks.size());
		Tag tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		Tag tagB = tagDao.getTag(2);
		assertEquals(0,tagB.getCount());
		Tag tagC = tagDao.getTag(3);
		assertEquals(0,tagC.getCount());
		LinkTag linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		LinkTag linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		LinkTag linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		Bookmark bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		Bookmark bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		Bookmark bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		Bookmark bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		
		action.setTag("A");
		action.setNewTag("A B C");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(2),0,10).getResult();
		assertEquals(3,bmarks.size());
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(3),0,10).getResult();
		assertEquals(3,bmarks.size());
		
		tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		tagB = tagDao.getTag(2);
		assertEquals(3,tagB.getCount());
		tagC = tagDao.getTag(3);
		assertEquals(3,tagC.getCount());
		
		linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		
		LinkTag linkTag1000_B = tagDao.findLinkTag(new Link(1000),new Tag(2)).get(0);
		assertEquals(1,linkTag1000_B.getCount());
		LinkTag linkTag1001_B = tagDao.findLinkTag(new Link(1001),new Tag(2)).get(0);
		assertEquals(1,linkTag1001_B.getCount());
		LinkTag linkTag1002_B = tagDao.findLinkTag(new Link(1002),new Tag(2)).get(0);
		assertEquals(1,linkTag1002_B.getCount());
		
		LinkTag linkTag1000_C = tagDao.findLinkTag(new Link(1000),new Tag(3)).get(0);
		assertEquals(1,linkTag1000_C.getCount());
		LinkTag linkTag1001_C = tagDao.findLinkTag(new Link(1001),new Tag(3)).get(0);
		assertEquals(1,linkTag1001_C.getCount());
		LinkTag linkTag1002_C = tagDao.findLinkTag(new Link(1002),new Tag(3)).get(0);
		assertEquals(1,linkTag1002_C.getCount());
		
		bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		assertTrue(bm301.getTagList().contains("B"));
		assertTrue(bm301.getTagList().contains("C"));
		bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		assertTrue(bm302.getTagList().contains("B"));
		assertTrue(bm302.getTagList().contains("C"));
		bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		assertTrue(bm303.getTagList().contains("B"));
		assertTrue(bm303.getTagList().contains("C"));
		bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		assertFalse(bm304.getTagList().contains("B"));
		assertFalse(bm304.getTagList().contains("C"));
	}
	
	public void testRenameTag4() throws Exception{
		List<Bookmark> bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());		
		bmarks = bookmarkDao.pageBookmarks(new User(1), new Tag(2),0,10).getResult();
		assertEquals(0,bmarks.size());
		Tag tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		Tag tagB = tagDao.getTag(2);
		assertEquals(0,tagB.getCount());
		Tag tagC = tagDao.getTag(3);
		assertEquals(0,tagC.getCount());
		LinkTag linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		LinkTag linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		LinkTag linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		Bookmark bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		Bookmark bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		Bookmark bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		Bookmark bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		
		action.setTag("A");
		action.setNewTag("A B A B C");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(2),0,10).getResult();
		assertEquals(3,bmarks.size());
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(3),0,10).getResult();
		assertEquals(3,bmarks.size());
		
		tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		tagB = tagDao.getTag(2);
		assertEquals(3,tagB.getCount());
		tagC = tagDao.getTag(3);
		assertEquals(3,tagC.getCount());
		
		linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		
		LinkTag linkTag1000_B = tagDao.findLinkTag(new Link(1000),new Tag(2)).get(0);
		assertEquals(1,linkTag1000_B.getCount());
		LinkTag linkTag1001_B = tagDao.findLinkTag(new Link(1001),new Tag(2)).get(0);
		assertEquals(1,linkTag1001_B.getCount());
		LinkTag linkTag1002_B = tagDao.findLinkTag(new Link(1002),new Tag(2)).get(0);
		assertEquals(1,linkTag1002_B.getCount());
		
		LinkTag linkTag1000_C = tagDao.findLinkTag(new Link(1000),new Tag(3)).get(0);
		assertEquals(1,linkTag1000_C.getCount());
		LinkTag linkTag1001_C = tagDao.findLinkTag(new Link(1001),new Tag(3)).get(0);
		assertEquals(1,linkTag1001_C.getCount());
		LinkTag linkTag1002_C = tagDao.findLinkTag(new Link(1002),new Tag(3)).get(0);
		assertEquals(1,linkTag1002_C.getCount());
		
		bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		assertTrue(bm301.getTagList().contains("B"));
		assertTrue(bm301.getTagList().contains("C"));
		bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		assertTrue(bm302.getTagList().contains("B"));
		assertTrue(bm302.getTagList().contains("C"));
		bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		assertTrue(bm303.getTagList().contains("B"));
		assertTrue(bm303.getTagList().contains("C"));
		bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		assertFalse(bm304.getTagList().contains("B"));
		assertFalse(bm304.getTagList().contains("C"));
	}
	
	public void testDeleteTag1() throws Exception{
		List<Bookmark> bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());		
		bmarks = bookmarkDao.pageBookmarks(new User(1), new Tag(2),0,10).getResult();
		assertEquals(0,bmarks.size());
		Tag tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		Tag tagB = tagDao.getTag(2);
		assertEquals(0,tagB.getCount());
		LinkTag linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		LinkTag linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		LinkTag linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		Bookmark bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		Bookmark bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		Bookmark bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		Bookmark bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		
		action.setTag("A");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(0,bmarks.size());
		
		tagA = tagDao.getTag(1);
		assertEquals(1,tagA.getCount());
		
		linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(0,linkTag1000_A.getCount());
		linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(0,linkTag1001_A.getCount());
		linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(1,linkTag1002_A.getCount());
			
		bm301 = bookmarkDao.getBookmark(301);
		assertFalse(bm301.getTagList().contains("A"));
		bm302 = bookmarkDao.getBookmark(302);
		assertFalse(bm302.getTagList().contains("A"));	
		bm303 = bookmarkDao.getBookmark(303);
		assertFalse(bm303.getTagList().contains("A"));	
		bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
	}
	
	public void testDeleteTag2() throws Exception{
		List<Bookmark> bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());		
		bmarks = bookmarkDao.pageBookmarks(new User(1), new Tag(2),0,10).getResult();
		assertEquals(0,bmarks.size());
		Tag tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		Tag tagB = tagDao.getTag(2);
		assertEquals(0,tagB.getCount());
		LinkTag linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		LinkTag linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		LinkTag linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
		Bookmark bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		Bookmark bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));
		Bookmark bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));
		Bookmark bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
		
		action.setTag("B");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		// delete a 0-usage-freq tag is allowed.
		assertNull(GnizrDaoUtil.getUserTag(tagDao, new User(1), new Tag(2)));
		
		bmarks = bookmarkDao.pageBookmarks(new User(1),new Tag(1),0,10).getResult();
		assertEquals(3,bmarks.size());
		
		tagA = tagDao.getTag(1);
		assertEquals(4,tagA.getCount());
		
		linkTag1000_A = tagDao.findLinkTag(new Link(1000),new Tag(1)).get(0);
		assertEquals(1,linkTag1000_A.getCount());
		linkTag1001_A = tagDao.findLinkTag(new Link(1001),new Tag(1)).get(0);
		assertEquals(1,linkTag1001_A.getCount());
		linkTag1002_A = tagDao.findLinkTag(new Link(1002),new Tag(1)).get(0);
		assertEquals(2,linkTag1002_A.getCount());
			
		bm301 = bookmarkDao.getBookmark(301);
		assertTrue(bm301.getTagList().contains("A"));
		bm302 = bookmarkDao.getBookmark(302);
		assertTrue(bm302.getTagList().contains("A"));	
		bm303 = bookmarkDao.getBookmark(303);
		assertTrue(bm303.getTagList().contains("A"));	
		bm304 = bookmarkDao.getBookmark(304);
		assertTrue(bm304.getTagList().contains("A"));
	}
	
}
