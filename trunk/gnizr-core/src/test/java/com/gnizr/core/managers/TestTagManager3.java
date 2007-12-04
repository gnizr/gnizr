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
import com.gnizr.core.tag.TagManager;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.User;

public class TestTagManager3 extends GnizrCoreTestBase {

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
		return new FlatXmlDataSet(TestTagManager3.class.getResourceAsStream("/TestTagManager3-input.xml"));
	}

	public void testListBookmarkTag() throws Exception{
		User hchen1 = new User("hchen1");
		List<BookmarkTag> bmarkTags = tagManager.listBookmarkTagUserFolder(hchen1,"private folder");
		assertEquals(6,bmarkTags.size());
	}
	
}
