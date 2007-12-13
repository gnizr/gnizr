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
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDBDao;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.folder.FolderDBDao;
import com.gnizr.db.dao.folder.FolderDao;

public class TestTagDBDao3 extends GnizrDBTestBase {

	
	private TagDao tagDao;
	private FolderDao folderDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagDao = new TagDBDao(getDataSource());
		folderDao = new FolderDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagDBDao1.class.getResourceAsStream("/dbunit/tagdbdao/TestTagDBDao3-input.xml"));
	}
	
	public void testAddTagCountOne() throws Exception{
		List<FolderTag> folderTags = folderDao.findTagsInFolder(new Folder(2), 0, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(2,folderTags.size());
		FolderTag t1 = folderTags.get(0);
		FolderTag t2 = folderTags.get(1);
		
		assertEquals("cnn",t1.getTag().getLabel());
		assertEquals("news",t2.getTag().getLabel());
		assertEquals(1,t1.getCount());
		assertEquals(1,t1.getCount());
		
		tagDao.addTagCountOne(new Tag[]{new Tag(100)}, new User(1), new Link(202), new Bookmark(1));
		folderTags = folderDao.findTagsInFolder(new Folder(2), 0, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(3,folderTags.size());
		t1 = folderTags.get(0);
		t2 = folderTags.get(1);
		assertEquals(1,t1.getCount());
		assertEquals(1,t1.getCount());
		
		FolderTag t3 = folderTags.get(2);
		assertEquals(1,t3.getCount());
		assertEquals("newsnews",t3.getTag().getLabel());
	}

	public void testSubTagCountOne() throws Exception{
		List<FolderTag> folderTags = folderDao.findTagsInFolder(new Folder(2), 1, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(2,folderTags.size());
		FolderTag t1 = folderTags.get(0);
		FolderTag t2 = folderTags.get(1);
		
		assertEquals("cnn",t1.getTag().getLabel());
		assertEquals("news",t2.getTag().getLabel());
		assertEquals(1,t1.getCount());
		assertEquals(1,t1.getCount());
		
		tagDao.subtractTagCountOne(new Tag[]{new Tag(1)}, new User(1), new Link(202), new Bookmark(1));
		folderTags = folderDao.findTagsInFolder(new Folder(2), 1, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(1,folderTags.size());
		t1 = folderTags.get(0);
		assertEquals(1,t1.getCount());
		assertEquals("news",t1.getTag().getLabel());
	}
	
	public void testExpandTag() throws Exception{
		List<FolderTag> folderTags = null;
		FolderTag t1, t2, t3;
		
		BookmarkDao bmarkDao = new BookmarkDBDao(getDataSource());
		Bookmark bm = bmarkDao.getBookmark(1);
		List<String> tags = bm.getTagList();
		assertTrue(tags.contains("cnn"));
		assertTrue(tags.contains("news"));
		
		Tag cnnTag = tagDao.getTag(1);
		assertEquals(1,cnnTag.getCount());
		
		Tag newsTag = tagDao.getTag(2);
		assertEquals(1,newsTag.getCount());
		
		LinkTag ltag1 = tagDao.getLinkTag(1);
		assertEquals(1,ltag1.getCount());
		
		LinkTag ltag2 = tagDao.getLinkTag(2);
		assertEquals(1,ltag2.getCount());
		
		BookmarkTag bmtag10 = tagDao.getBookmarkTag(10);
		assertEquals(1,bmtag10.getCount());
		
		BookmarkTag bmtag11 = tagDao.getBookmarkTag(11);
		assertEquals(1,bmtag11.getCount());
		
		BookmarkTag bmtagQ = tagDao.getBookmarkTag(12);
		assertEquals(0,bmtagQ.getCount());
		
		folderTags = folderDao.findTagsInFolder(new Folder(2), 0, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(2,folderTags.size());
		
		t1 = folderTags.get(0);
		assertEquals("cnn",t1.getTag().getLabel());
		assertEquals(1,t1.getCount());
		t2 = folderTags.get(1);
		assertEquals("news",t2.getTag().getLabel());
		assertEquals(1,t2.getTag().getCount());
		
		List<Bookmark> chngBmarks = tagDao.expandTag(new User(1), new Tag(1), new Tag[]{new Tag(4)});
		assertEquals(1,chngBmarks.size());
		for(Bookmark c : chngBmarks){
			assertTrue(c.getTagList().contains("questions"));
			assertTrue(c.getTagList().contains("cnn"));
		}
				
		cnnTag = tagDao.getTag(1);
		assertEquals(1,cnnTag.getCount());
		
		newsTag = tagDao.getTag(2);
		assertEquals(1,newsTag.getCount());
		
		ltag1 = tagDao.getLinkTag(1);
		assertEquals(1,ltag1.getCount());
		
		ltag2 = tagDao.getLinkTag(2);
		assertEquals(1,ltag2.getCount());
		
		bmtag10 = tagDao.getBookmarkTag(10);
		assertEquals(1,bmtag10.getCount());
		
		bmtag11 = tagDao.getBookmarkTag(11);
		assertEquals(1,bmtag11.getCount());
	
		bmtagQ = tagDao.getBookmarkTag(12);
		assertEquals(1,bmtagQ.getCount());
		
		folderTags = folderDao.findTagsInFolder(new Folder(2), 0, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(3,folderTags.size());
		
		t1 = folderTags.get(0);
		assertEquals("cnn",t1.getTag().getLabel());
		assertEquals(1,t1.getCount());
		t2 = folderTags.get(1);
		assertEquals("news",t2.getTag().getLabel());
		assertEquals(1,t2.getTag().getCount());
		t3 = folderTags.get(2);
		assertEquals("questions",t3.getTag().getLabel());
		assertEquals(1,t3.getCount());
	}
	
	
	public void testReduceTag() throws Exception{
		List<FolderTag> folderTags = null;
		FolderTag t1, t2;
		
		folderTags = folderDao.findTagsInFolder(new Folder(2), 1, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(2,folderTags.size());
		
		t1 = folderTags.get(0);
		assertEquals("cnn",t1.getTag().getLabel());
		assertEquals(1,t1.getCount());
		t2 = folderTags.get(1);
		assertEquals("news",t2.getTag().getLabel());
		assertEquals(1,t2.getTag().getCount());
		
		List<Bookmark> changeBookmarks= tagDao.reduceTag(new User(1), new Tag[]{new Tag(1)});
		assertEquals(1,changeBookmarks.size());
		for(Bookmark c : changeBookmarks){
			assertFalse(c.getTagList().contains("cnn"));
		}
		
		BookmarkTag bm11 = tagDao.getBookmarkTag(11);
		assertEquals("cnn",bm11.getTag().getLabel());
		assertEquals(0,bm11.getCount());
		assertEquals(0,bm11.getTag().getCount());
	
		LinkTag ln1 = tagDao.getLinkTag(1);
		assertEquals(202,ln1.getLink().getId());
		assertEquals(0,ln1.getCount());
		assertEquals(0,ln1.getTag().getCount());
		
		folderTags = folderDao.findTagsInFolder(new Folder(2), 1, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(1,folderTags.size());
		
		t1 = folderTags.get(0);
		assertEquals("news",t1.getTag().getLabel());
		assertEquals(1,t1.getCount());
	
	}
}
