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
import java.util.Date;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.link.LinkDBDao;
import com.gnizr.db.dao.user.UserDBDao;

public class TestBookmarkDBDao1 extends GnizrDBTestBase {
	
	private BookmarkDBDao bmDao;
	
	public TestBookmarkDBDao1(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		bmDao = new BookmarkDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetBookmark() throws Exception{
		Bookmark bm = bmDao.getBookmark((301));
		assertNotNull(bm);
		assertEquals((1),bm.getUser().getId());
		assertEquals((203),bm.getLink().getId());
		assertEquals(4,bm.getLink().getCount());
		assertEquals("foo",bm.getTitle());
		assertEquals("news",bm.getNotes());
		assertNotNull(bm.getCreatedOn());
		assertNotNull(bm.getLastUpdated());
		assertEquals("cnn news",bm.getTags());
		assertNotNull(bm.getLink().getUrlHash());
	}
	
	public void testDeleteBookmark() throws Exception{
		Bookmark bm = bmDao.getBookmark((300));
		assertNotNull(bm);
		assertTrue(bmDao.deleteBookmark(bm.getId()));
		assertFalse(bmDao.deleteBookmark(bm.getId()));
		assertFalse(bmDao.deleteBookmark(bm.getId()));
		assertFalse(bmDao.deleteBookmark(bm.getId()));
		assertNull(bmDao.getBookmark(bm.getId()));
	}
	
	public void testCreateBookmark() throws Exception{
		LinkDBDao linkDao = new LinkDBDao(getDataSource());
		Link link = linkDao.getLink((202));
		assertNotNull(link);
		
		UserDBDao userDao = new UserDBDao(getDataSource());
		User user = userDao.getUser((1));
		assertNotNull(user);
		
		Bookmark bm = new Bookmark();
		bm.setCreatedOn(Calendar.getInstance().getTime());
		bm.setLastUpdated(Calendar.getInstance().getTime());
		bm.setLink(link);
		bm.setUser(user);
		bm.setTags("a b c d");
		bm.setNotes("notes");
		bm.setTitle("title");		
		
		int id = bmDao.createBookmark(bm);
		assertTrue((id>0));	
		
		int id2 = bmDao.createBookmark(bm);
		assertEquals(id,id2);	
		
		Bookmark bmCopy = bmDao.getBookmark(id);
		assertNotNull(bmCopy);
		assertEquals(bm.getTitle(),bmCopy.getTitle());
		assertEquals(bm.getNotes(),bmCopy.getNotes());
		assertEquals(bm.getUser().getId(),bmCopy.getUser().getId());
		assertEquals(bm.getLink().getId(),bmCopy.getLink().getId());
	
		assertEquals(id,bmDao.createBookmark(bm));
		assertEquals(id,bmDao.createBookmark(bm));
		assertTrue(bmDao.deleteBookmark(id));
	}
	
	public void testGetBookmarkCountOfTag() throws Exception{
		int cnt = bmDao.getBookmarkCount(new Tag(1));
		assertEquals(2,cnt);
		
		cnt = bmDao.getBookmarkCount(new Tag(3));
		assertEquals(2,cnt);
	}
		
	public void testGetBookmarkCountUser() throws Exception{
		User u = new User();
		u.setId((1));
		assertEquals((1),bmDao.getBookmarkCount(u));
	}
	
	public void testGetBookmarkCountLink() throws Exception{
		Link link = new Link();
		link.setId((202));
		assertEquals((1),bmDao.getBookmarkCount(link));
		
		link.setId((203203));
		assertEquals((0),bmDao.getBookmarkCount(link));
	}
	
	public void testPageBookmarksUser() throws Exception{
		User u = new User();
		u.setId((1));
		List<Bookmark> bmarks = null;
		DaoResult<Bookmark> result = bmDao.pageBookmarks(u,(0),10);
		bmarks = result.getResult();
		assertEquals(1,bmarks.size());
		assertEquals(bmarks.size(),result.getSize());
		
		result = bmDao.pageBookmarks(u,(0),1);
		bmarks = result.getResult();
		assertEquals(1,bmarks.size());
	}
		
	public void testPageBookmarksUser2() throws Exception{
		User u = new User(2);
		DaoResult<Bookmark> result = bmDao.pageBookmarks(u, 0,10, BookmarkDao.SORT_BY_CREATED_ON, BookmarkDao.ASCENDING);
		List<Bookmark> bmarks = result.getResult();
		Date d1 = bmarks.get(0).getCreatedOn();
		Date d2 = bmarks.get(1).getCreatedOn();
		assertTrue(d1.before(d2));
		
		result = bmDao.pageBookmarks(u, 0,10, BookmarkDao.SORT_BY_CREATED_ON, BookmarkDao.DESCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getCreatedOn();
		d2 = bmarks.get(1).getCreatedOn();
		assertTrue(d1.after(d2));
		
		result = bmDao.pageBookmarks(u, 0,10, BookmarkDao.SORT_BY_LAST_UPDATED, BookmarkDao.ASCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		assertTrue(d1.before(d2));
		
		result = bmDao.pageBookmarks(u, 0,10, BookmarkDao.SORT_BY_LAST_UPDATED, BookmarkDao.DESCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		assertTrue(d1.after(d2));
		
		
	}
	
	public void testPageBookmarksLink() throws Exception{
		Link l = new Link();
		l.setId((203));
		
		List<Bookmark> bmarks = null;
		DaoResult<Bookmark> result = bmDao.pageBookmarks(l,(0),100);
		bmarks = result.getResult();
		assertEquals(4,bmarks.size());
		
		l.setId((3000));
		result = bmDao.pageBookmarks(l,(0),10000);
		bmarks = result.getResult();
		assertEquals(0,bmarks.size());
	}
	
	public void testPageBookmarksTag() throws Exception{
		Tag tag = new Tag();
		tag.setId((1));
		List<Bookmark> bmarks = null;
		DaoResult<Bookmark> result = bmDao.pageBookmarks(tag,0,1000);
		bmarks = result.getResult();
		assertEquals(2,bmarks.size());
		
		result = bmDao.pageBookmarks(new Tag(3), 0, 20);
		bmarks = result.getResult();
		assertEquals(2,bmarks.size());
		
		Bookmark bm1 = bmarks.get(0);
		assertNotNull(bm1.getLink().getUrl());
		assertNotNull(bm1.getUser().getFullname());
		
		tag.setId((2));
		result = bmDao.pageBookmarks(tag,0,10000);
		bmarks = result.getResult();
		assertEquals(0,bmarks.size());
	}
	
	public void testPageBookmarkUserTag() throws Exception{
		Tag tag = new Tag();
		tag.setId((1));
		User user = new User();
		user.setId((1));
		
		List<Bookmark> bmarks = null;
		DaoResult<Bookmark> result = bmDao.pageBookmarks(user, tag,(0), 500);
		bmarks = result.getResult();
		assertEquals(1,bmarks.size());
		
		Bookmark bm1 = bmarks.get(0);
		assertNotNull(bm1.getUser().getEmail());
		assertNotNull(bm1.getLink().getUrlHash());
		
		user.setId((2));
		result = bmDao.pageBookmarks(user, tag,(0), 500);
		bmarks = result.getResult();
		assertEquals(2,bmarks.size());
	}
	
	
	public void testPageBookmarkUserTag2() throws Exception{
		User user = new User(2);
		Tag tag = new Tag(1);
		
		DaoResult<Bookmark> result = bmDao.pageBookmarks(user, tag,0, 10,BookmarkDao.SORT_BY_CREATED_ON,BookmarkDao.ASCENDING);
		assertEquals(2,result.getSize());
		List<Bookmark> bmarks = result.getResult();
		Date d1 = bmarks.get(0).getCreatedOn();
		Date d2 = bmarks.get(1).getCreatedOn();
		assertTrue(d1.before(d2));
		
		result = bmDao.pageBookmarks(user, tag,0, 10,BookmarkDao.SORT_BY_CREATED_ON,BookmarkDao.DESCENDING);
		assertEquals(2,result.getSize());
		bmarks = result.getResult();
		d1 = bmarks.get(0).getCreatedOn();
		d2 = bmarks.get(1).getCreatedOn();
		assertTrue(d1.after(d2));
		
		result = bmDao.pageBookmarks(user, tag,0, 10,BookmarkDao.SORT_BY_LAST_UPDATED,BookmarkDao.ASCENDING);
		assertEquals(2,result.getSize());
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		assertTrue(d1.before(d2));
		
		result = bmDao.pageBookmarks(user, tag,0, 10,BookmarkDao.SORT_BY_LAST_UPDATED,BookmarkDao.DESCENDING);
		assertEquals(2,result.getSize());
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		assertTrue(d1.after(d2));
	}
	
	public void testFindBookmark() throws Exception{
		User user = new User(2);
		Link link = new Link(202);
		List<Bookmark> bmarks = bmDao.findBookmark(user, link);
		assertEquals(1,bmarks.size());
		Bookmark bm = bmarks.get(0);
		assertEquals((2),bm.getUser().getId());
		assertEquals((202),bm.getLink().getId());
	}
	
	
	public void testUpdateBookmark() throws Exception{
		Bookmark bm = bmDao.getBookmark(300);
		assertEquals("cnn",bm.getTitle());
	
		assertTrue(bm.getTags().contains("cnn"));
		assertTrue(bm.getTags().contains("news"));
		
		bm.setTitle("newtitle");
		assertTrue(bmDao.updateBookmark(bm));
		
		bm = bmDao.getBookmark(300);
		assertEquals("newtitle",bm.getTitle());
		assertTrue(bm.getTagList().contains("news"));
		assertTrue(bm.getTagList().contains("cnn"));
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkDBDao1.class.getResourceAsStream("/dbunit/bmarkdbdao/TestBookmarkDBDao1-input.xml"));
	}

}
