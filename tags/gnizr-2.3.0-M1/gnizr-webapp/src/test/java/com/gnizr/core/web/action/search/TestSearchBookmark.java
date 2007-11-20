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
package com.gnizr.core.web.action.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.managers.UserManager;
import com.gnizr.core.search.Search;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.core.web.util.GnizrConfiguration;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class TestSearchBookmark extends GnizrWebappTestBase {
	
	
	private Search search;
	private SearchBookmark action;
	private UserManager userManager;
	private Map<Object, Object> session = new HashMap<Object, Object>();
	
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
		GnizrConfiguration config = new GnizrConfiguration();
		config.setWebApplicationUrl("http://foo.com/gnizr");
		
		search = new Search(getGnizrDao());
		
		action = new SearchBookmark();		
		action.setSearch(search);
		action.setGnizrConfiguration(config);
		action.setSession(session);
		action.setUserManager(userManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testExecuteNoPaging() throws Exception{
		action.setType(SearchBookmark.TYPE_TEXT);
		action.setQueryString("fulltext mysql");
		
		// even we set perPageCount = 1, it should not affact the result set size
		action.setPerPageCount(1);
		// or event page #
		action.setPage(10);
		
		assertEquals(ActionSupport.SUCCESS,action.executeNoPaging());
		
		List<Bookmark> result = action.getBookmarks();
		assertEquals(2,result.size());
		
		assertEquals(2,action.getTotalMatched());
	}
	
	public void testSearchUserBookmark() throws Exception{
		action.setType(SearchBookmark.TYPE_USER);
		action.setLoggedInUser(new User(3));
		action.setQueryString("Category WordPress Group Wiki");
		
		String code = action.executeNoPaging();
		assertEquals(ActionSupport.SUCCESS,code);
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());
	}
	
	public void testSearchUserBookmark2() throws Exception{
		action.setType(SearchBookmark.TYPE_USER);
		action.setLoggedInUser(null);
		action.setUsername("joe");
		action.setQueryString("Category WordPress Group Wiki");
		
		String code = action.executeNoPaging();
		assertEquals(ActionSupport.SUCCESS,code);
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());
	}
	
	public void testGetOpenSearchResult() throws Exception {
		action.setType(SearchBookmark.TYPE_USER);
		action.setLoggedInUser(new User(3));
		action.setQueryString("Category WordPress Group Wiki");
		
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		SyndFeed syndFeed = action.getOpenSearchResult();
		assertNotNull(syndFeed);
		
		assertEquals("gnizr",syndFeed.getAuthor());
		assertNotNull(syndFeed.getTitle());
		assertNotNull(syndFeed.getUri());
		assertNotNull(syndFeed.getPublishedDate());
		assertNotNull(syndFeed.getEntries());
		assertNotNull(syndFeed.getLink());
		
		@SuppressWarnings("unchecked")
		List<SyndEntry> entries = syndFeed.getEntries();
		assertEquals(1,entries.size());
		
		OpenSearchModule mod = (OpenSearchModule)syndFeed.getModule(OpenSearchModule.URI);
		assertNotNull(mod);
		assertEquals(10,mod.getItemsPerPage());
		assertEquals(0,mod.getStartIndex());
		assertEquals(1,mod.getTotalResults());
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSearchBookmark.class.getResourceAsStream("/TestSearchBookmark-input.xml"));
	}

}
