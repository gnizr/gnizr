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
package com.gnizr.web.action.folder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.SessionConstants;
import com.gnizr.web.action.folder.ListUserFolderContent;
import com.opensymphony.xwork.ActionSupport;

public class TestListUserFolderContent extends GnizrWebappTestBase {

	
	private FolderManager folderManager;
	private ListUserFolderContent action;
	private Map session = new HashMap();
	
	protected void setUp() throws Exception {
		super.setUp();
		folderManager = new FolderManager(getGnizrDao());
		action = new ListUserFolderContent();
		action.setFolderManager(folderManager);
		action.setSession(session);
		
		initFolderData();		
	}
	
	private void initFolderData() throws Exception{
		folderManager.createFolder(new User("hchen1"), "folder1", "");
		List<Bookmark> bookmarks = new ArrayList<Bookmark>();
		bookmarks.add(new Bookmark(300));
		bookmarks.add(new Bookmark(301));
		bookmarks.add(new Bookmark(302));
		bookmarks.add(new Bookmark(303));
		
		folderManager.addBookmarks(new User("hchen1"), "folder1", bookmarks);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestListUserFolderContent.class.getResourceAsStream("/TestListUserFolderContent-input.xml"));
	}
	
	public void testGoNoPagingData() throws Exception{
		action.setUsername("hchen1");
		action.setFolderName("folder1");
		
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		int max = (Integer)session.get(SessionConstants.PAGE_TOTAL_COUNT);
		assertEquals(1,max);
		
		assertEquals(10,action.getPerPageCount());
		assertEquals(1,action.getPage());
		
		Folder f = action.getFolder();
		assertNotNull(f);
		assertEquals("folder1",f.getName());
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(4,bmarks.size());		
	}
	
	public void testGoPageCount2() throws Exception{
		action.setUsername("hchen1");
		action.setFolderName("folder1");
		action.setPerPageCount(2);
	
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		assertEquals(2,action.getPerPageCount());
		assertEquals(1,action.getPage());
		
		assertEquals(2,session.get(SessionConstants.PAGE_TOTAL_COUNT));
		
		Folder f = action.getFolder();
		assertNotNull(f);
		assertEquals("folder1",f.getName());
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(2,bmarks.size());			
	}
	
	public void testGoLastPage() throws Exception{
		action.setUsername("hchen1");
		action.setFolderName("folder1");
		action.setPerPageCount(1);
		action.setPage(4);
		
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		assertEquals(1,action.getPerPageCount());
		assertEquals(4,action.getPage());
		assertEquals(3,session.get(SessionConstants.PREVIOUS_PAGE_NUM));
		assertEquals(0,session.get(SessionConstants.NEXT_PAGE_NUM));
		
		assertEquals(4,session.get(SessionConstants.PAGE_TOTAL_COUNT));
		
		Folder f = action.getFolder();
		assertNotNull(f);
		assertEquals("folder1",f.getName());
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());			
	}
	
	public void testGoPageOffsetCountOutofBound() throws Exception{
		action.setUsername("hchen1");
		action.setFolderName("folder1");
		action.setPerPageCount(1);
		action.setPage(1000);
		
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		assertEquals(1,action.getPerPageCount());
		assertEquals(1000,action.getPage());
		assertEquals(0,session.get(SessionConstants.PREVIOUS_PAGE_NUM));
		assertEquals(0,session.get(SessionConstants.NEXT_PAGE_NUM));
		
		assertEquals(4,session.get(SessionConstants.PAGE_TOTAL_COUNT));
		
		Folder f = action.getFolder();
		assertNotNull(f);
		assertEquals("folder1",f.getName());
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(0,bmarks.size());
	}
	
	public void testGoLoggedInUser() throws Exception{
		action.setLoggedInUser(new User("hchen1"));
		action.setFolderName("folder1");
		action.setPerPageCount(1);
		action.setPage(4);
		
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		assertEquals(1,action.getPerPageCount());
		assertEquals(4,action.getPage());
		assertEquals(3,session.get(SessionConstants.PREVIOUS_PAGE_NUM));
		assertEquals(0,session.get(SessionConstants.NEXT_PAGE_NUM));
		
		assertEquals(4,session.get(SessionConstants.PAGE_TOTAL_COUNT));
		
		Folder f = action.getFolder();
		assertNotNull(f);
		assertEquals("folder1",f.getName());
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());				
	}

	public void testGoUsernameOverrideLoggedInUser() throws Exception{
		
		action.setLoggedInUser(new User("fdsfas"));
		action.setUsername("hchen1");
		action.setFolderName("folder1");
		action.setPerPageCount(1);
		action.setPage(4);
		
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		assertEquals(1,action.getPerPageCount());
		assertEquals(4,action.getPage());
		assertEquals(3,session.get(SessionConstants.PREVIOUS_PAGE_NUM));
		assertEquals(0,session.get(SessionConstants.NEXT_PAGE_NUM));
		
		assertEquals(4,session.get(SessionConstants.PAGE_TOTAL_COUNT));
		
		Folder f = action.getFolder();
		assertNotNull(f);
		assertEquals("folder1",f.getName());
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());	
			
	}
	
	public void testNoFolderDontCrash() throws Exception{
		action.setLoggedInUser(new User("hchen1"));
		action.setFolderName("fdsf");
	
		String r = action.execute();
		assertEquals(ActionSupport.INPUT,r);	
	}
	
	public void testTagsInFolder() throws Exception{
		action.setLoggedInUser(new User("hchen1"));
		action.setFolderName("folder1");
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(4,bmarks.size());
		
	}
	
	public void testGoPageBookmarkWithTag() throws Exception{
		action.setLoggedInUser(new User("hchen1"));
		action.setFolderName("folder1");
		action.setTag("webwork");
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(1,bmarks.size());
		Bookmark b = bmarks.get(0);
		assertEquals(302,b.getId());		
	}
	
	public void testGoPageBookmarkWithTagNoCrash() throws Exception{
		action.setLoggedInUser(new User("hchen1"));
		action.setFolderName("folder1");
		action.setTag("jfkdjsalkfdja");
		String r = action.execute();
		assertEquals(ActionSupport.SUCCESS,r);
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(0,bmarks.size());
	}
}
