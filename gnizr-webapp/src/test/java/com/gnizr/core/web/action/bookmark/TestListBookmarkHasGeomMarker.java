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
package com.gnizr.core.web.action.bookmark;

import java.util.Arrays;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.PointMarker;
import com.opensymphony.xwork.ActionSupport;

public class TestListBookmarkHasGeomMarker extends GnizrWebappTestBase {

	private ListBookmarkHasGeomMarker action;
	private BookmarkManager bookmarkManager;
	private FolderManager folderManager;
	private UserManager userManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		bookmarkManager = new BookmarkManager(getGnizrDao());
		folderManager = new FolderManager(getGnizrDao());
		userManager = new UserManager(getGnizrDao());
		action = new ListBookmarkHasGeomMarker();
		action.setUserManager(userManager);
		action.setBookmarkManager(bookmarkManager);
		action.setFolderManager(folderManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestListBookmarkHasGeomMarker.class
				.getResourceAsStream("/TestListBookmarkHasGeomMarker-input.xml"));
	}

	public void testListBmarkInFolder() throws Exception{
		PointMarker pm = new PointMarker();
		pm.setMarkerIconId(0);
		pm.setNotes("notes");
		pm.setPoint(10.0, 10.0);
		
		PointMarker[] markers = new PointMarker[]{pm};
		bookmarkManager.addPointMarkers(new Bookmark(307), Arrays.asList(markers));
		
		action.setUsername("gnizr");
		action.setFolder("my folder1");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());
		assertEquals(307,bmarks.get(0).getId());
		
		action.setFolder("my folder2");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());
		assertEquals(307,bmarks.get(0).getId());
		
		assertTrue(bookmarkManager.deleteBookmark(new Bookmark(307)));
	}
	
	public void testListBmarkInArchive() throws Exception{
		PointMarker pm = new PointMarker();
		pm.setMarkerIconId(0);
		pm.setNotes("notes");
		pm.setPoint(10.0, 10.0);
		
		PointMarker[] markers = new PointMarker[]{pm};
		bookmarkManager.addPointMarkers(new Bookmark(307), Arrays.asList(markers));
		
		action.setUsername("hchen1");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());
		assertEquals(307,bmarks.get(0).getId());
		
		assertTrue(bookmarkManager.deleteBookmark(new Bookmark(307)));
	}
	
}
