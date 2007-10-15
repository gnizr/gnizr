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
import com.gnizr.core.managers.ForUserManager;
import com.gnizr.core.managers.UserManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

public class TestForUserListener extends GnizrCoreTestBase {

	private BookmarkManager bookmarkManager;
	private ForUserManager forUserManager;
	private UserManager userManager;
	private ForUserListener listener;
	
	protected void setUp() throws Exception {
		super.setUp();		
		
		forUserManager = new ForUserManager(getGnizrDao());
		userManager = new UserManager(getGnizrDao());
		listener = new ForUserListener(userManager,forUserManager);
		
		bookmarkManager = new BookmarkManager(getGnizrDao());
		bookmarkManager.addBookmarkListener(listener);
	}

	protected void tearDown() throws Exception {
		bookmarkManager.shutdown();
		super.tearDown();
	}

	public void testDeleteBookmark() throws Exception{
		ForUser record = forUserManager.getForUser(400);
		assertNotNull(record);
		
		assertTrue(bookmarkManager.deleteBookmark(new Bookmark(300)));
		
		record = forUserManager.getForUser(400);
		assertNull(record);		
	}
	
	public void testAddBookmark() throws Exception {
		int cnt = forUserManager.getForUserCount(new User(2));
		assertEquals(1,cnt);
		cnt = forUserManager.getForUserCount(new User(3));
		assertEquals(0,cnt);
		
		Bookmark b = new Bookmark();
		b.setTitle("example site");
		b.setLink(new Link("http://example.org/foobar"));
		b.setTags("abc 123 for:jsmith for:hchen1");
		b.setUser(new User(1));
		
		int id = bookmarkManager.addBookmark(b);
		assertTrue((id > 0));
		
		// must call shutdown() to wait for all threads that
		// runs the listener notification to finish
		bookmarkManager.shutdown();
		
		cnt = forUserManager.getForUserCount(new User(2));
		assertEquals(2,cnt);
		cnt = forUserManager.getForUserCount(new User(3));
		assertEquals(1,cnt);		
	}
	
	public void testUpdateBookmark() throws Exception{
		int cnt = forUserManager.getForUserCount(new User(2));
		assertEquals(1,cnt);
		cnt = forUserManager.getForUserCount(new User(3));
		assertEquals(0,cnt);
		
		Bookmark b = new Bookmark();
		b.setTitle("example site");
		b.setLink(new Link("http://example.org/foobar"));
		b.setTags("abc 123 for:jsmith for:hchen1");
		b.setUser(new User(1));
		
		int id = bookmarkManager.addBookmark(b);
		
		Bookmark bm = bookmarkManager.getBookmark(id);
		bm.setTags(bm.getTags() + " for:hchen1 for:jsmith for:foobar for:crap");
		assertTrue(bookmarkManager.updateBookmark(bm));
		bookmarkManager.shutdown();
		
		cnt = forUserManager.getForUserCount(new User(2));
		assertEquals(2,cnt);
		cnt = forUserManager.getForUserCount(new User(3));
		assertEquals(1,cnt);		
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestForUserListener.class.getResourceAsStream("/TestForUserListener-input.xml"));
	}

}
