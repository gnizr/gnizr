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
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestEditUserFolder extends GnizrWebappTestBase {

	private FolderManager folderManager;
	private BookmarkManager bookmarkManager;
	private EditUserFolder action;
	
	protected void setUp() throws Exception {
		super.setUp();
		folderManager = new FolderManager(getGnizrDao());
		bookmarkManager = new BookmarkManager(getGnizrDao());
		action = new EditUserFolder();
		action.setFolderManager(folderManager);
		action.setBookmarkManager(bookmarkManager);
		
		initData();
	}
	
	private void initData() throws Exception{
		User user2 = new User(2);
		Folder xFolder = folderManager.createFolder(user2, "x-folder", "");
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(307));
		bmarks.add(new Bookmark(305));
		
		boolean[] opOkay = folderManager.addBookmarks(user2, "x-folder",bmarks);
		for(boolean b : opOkay){
			assertTrue(b);
		}
		
		xFolder = folderManager.getUserFolder(user2, "x-folder");
		assertEquals(2,xFolder.getSize());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestEditUserFolder.class.getResourceAsStream("/TestEditUserFolder-input.xml"));
	}
	
	public void testDoRemoveBookmark1() throws Exception{
		action.setLoggedInUser(new User(2));
		action.setUrl("http://www.gamefaqs.com/console/wii/game/928519.html");
		action.setFolderName("x-folder");
		String code = action.doRemoveBookmark();
		assertEquals(ActionSupport.SUCCESS,code);
		
		Folder xfolder = folderManager.getUserFolder(new User(2), "x-folder");
		assertEquals(1,xfolder.getSize());
		
		action.setUrl("http://www.nytimes.com/2006/12/10/travel/10Tibet.html?pagewanted=1&amp;ei=5087%0A&amp;em&amp;en=5ec229a46c166112&amp;ex=1165899600");
		code = action.doRemoveBookmark();
		assertEquals(ActionSupport.SUCCESS,code);
		
		xfolder = folderManager.getUserFolder(new User(2), "x-folder");
		assertEquals(0,xfolder.getSize());
	}
	
	public void testDoRemoveBookmark2() throws Exception{
		Folder xfolder = folderManager.getUserFolder(new User(1), "my folder1");
		assertEquals(1,xfolder.getSize());
		
		action.setLoggedInUser(new User(1));
		action.setUrl("http://www.gamefaqs.com/console/wii/game/928519.html");
		action.setFolderName("my folder1");
		action.setOwner("hchen1");
		String code = action.doRemoveBookmark();
		assertEquals(ActionSupport.SUCCESS,code);
		
		xfolder = folderManager.getUserFolder(new User(1), "my folder1");
		assertEquals(0,xfolder.getSize());		
	}
	
	public void testDoRemoveBookmark3() throws Exception{
		Folder xfolder = folderManager.getUserFolder(new User(1), "my folder1");
		assertEquals(1,xfolder.getSize());
		
		action.setLoggedInUser(new User(1));
		action.setUrl("http://www.gamefaqs.com/console/wii/game/928519.html");
		action.setFolderName("my folder1");
		String code = action.doRemoveBookmark();
		assertEquals(ActionSupport.SUCCESS,code);
		
		xfolder = folderManager.getUserFolder(new User(1), "my folder1");
		assertEquals(1,xfolder.getSize());		
	}
	
	
	public void testCreateFolderDefault() throws Exception{
		action.setLoggedInUser(new User(2));
		String code = action.execute();
		assertEquals(ActionSupport.INPUT,code);
		
		action.setFolderName("java_programming");
		code = action.execute();
		assertEquals(ActionSupport.INPUT,code);
	}
	
	public void testCreateFolder() throws Exception{		
		action.setLoggedInUser(new User(2));
		action.setFolderName("fff");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		Folder fff = folderManager.getUserFolder(new User(2), "fff");
		assertNotNull(fff);
		assertEquals(0,fff.getSize());
	}

	public void testUpdateFolder() throws Exception{
		action.setLoggedInUser(new User(2));
		action.setFolderName("x-folder");
		
		Folder folderData = new Folder();
		folderData.setDescription("new dsp");
		folderData.setName("new name");
		
		action.setFolder(folderData);
		
		String code = action.doUpdateFolder();
		assertEquals(ActionSupport.SUCCESS,code);
		
		Folder newNameFolder = folderManager.getUserFolder(new User(2), "new name");
		assertNotNull(newNameFolder);
		assertNull(folderManager.getUserFolder(new User(2), "x-folder"));
	
		// try renaming folder name that contains illegal character
		folderData.setName("java_programming");
		action.setFolder(folderData);
		code = action.doUpdateFolder();
		assertEquals(ActionSupport.INPUT,code);
		
	}
	
	public void testDeleteUserFolder() throws Exception{
		action.setLoggedInUser(new User(2));
		action.setFolderName("x-folder");
		String code = action.doDeleteFolder();
		assertEquals(ActionSupport.SUCCESS, code);
	}
	
	public void testPurgeFolder() throws Exception{
		action.setLoggedInUser(new User(2));
		action.setFolderName("x-folder");
		String code = action.doPurgeFolder();
		assertEquals(ActionSupport.SUCCESS, code);
	}
	
	public void testCreateMyBookmarksFolder() throws Exception{		
		int count = folderManager.getUserFolderCount(new User(3));
		assertEquals(0,count);
		action.setLoggedInUser(new User(3));
		String code = action.doCreateMyBookmarksFolder();
		assertEquals(ActionSupport.SUCCESS,code);
		count = folderManager.getUserFolderCount(new User(3));
		assertEquals(1,count);
		Folder f = folderManager.getUserFolder(new User(3),FolderManager.MY_BOOKMARKS_LABEL);
		assertNotNull(f);
	}

}
