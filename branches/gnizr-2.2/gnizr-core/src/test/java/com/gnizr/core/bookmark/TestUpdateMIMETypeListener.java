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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.MIMEType;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

public class TestUpdateMIMETypeListener extends GnizrCoreTestBase {

	
	private UpdateMIMETypeListener listener;
	private BookmarkManager manager;
	
	protected void setUp() throws Exception {
		super.setUp();
		manager = new BookmarkManager(getGnizrDao());
		listener = new UpdateMIMETypeListener(getGnizrDao());
		manager.addBookmarkListener(listener);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUpdateMIMETypeListener.class.getResourceAsStream("/TestUpdateMIMETypeListener-input.xml"));
	}
	
	public void testAddBookmark() throws Exception{
		Bookmark bm = new Bookmark();
		bm.setUser(new User(3));
		bm.setLink(new Link("http://www.google.com"));
		bm.setTitle("google");
		
		int id = manager.addBookmark(bm);
		assertTrue(id > 0);
		
		manager.shutdown();
		
		bm = getGnizrDao().getBookmarkDao().getBookmark(id);
		assertEquals(MIMEType.TEXT_HTML,bm.getLink().getMimeTypeId());
	}
	
	public void testUpdateBookmark() throws Exception{
		Bookmark bm = manager.getBookmark(300);
		assertTrue(manager.updateBookmark(bm));
		
		manager.shutdown();
		bm = getGnizrDao().getBookmarkDao().getBookmark(300);
		assertEquals(MIMEType.TEXT_HTML,bm.getLink().getMimeTypeId());
	}

}
