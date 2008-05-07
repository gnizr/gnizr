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
package com.gnizr.web.action.bookmark;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.link.LinkManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestEditBookmark2 extends GnizrWebappTestBase {

	private EditBookmark action;
	private BookmarkManager bookmarkManager;
	private UserManager userManager;
	private LinkManager linkManager;
	private FolderManager folderManager;
	
	@Override
	protected void setUp() throws Exception {		
		super.setUp();
		bookmarkManager = new BookmarkManager(getGnizrDao());
		userManager = new UserManager(getGnizrDao());
		linkManager = new LinkManager(getGnizrDao());
		folderManager = new FolderManager(getGnizrDao());
		action = new EditBookmark();
		action.setBookmarkManager(bookmarkManager);
		action.setLinkManager(linkManager);
		action.setFolderManager(folderManager);
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestEditBookmark2.class.getResourceAsStream("/TestEditBookmark-input.xml"));
	}

	public void testDoFetchBookmark() throws Exception{
		User loggedInUser = userManager.getUser("hchen1");
		action.setUrl("http://cnn.com");
		action.setTags("cnn news");
		action.setTitle("CNN News Home Page");
		action.setLoggedInUser(loggedInUser);
		String result = action.doFetchBookmark();
		assertEquals(ActionSupport.SUCCESS,result);
		
		Bookmark bm = action.getEditBookmark();
		assertNotNull(bm);
		
		assertEquals("http://cnn.com",bm.getLink().getUrl());
		assertEquals("cnn news",bm.getTags());
		assertEquals("CNN News Home Page",bm.getTitle());
	}
	
	public void testDeleteBookmark1() throws Exception{
		Bookmark bm = bookmarkManager.getBookmark(300);
		assertNotNull(bm);		
		
		User loggedInUser = userManager.getUser("gnizr");
		action.setLoggedInUser(loggedInUser);
		action.setUrl("http://zirr.us/");
		String result = action.doDeleteBookmark();
		assertEquals(ActionSupport.SUCCESS,result);
		
		bm = bookmarkManager.getBookmark(300);
		assertNull(bm);				
	}
	
	
	public void testDeleteBookmark2() throws Exception{
		Bookmark bm = bookmarkManager.getBookmark(300);
		assertNotNull(bm);		
		
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		action.setUrl("http://zirr.us/");
		String result = action.doDeleteBookmark();
		assertEquals(ActionSupport.SUCCESS,result);
		
		bm = bookmarkManager.getBookmark(300);
		assertNotNull(bm);				
	}
	
	public void testDeleteBookmark3() throws Exception{
		Bookmark bm = bookmarkManager.getBookmark(300);
		assertNotNull(bm);		
		
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		String result = action.doDeleteBookmark();
		assertEquals(ActionSupport.INPUT,result);
		
		bm = bookmarkManager.getBookmark(300);
		assertNotNull(bm);				
	}
	
	public void testAddBookmark() throws Exception {
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		action.setTitle("CNN homepage");
		action.setUrl("http://cnn.com");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);

		int bmid = bookmarkManager.getBookmarkId(loggedInUser, "http://cnn.com");
		assertTrue(bmid > 0);
		Bookmark bm = bookmarkManager.getBookmark(bmid);
		assertEquals("CNN homepage",bm.getTitle());
		assertEquals("",bm.getNotes());
	}
	
	public void testAddBookmark2() throws Exception {
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		action.setTitle("CNN homepage");
		action.setNotes("<script type=\"javascript\">alert(343)</script>notes goes here...<h1>233</h1>");
		action.setUrl("http://cnn.com");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);

		int bmid = bookmarkManager.getBookmarkId(loggedInUser, "http://cnn.com");
		assertTrue(bmid > 0);
		Bookmark bm = bookmarkManager.getBookmark(bmid);
		assertEquals("CNN homepage",bm.getTitle());
		assertEquals("notes goes here... <h3>233</h3>",bm.getNotes());
	}
	
	public void testUpdateBookmark() throws Exception{
		User loggedInUser = userManager.getUser("gnizr");
		action.setLoggedInUser(loggedInUser);
		action.setTags("abc");
		action.setUrl("http://zirr.us/");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);
		
		Bookmark bm = bookmarkManager.getBookmark(300);
		assertEquals("abc",bm.getTags());
	}
	
	public void testUpdateBookmark2() throws Exception {
		User loggedInUser = userManager.getUser("gnizr");
		action.setLoggedInUser(loggedInUser);
		action.setTags("abc");
		action.setId(300);
		action.setTitle("abc title and abc title");
		action.setUrl("http://zirr.us/");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);
		
		Bookmark bm = bookmarkManager.getBookmark(300);
		assertEquals("abc",bm.getTags());
		assertEquals("abc title and abc title",bm.getTitle());
	}
	
	public void testUpdateBookmark3() throws Exception{
		User loggedInUser = userManager.getUser("gnizr");
		action.setLoggedInUser(loggedInUser);
		action.setTags("abc");
		action.setId(300);
		action.setUrl("http://zirr.us.com/");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);
		
		Bookmark bm = bookmarkManager.getBookmark(300);
		assertEquals("abc",bm.getTags());
		assertEquals("http://zirr.us.com/",bm.getLink().getUrl());
	}
	
	public void testUpdateBookmark4() throws Exception{
		User loggedInUser = new User("gnizr");
		action.setLoggedInUser(loggedInUser);
		action.setUrl("http://newurl.com");
		action.setOldUrl("http://zirr.us/");
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);
		
		Bookmark bm = bookmarkManager.getBookmark(300);
		Link link = bm.getLink();
		assertTrue((link.getId() != 202));
		assertEquals("http://newurl.com",link.getUrl());
		
		int bmid = bookmarkManager.getBookmarkId(loggedInUser, "http://zirr.us/");
		assertTrue((bmid <=0));
	}
	
	public void testAddPointMarker1() throws Exception{
		PointMarker pm1 = new PointMarker();
		pm1.setId(-1);
		pm1.setMarkerIconId(5);
		pm1.setNotes("this is marker1 notes");
		pm1.setPoint(10.0, 20.00);
		
		PointMarker pm2 = new PointMarker();
		pm2.setId(-2);
		pm2.setMarkerIconId(10);
		pm2.setNotes("this is marker2 notes <h1>hello</h1> <br> <hr>");
		pm2.setPoint(20.0, 40.00);
		
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		action.setTitle("CNN homepage");
		action.setUrl("http://cnn.com");
		action.setPointMarkers(new PointMarker[]{pm1,pm2});
		
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);

		int bmid = bookmarkManager.getBookmarkId(loggedInUser, "http://cnn.com");
		assertTrue(bmid > 0);
		Bookmark bm = bookmarkManager.getBookmark(bmid);
		assertEquals("CNN homepage",bm.getTitle());
		assertEquals("",bm.getNotes());
		
		PointMarker[] resultPtMarkers = action.getPointMarkers();
		
		List<PointMarker> pms = bookmarkManager.getPointMarkers(bm);
		assertEquals(2,pms.size());
		int found = 0;
		for(PointMarker p : pms){
			int id = p.getId();
			for(PointMarker ep : resultPtMarkers){
				if(ep.getId() == id){
					assertEquals(ep.getNotes(),p.getNotes());
					assertEquals(ep.getMarkerIconId(),p.getMarkerIconId());
					assertTrue(ep.getPoint().equals(p.getPoint()));
					assertFalse(ep.getNotes().contains("h1"));
					assertFalse(ep.getNotes().contains("br"));
					assertFalse(ep.getNotes().contains("hr"));
					found++;
				}
			}
		}
		assertEquals(2,found);
		bookmarkManager.deleteBookmark(bm);
	}
	
	public void testAddPointMarker2() throws Exception{
		PointMarker pm1 = new PointMarker();
		pm1.setId(-1);
		pm1.setMarkerIconId(5);
		pm1.setNotes("this is marker1 notes");
		pm1.setPoint(10.0, 20.00);
		
		PointMarker pm2 = new PointMarker();
		pm2.setId(-2);
		pm2.setMarkerIconId(10);
		pm2.setNotes("this is marker2 notes");
		pm2.setPoint(20.0, 40.00);
				
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		action.setTitle("CNN homepage");
		action.setUrl("http://cnn.com");
		action.setPointMarkers(new PointMarker[]{pm1});
		
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);

		int bmid = bookmarkManager.getBookmarkId(loggedInUser, "http://cnn.com");
		assertTrue(bmid > 0);
		Bookmark bm = bookmarkManager.getBookmark(bmid);
		assertEquals("CNN homepage",bm.getTitle());
		assertEquals("",bm.getNotes());
		
		action.setPointMarkers(new PointMarker[]{pm1,pm2});
		result = action.execute();		
		
		PointMarker[] resultPtMarkers = action.getPointMarkers();
		
		List<PointMarker> pms = bookmarkManager.getPointMarkers(bm);
		assertEquals(2,pms.size());
		int found = 0;
		for(PointMarker p : pms){
			int id = p.getId();
			for(PointMarker ep : resultPtMarkers){
				if(ep.getId() == id){
					assertEquals(ep.getNotes(),p.getNotes());
					assertEquals(ep.getMarkerIconId(),p.getMarkerIconId());
					assertTrue(ep.getPoint().equals(p.getPoint()));
					found++;
				}
			}
		}
		assertEquals(2,found);
		bookmarkManager.deleteBookmark(bm);
	}
	
	public void testAddPointMarker3() throws Exception{
		PointMarker pm1 = new PointMarker();
		pm1.setId(-1);
		pm1.setMarkerIconId(5);
		pm1.setNotes("this is marker1 notes");
		pm1.setPoint(10.0, 20.00);
		
		PointMarker pm2 = new PointMarker();
		pm2.setId(-2);
		pm2.setMarkerIconId(10);
		pm2.setNotes("this is marker2 notes");
		pm2.setPoint(20.0, 40.00);
				
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		action.setTitle("CNN homepage");
		action.setUrl("http://cnn.com");
		action.setPointMarkers(new PointMarker[]{pm1});
		
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);

		int bmid = bookmarkManager.getBookmarkId(loggedInUser, "http://cnn.com");
		assertTrue(bmid > 0);
		Bookmark bm = bookmarkManager.getBookmark(bmid);
		assertEquals("CNN homepage",bm.getTitle());
		assertEquals("",bm.getNotes());
		
		pm1.setNotes("this is new notes for pm1");
		pm1.setPoint(100.100, -99.00);
		pm1.setMarkerIconId(0);
		
		action.setPointMarkers(new PointMarker[]{pm1,pm2});
		result = action.execute();		
		
		PointMarker[] resultPtMarkers = action.getPointMarkers();
		
		List<PointMarker> pms = bookmarkManager.getPointMarkers(bm);
		assertEquals(2,pms.size());
		int found = 0;
		for(PointMarker p : pms){
			int id = p.getId();
			for(PointMarker ep : resultPtMarkers){
				if(ep.getId() == id){
					assertEquals(ep.getNotes(),p.getNotes());
					assertEquals(ep.getMarkerIconId(),p.getMarkerIconId());
					assertTrue(ep.getPoint().equals(p.getPoint()));
					found++;
				}
			}
		}
		assertEquals(2,found);
		bookmarkManager.deleteBookmark(bm);
	}
	
	public void testAddPointMarker4() throws Exception{
		PointMarker pm1 = new PointMarker();
		pm1.setId(-1);
		pm1.setMarkerIconId(5);
		pm1.setNotes("this is marker1 notes");
		pm1.setPoint(10.0, 20.00);
		
		PointMarker pm2 = new PointMarker();
		pm2.setId(-2);
		pm2.setMarkerIconId(10);
		pm2.setNotes("this is marker2 notes");
		pm2.setPoint(20.0, 40.00);
				
		User loggedInUser = userManager.getUser("hchen1");
		action.setLoggedInUser(loggedInUser);
		action.setTitle("CNN homepage");
		action.setUrl("http://cnn.com");
		action.setPointMarkers(new PointMarker[]{pm1});
		
		String result = action.execute();
		assertEquals(ActionSupport.SUCCESS,result);

		int bmid = bookmarkManager.getBookmarkId(loggedInUser, "http://cnn.com");
		assertTrue(bmid > 0);
		Bookmark bm = bookmarkManager.getBookmark(bmid);
		assertEquals("CNN homepage",bm.getTitle());
		assertEquals("",bm.getNotes());
		
		action.setPointMarkers(new PointMarker[]{pm2});
		result = action.execute();		
		
		PointMarker[] resultPtMarkers = action.getPointMarkers();
		
		List<PointMarker> pms = bookmarkManager.getPointMarkers(bm);
		assertEquals(1,pms.size());
		int found = 0;
		for(PointMarker p : pms){
			int id = p.getId();
			for(PointMarker ep : resultPtMarkers){
				if(ep.getId() == id){
					assertEquals(ep.getNotes(),p.getNotes());
					assertEquals(ep.getMarkerIconId(),p.getMarkerIconId());
					assertTrue(ep.getPoint().equals(p.getPoint()));
					found++;
				}
			}
		}
		assertEquals(1,found);
		bookmarkManager.deleteBookmark(bm);
	}
}
