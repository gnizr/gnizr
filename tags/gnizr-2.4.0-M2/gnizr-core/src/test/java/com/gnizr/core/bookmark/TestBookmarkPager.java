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
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class TestBookmarkPager extends GnizrCoreTestBase {
	
	private BookmarkPager pager;
		
	protected void setUp() throws Exception {
		super.setUp();
		pager = new BookmarkPager(getGnizrDao());
	}
	
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkPager.class
				.getResourceAsStream("/TestBookmarkPager-input.xml"));
	}
	
	public void testMaxPageNumberUser() throws Exception{
		assertEquals((1),pager.getMaxPageNumber(new User("hchen1"),10));	
		assertEquals((8),pager.getMaxPageNumber(new User("hchen1"),1));
		assertEquals((3),pager.getMaxPageNumber(new User("hchen1"),3));
	}
	
	public void testMaxPageNumberUserTag() throws Exception{
		assertEquals((2),pager.getMaxPageNumber(new UserTag("hchen1","webwork"),1));
		assertEquals((1),pager.getMaxPageNumber(new UserTag("hchen1","wii"),1));		
	}
	
	public void testPageBookmark() throws Exception{
		User hchen1 = new User("hchen1");
		List<Bookmark> bm = null;
		DaoResult<Bookmark> result = pager.pageBookmark(hchen1,(0),2);
		bm = result.getResult();
		assertEquals(2,bm.size());
		bm = pager.pageBookmark(hchen1,50,1).getResult();
		assertEquals(0,bm.size());
		bm = pager.pageBookmark(hchen1,5,10).getResult();
		assertEquals(3,bm.size());
		bm = pager.pageBookmark(hchen1,-1, 10).getResult();
		assertEquals(0,bm.size());
	}
	
	public void testPageBookmarkTag() throws Exception{
		List<Bookmark> bm = pager.pageBookmark(new UserTag("hchen1","webwork"),(0),2).getResult();
		assertEquals(2,bm.size());		
		
		bm = pager.pageBookmark(new UserTag("hchen1","wii"),(0),100).getResult();
		assertEquals(1,bm.size());		
	}

}
