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
package com.gnizr.core.web.action.clustermap;

import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.search.Search;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestClusterSearchResult2 extends GnizrWebappTestBase {

	private ClusterSearchResult action;
	private BookmarkManager bmManager;
	
	public TestClusterSearchResult2(){
		bmManager = new BookmarkManager(getGnizrDao());
	}
	protected void setUp() throws Exception {
		super.setUp();
		action = new ClusterSearchResult();
		action.setSearch(new Search(getGnizrDao()));
		
		User user1 = new User(1);
		Link link1 = new Link(1);
		Link link2 = new Link(2);
		Link link3 = new Link(3);
		Link link205 = new Link(205);
		
		Bookmark bm1 = new Bookmark(user1,link1);
		bm1.setTags("tags1 tags2 tags3");
		bm1.setTitle("title foobar1");
		
		Bookmark bm2 = new Bookmark(user1,link2);
		bm2.setTags("tags4 tags5 tags6");
		bm2.setTitle("title foobar1");
		
		Bookmark bm3 = new Bookmark(user1,link3);
		bm3.setTags("tags1 tags");
		bm3.setTitle("title3 foobar2");
		
		Bookmark bm4 = new Bookmark(user1,link205);
		bm4.setTags("");
		bm4.setTitle("del.icio.us");
		
		assertTrue(bmManager.addBookmark(bm1)>0);
		assertTrue(bmManager.addBookmark(bm2)>0);
		assertTrue(bmManager.addBookmark(bm3)>0);
		assertTrue(bmManager.addBookmark(bm4)>0);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestClusterSearchResult2.class
				.getResourceAsStream("/TestClusterSearchResult2-input.xml"));
	}

	
	public void testGoSearchText2() throws Exception{
		action.setType("text");
		action.setQueryString("del.icio.us");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);
		
		List<Integer> rootCluster = action.getRootCluster();
		assertEquals(1,rootCluster.size());
		
		List<Tag> tagKey = action.getTagClusterKey();
		assertEquals(1,tagKey.size());		
		
		Tag t0 = tagKey.get(0);
		assertEquals(0,t0.getId());
		
		List<Bookmark> bm = action.getBookmarks();
		assertEquals(1,bm.size());
		assertEquals(205,bm.get(0).getLink().getId());
	}
	
	public void testGoSearchText() throws Exception{
		action.setType("text");
		action.setQueryString("foobar2");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);
		
		List<Integer> rootCluster = action.getRootCluster();
		assertEquals(1,rootCluster.size());
		
		List<User> userKey = action.getUserClusterKey();
		assertEquals(1,userKey.size());
		assertEquals("gnizr",userKey.get(0).getUsername());
		
		Map<String,List<Integer>> userClusterMap = action.getUserClusterMap();
		assertEquals(1,userClusterMap.size());
		assertEquals(1,userClusterMap.get("u1").size());
		
		List<Tag> tagKey = action.getTagClusterKey();
		assertEquals(2,tagKey.size());		
		
		Tag t1 = tagKey.get(0);
		Tag t2 = tagKey.get(1);
		
		Map<String,List<Integer>> tagClusterMap = action.getTagClusterMap();
		assertEquals(2,tagClusterMap.size());
		
		List<Integer> tc1 = tagClusterMap.get("t"+t1.getId());
		assertEquals(1,tc1.size());

		List<Integer> tc2 = tagClusterMap.get("t"+t2.getId());
		assertEquals(1,tc2.size());
		
		List<Bookmark> bm = action.getBookmarks();
		assertEquals(1,bm.size());
		assertEquals("title3 foobar2",bm.get(0).getTitle());
	}
	
	
}
