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
package com.gnizr.core.web.action.folder;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestUpdateOwnership extends GnizrWebappTestBase {

	private BookmarkManager bookmarkManager;
	private FolderManager folderManager;
	private UserManager userManager;
	
	private UpdateOwnership action;
	
	protected void setUp() throws Exception {
		super.setUp();
		bookmarkManager = new BookmarkManager(getGnizrDao());
		folderManager = new FolderManager(getGnizrDao());
		userManager = new UserManager(getGnizrDao());		
		action = new UpdateOwnership();
		action.setBookmarkManager(bookmarkManager);
		action.setFolderManager(folderManager);
		action.setUserManager(userManager);		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUpdateOwnership.class.getResourceAsStream("/TestUpdateOwnership-input.xml"));
	}

	public void testExecute() throws Exception{
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		DaoResult<Bookmark> bookmarks = folderManager.pageFolderContent(new User("hchen1"), "my folder1", 0, 100);
		assertEquals(3,bookmarks.getSize());
		for(Bookmark b : bookmarks.getResult()){
			assertEquals(2,b.getUser().getId());
			assertEquals("hchen1",b.getUser().getUsername());
		}
		
		bookmarks = folderManager.pageFolderContent(new User("jsmith"), "my folder2", 0, 100);
		assertEquals(3,bookmarks.getSize());
		for(Bookmark b : bookmarks.getResult()){
			assertEquals(3,b.getUser().getId());
			assertEquals("jsmith",b.getUser().getUsername());
		}
		
	}
	
}
