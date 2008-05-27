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
package com.gnizr.web.action.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.search.SearchIndexManager;
import com.gnizr.core.search.SearchIndexProfile;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestIndexBookmark extends GnizrWebappTestBase {

	private SearchIndexProfile profile;
	private SearchIndexManager searchIndexManager;
	private BookmarkPager bookmarkPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/search-data");
		
		searchIndexManager = new SearchIndexManager(true);
		searchIndexManager.setProfile(profile);
		searchIndexManager.init();
		
		bookmarkPager = new BookmarkPager(getGnizrDao());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		searchIndexManager.destroy();
	}

	public void testGo() throws Exception{
		Map<Object, Object> session = new HashMap<Object, Object>();
		
		
		IndexBookmark action = new IndexBookmark();
		action.setBookmarkPager(bookmarkPager);
		action.setSearchIndexManager(searchIndexManager);
		action.setUserManager(new UserManager(getGnizrDao()));
		action.setUser(new User(1));
		action.setSession(session);
		
		String doc1Hash = "d1a8e491759cb30d11357c4776be9c66";
		Document d1 = searchIndexManager.findLeadDocument(doc1Hash);
		assertNull(d1);
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		Thread.sleep(10000);
		d1 = searchIndexManager.findLeadDocument(doc1Hash);
		assertNotNull(d1);
		
		assertNotNull(session.get("status"));
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestIndexBookmark.class.getResourceAsStream("/TestIndexBookmark-input.xml"));
	}

}
