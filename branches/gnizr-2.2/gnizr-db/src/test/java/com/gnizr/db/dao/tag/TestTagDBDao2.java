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

import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.GnizrDBTestBase;

public class TestTagDBDao2 extends GnizrDBTestBase {

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
		return new FlatXmlDataSet(TestTagDBDao1.class.getResourceAsStream("/dbunit/tagdbdao/TestTagDBDao2-input.xml"));
	}
	
	public void testFindBookmarkTagByFolderId() throws Exception{
		List<BookmarkTag> bookmarkTags = tagDao.findBookmarkTag(new Folder(1));
		assertEquals(0,bookmarkTags.size());
		bookmarkTags = tagDao.findBookmarkTag(new Folder(2));
		assertEquals(6,bookmarkTags.size());
		BookmarkTag bmTag = bookmarkTags.get(0);
		assertNotNull(bmTag.getBookmark());
		assertNotNull(bmTag.getTag());
		assertNotNull(bmTag.getBookmark().getLink());
		assertNotNull(bmTag.getBookmark().getUser());
		assertEquals(0,bmTag.getId());
	}
	
}
