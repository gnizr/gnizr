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
package com.gnizr.core.managers;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.tag.TagManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.User;

public class TestTagManager2 extends GnizrCoreTestBase {

	private TagManager tagManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagManager = new TagManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagManager2.class.getResourceAsStream("/TestTagManager2-input.xml"));
	}

	public void testListBookmarkTag() throws Exception{
		User gnizr = new User("gnizr");
		List<BookmarkTag> bTags = tagManager.listBookmarkTag(gnizr);
		assertEquals(2,bTags.size());
		assertEquals("zirrus",bTags.get(1).getBookmark().getTitle());

		BookmarkManager bManager = new BookmarkManager(getGnizrDao());
		Bookmark bm300 = bManager.getBookmark(300);
		String tags = bm300.getTags();
		bm300.setTags(tags + " web cnn");
		assertTrue(bManager.updateBookmark(bm300));
		assertEquals(202,bm300.getLink().getId());
		
		bTags = tagManager.listBookmarkTag(gnizr);
		assertEquals(3,bTags.size());
		assertEquals(202,bTags.get(1).getBookmark().getLink().getId());
		assertEquals(202,bTags.get(2).getBookmark().getLink().getId());		
		assertTrue((bTags.get(1).getTag().getId() != bTags.get(2).getTag().getId()));
	}
	
}
