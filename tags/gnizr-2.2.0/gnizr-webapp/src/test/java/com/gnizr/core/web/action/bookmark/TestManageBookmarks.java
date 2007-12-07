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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestManageBookmarks extends GnizrWebappTestBase {

	private ManageBookmarks action;
	private BookmarkManager bookmarkManager;
	private FolderManager folderManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		bookmarkManager = new BookmarkManager(getGnizrDao());
		folderManager = new FolderManager(getGnizrDao());
		action = new ManageBookmarks();
		action.setFolderManager(folderManager);
		action.setBookmarkManager(bookmarkManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestManageBookmarks.class.getResourceAsStream("/TestManageBookmarks-input.xml"));
	}
	
	public void testDoDeleteBookmarks() throws Exception{
		Bookmark b = bookmarkManager.getBookmark(301);
		assertNotNull(b);
		b = bookmarkManager.getBookmark(302);
		assertNotNull(b);
		b = bookmarkManager.getBookmark(303);
		assertNotNull(b);
		
		int[] delBmIds = {301,302,303};
		
		action.setLoggedInUser(new User(1));
		action.setBookmarkId(delBmIds);
		action.setOp(new String[]{ManageBookmarks.OP_DELETE_BOOKMARKS});
		String code  = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		b = bookmarkManager.getBookmark(301);
		assertNull(b);
		b = bookmarkManager.getBookmark(302);
		assertNull(b);
		b = bookmarkManager.getBookmark(303);
		assertNull(b);
	}
	
	public void testDoRemoveFromFolder() throws Exception{
		Folder f = folderManager.getUserFolder(new User(1), "my folder1");
		assertEquals(3,f.getSize());
		
		int[] bmIds = {301,302,303};
		
		action.setLoggedInUser(new User(1));
		action.setOp(new String[]{ManageBookmarks.OP_REMOVE_FROM_FOLDER});
		action.setFolder("my folder1");
		action.setBookmarkId(bmIds);
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		f = folderManager.getUserFolder(new User(1), "my folder1");
		assertEquals(0,f.getSize());
	}
	
	public void testDoAddToFolder() throws Exception{
		Folder f = folderManager.getUserFolder(new User(1), "my folder3");
		assertEquals(0,f.getSize());
		
		int[] bmIds = {301,302,303};
		
		action.setLoggedInUser(new User(1));
		action.setOp(new String[]{ManageBookmarks.OP_ADD_TO_FOLDER});
		action.setFolder("my folder3");
		action.setBookmarkId(bmIds);
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		f = folderManager.getUserFolder(new User(1), "my folder3");
		assertEquals(3,f.getSize());
	}
	
	public void testDoAddToFolder2() throws Exception{
		Folder f = folderManager.getUserFolder(new User(1), "my folder4");
		assertNull(f);
		
		int[] bmIds = {301,302,303};
		
		action.setLoggedInUser(new User(1));
		action.setOp(new String[]{ManageBookmarks.OP_ADD_TO_FOLDER});
		action.setFolder("my folder4");
		action.setBookmarkId(bmIds);
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		f = folderManager.getUserFolder(new User(1), "my folder4");
		assertEquals(3,f.getSize());
	}
	

}
