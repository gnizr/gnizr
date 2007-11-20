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
import com.gnizr.core.folder.FolderManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

public class TestFolderTagListener extends GnizrCoreTestBase {

	private BookmarkManager bookmarkManager;
	private FolderTagListener folderTagListener;
	private FolderManager folderManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		folderManager = new FolderManager(getGnizrDao());
		
		folderTagListener = new FolderTagListener(folderManager);
		bookmarkManager = new BookmarkManager(getGnizrDao());
		bookmarkManager.addBookmarkListener(folderTagListener);
	}

	protected void tearDown() throws Exception {
		bookmarkManager.shutdown();
		super.tearDown();
	}
	
	public void testAddBookmark2Folder() throws Exception{		
		User user4 = new User(4);
		
		int totalFolderCnt = folderManager.getUserFolderCount(user4);
		assertEquals(0,totalFolderCnt);
		
		Bookmark bmark = new Bookmark();
		bmark.setLink(new Link(202));
		bmark.setUser(user4);
		bmark.setTags("foo folder:work_related cnn folder:job");
		bmark.setTitle("cnn homepage");
	
		int id = bookmarkManager.addBookmark(bmark);
		assertTrue(id > 0);
		
		bookmarkManager.shutdown();
		
		totalFolderCnt = folderManager.getUserFolderCount(user4);
		assertEquals(2,totalFolderCnt);
		
		DaoResult<Bookmark> result = folderManager.pageFolderContent(user4, "work related", 0, 10);
		assertEquals(1,result.getSize());
		assertEquals(1,result.getResult().size());
		assertEquals(202,result.getResult().get(0).getLink().getId());
	}
	
	public void testDeleteBookmark() throws Exception{
		Folder f1 = folderManager.createFolder(new User("jsmith"), "my folder1", "");
		assertEquals(3,f1.getSize());
		
		boolean isOkay = bookmarkManager.deleteBookmark(new Bookmark(300));
		assertTrue(isOkay);
		
		bookmarkManager.shutdown();
		
		f1 = folderManager.createFolder(new User("jsmith"), "my folder1", "");
		assertEquals(2,f1.getSize());
	}
	
	public void testUpdateBookmark() throws Exception{
		Folder f1 = folderManager.createFolder(new User("jsmith"), "my folder1", "");
		assertEquals(3,f1.getSize());
		
		Bookmark bm300 = bookmarkManager.getBookmark(300);
		bm300.setTags("folder:xyz gn:folder=my_folder2");
		
		boolean isOkay = bookmarkManager.updateBookmark(bm300);	
		assertTrue(isOkay);
			
		bookmarkManager.shutdown();
		f1 = folderManager.createFolder(new User("jsmith"), "my folder1", "");
		assertEquals(2,f1.getSize());

		Folder f2 = folderManager.createFolder(new User("jsmith"), "my folder2", "");
		assertEquals(1,f2.getSize());
		
		Folder f4 = folderManager.createFolder(new User("jsmith"), "xyz", "");
		assertEquals(1,f4.getSize());		
	}	
	
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestFolderTagListener.class.getResourceAsStream("/TestFolderTagListener-input.xml"));
	}
	
	
	

}
