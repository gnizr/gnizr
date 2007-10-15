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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.user.TestUserDBDao1;

public class TestBookmarkDBDao3 extends GnizrDBTestBase {

	private BookmarkDao bmarkDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		bmarkDao = new BookmarkDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testSearchCommunityBookmarks() throws Exception{
		DaoResult<Bookmark> result = bmarkDao.searchCommunityBookmarks("+MySQL +Boolean",0, 10);
		assertEquals(1, result.getSize());
		assertEquals(1, result.getResult().size());
		assertEquals(3, result.getResult().get(0).getId());
		
		result = bmarkDao.searchCommunityBookmarks("feature mysql",0, 10);
		assertEquals(3,result.getSize());
	}
	
	public void testSearchUserBookmarks() throws Exception{
		DaoResult<Bookmark> result = bmarkDao.searchUserBookmarks("MySQL WordPress Plugin", new User(2),0, 10);
		assertEquals(1,result.getSize());
		assertEquals(3, result.getResult().get(0).getId());
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserDBDao1.class.getResourceAsStream("/dbunit/bmarkdbdao/TestBookmarkDBDao3-input.xml"));
	}
	
}
