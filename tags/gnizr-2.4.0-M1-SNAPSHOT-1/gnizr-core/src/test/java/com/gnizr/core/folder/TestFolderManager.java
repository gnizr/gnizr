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
package com.gnizr.core.folder;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;

public class TestFolderManager extends GnizrCoreTestBase {

	
	private FolderManager folderManager;
	private BookmarkDao bookmarkDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		folderManager = new FolderManager(getGnizrDao());
		bookmarkDao = getGnizrDao().getBookmarkDao();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestFolderManager.class
				.getResourceAsStream("/TestFolderManager-input.xml"));
	}
	
	public void test1GetUserFolder() throws Exception{
		Folder f1 = folderManager.getUserFolder(new User("jsmith"), "my folder1");
		assertEquals(3,f1.getSize());
		
		Folder fx = folderManager.getUserFolder(new User("jsmith"), "my folder1 fjfjfjf");
		assertNull(fx);
	}
	
	public void testDeleteUserFolder() throws Exception{
		Folder f1 = folderManager.getUserFolder(new User("jsmith"), "my folder1");
		assertEquals(3,f1.getSize());
		boolean ok = folderManager.deleteUserFolder(new User("jsmith"), "my folder1");
		assertTrue(ok);
		
		f1 = folderManager.getUserFolder(new User("jsmith"), "my folder1");
		assertNull(f1);
	}
	
	public void testPurgeUserFolder() throws Exception{
		Folder f1 = folderManager.getUserFolder(new User("jsmith"), "my folder1");
		int size = f1.getSize();
		assertTrue(f1.getSize() > 0);
		int cnt = folderManager.purgeFolder(new User("jsmith"), "my folder1");
		assertEquals(size,cnt);
	}
	public void testPageUserFolders() throws Exception{
		DaoResult<Folder> result = folderManager.pageUserFolders(new User("jsmith"), 0, 2);
		assertEquals(3,result.getSize());
		assertEquals(2,result.getResult().size());
	}
	
	public void testGetUserFolderCount() throws Exception{
		int cnt = folderManager.getUserFolderCount(new User("hchen1"));
		assertEquals(0,cnt);
		
		cnt = folderManager.getUserFolderCount(new User(2));
		assertEquals(3,cnt);
	}
	
	public void testCreateFolder() throws Exception{
		Folder f1 = folderManager.createFolder(new User(2), "my folder1", "foo");
		assertNotNull(f1);
		assertEquals(1,f1.getId());
		assertEquals(3,f1.getSize());
		assertEquals("my folder1",f1.getName());
		
		int cnt = folderManager.getUserFolderCount(new User(2));
		assertEquals(3, cnt);
		
		Folder fXYZ = folderManager.createFolder(new User("jsmith"), "Project XYZ", "stuff");
		assertNotNull(fXYZ);
		assertEquals("Project XYZ",fXYZ.getName());
		assertEquals("stuff",fXYZ.getDescription());
	}
	
	
	public void testAddBookmarks() throws Exception{
		Folder myFolder = folderManager.createFolder(new User("hchen1"), "Harry's Folder", "");
		
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(300));
		boolean[] opOkay = folderManager.addBookmarks(new User("hchen1"), "Harry's Folder", bmarks);
		assertTrue(opOkay[0]);
		
		myFolder = folderManager.createFolder(new User("hchen1"), "Harry's Folder", "");
		assertEquals(1,myFolder.getSize());
		
		Bookmark bm300 = bookmarkDao.getBookmark(300);
		assertTrue(bm300.getFolderList().contains("Harry's Folder"));
		assertTrue(bm300.getFolderList().contains("my folder1"));
	}
	
	public void testRemoveBookmarks() throws Exception {
		Folder f1 = folderManager.createFolder(new User("jsmith"), "my folder1","");
		assertEquals(3,f1.getSize());
		
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(300));
		bmarks.add(new Bookmark(301));
		
		boolean[] opOkay = folderManager.removeBookmarks(new User("jsmith"), "my folder1", bmarks);
		for(boolean b : opOkay){
			assertTrue(b);
		}
		
		f1 = folderManager.createFolder(new User("jsmith"), "my folder1","");
		assertEquals(1,f1.getSize());
	}
	
	public void testUpdateUserFolder() throws Exception{
		Folder f1 = folderManager.getUserFolder(new User("jsmith"), "my folder1");
		f1.setDescription("new dsp");
		f1.setName("new title");
		boolean isOkay = folderManager.updateFolder(new User("jsmith"), f1);
		assertTrue(isOkay);
	}
	
	public void testPageContainedInFolder() throws Exception{
		User user = new User("jsmith");
		String url = "http://www.cnn.com/";
		DaoResult<Folder> result = folderManager.pageContainedInFolder(user, url, 0, 10);
		assertEquals(1,result.getSize());
		
		user = new User(1);
		url = "http://www.cnn3.com/";
		result = folderManager.pageContainedInFolder(user, url, 0, 10);
		assertEquals(1,result.getSize());
		Folder f = result.getResult().get(0);
		assertEquals(2,f.getUser().getId());
		assertEquals(3,f.getSize());
	}
	
	public void testPageBookmarksWithTag() throws Exception{
		DaoResult<Bookmark> result = folderManager.pageFolderContent(new User("jsmith"), "my folder1", "foo", 0, 10);
		assertEquals(1,result.getSize());
		
		Bookmark b = result.getResult().get(0);
		assertEquals(303,b.getId());
		
		result = folderManager.pageFolderContent(new User("jsmith"), "my folder1", "cnn", 0, 10);
		assertEquals(3,result.getSize());
		assertEquals(3,result.getResult().size());
	}
	
	public void testGetTagsSortByAlpha() throws Exception{
		Folder f = folderManager.getUserFolder(new User("jsmith"),"my folder1");
		assertNotNull(f);
		List<FolderTag> ftags = folderManager.getTagsSortByAlpha(f,2,FolderManager.ASCENDING_ORDER);
		assertEquals(2,ftags.size());
		assertEquals("cnn",ftags.get(0).getTag().getLabel());
		assertEquals("news",ftags.get(1).getTag().getLabel());
		
		ftags = folderManager.getTagsSortByAlpha(f,2,FolderManager.DESCENDING_ORDER);
		assertEquals(2,ftags.size());
		assertEquals("news",ftags.get(0).getTag().getLabel());
		assertEquals("cnn",ftags.get(1).getTag().getLabel());	
	}
	
	public void testGetTagsSortByFreq() throws Exception{
		Folder f = folderManager.getUserFolder(new User("jsmith"),"my folder1");
		assertNotNull(f);
		List<FolderTag> ftags = folderManager.getTagsSortByFreq(f,1,FolderManager.DESCENDING_ORDER);
		assertEquals(3,ftags.size());
		assertEquals(3,ftags.get(0).getCount());
		assertEquals(3,ftags.get(1).getCount());
		assertEquals(1,ftags.get(2).getCount());
	}

	public void testPageBookmarkHasGeomMarker() throws Exception{
		DaoResult<Bookmark> result = folderManager.pageBookmarkHasGeomMarker(new Folder(1), 0, 10);
		assertEquals(0,result.getSize());			
	}
	
}
