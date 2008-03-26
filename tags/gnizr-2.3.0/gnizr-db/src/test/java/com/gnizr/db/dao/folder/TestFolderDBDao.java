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
package com.gnizr.db.dao.folder;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;

public class TestFolderDBDao extends GnizrDBTestBase {

	//private static final Logger logger = Logger.getLogger(TestFolderDBDao.class);
	private FolderDBDao folderDao;

	protected void setUp() throws Exception {
		super.setUp();
		folderDao = new FolderDBDao(getDataSource());
	}

	public void testGetFolderById() throws Exception {
		Folder f1 = folderDao.getFolder(1);
		assertNotNull(f1);
		assertEquals(1, f1.getId());
		assertEquals("my folder1", f1.getName());
		assertEquals("stuff that matters", f1.getDescription());
		assertEquals("jsmith", f1.getUser().getUsername());
		assertNotNull(f1.getLastUpdated());
		assertEquals(3, f1.getSize());

		Folder f2 = folderDao.getFolder(2);
		assertNotNull(f2);
		assertEquals(1, f2.getSize());
	}

	public void testGetFolderByUserIdFolderName() throws Exception {
		Folder f1 = folderDao.getFolder(new User(2), "my folder1");
		assertNotNull(f1);
		assertEquals(1, f1.getId());
		assertEquals("my folder1", f1.getName());
		assertEquals("stuff that matters", f1.getDescription());
		assertEquals("jsmith", f1.getUser().getUsername());
		assertNotNull(f1.getLastUpdated());
		assertEquals(3, f1.getSize());
	}

	public void testCreateFolder() throws Exception {
		Folder folderX = new Folder();
		folderX.setName("X-files");
		folderX.setDescription("nothing interesting");
		folderX.setUser(new User(1));
		folderX.setLastUpdated(GregorianCalendar.getInstance().getTime());

		int id = folderDao.createFolder(folderX);
		assertTrue((id > 0));
		folderX.setId(id);

		Folder folderY = folderDao.getFolder(id);
		assertNotNull(folderY);
		assertEquals(folderX.getId(), folderY.getId());
		assertEquals(folderX.getName(), folderY.getName());
		assertEquals(folderX.getDescription(), folderY.getDescription());
		assertEquals(folderX.getUser().getId(), folderY.getUser().getId());
		assertNotNull(folderY.getUser().getUsername());
	}

	public void testDeleteFolder() throws Exception {
		Folder f1 = folderDao.getFolder(new User(2), "my folder1");
		assertNotNull(f1);

		List<FolderTag> fts = folderDao.findTagsInFolder(f1,0,FolderDao.SORT_BY_ALPHA,FolderDao.ASCENDING);
		assertEquals(3,fts.size());
		
		boolean delOkay = folderDao.deleteFolder(new User(2), "my folder1");
		assertTrue(delOkay);
		
		fts = folderDao.findTagsInFolder(f1,0,FolderDao.SORT_BY_ALPHA,FolderDao.ASCENDING);
		assertEquals(0,fts.size());
		
		f1 = folderDao.getFolder(new User(2), "my folder1");
		assertNull(f1);

		delOkay = folderDao.deleteFolder(new User(5), "my folder1");
		assertFalse(delOkay);

		Folder f3 = folderDao.getFolder(new User(2), "MY FOLDER1");
		assertNotNull(f3);
	}

	public void testPageFoldersByOwner() throws Exception {
		DaoResult<Folder> result = folderDao.pageFolders(new User(2), 0, 10);
		assertNotNull(result);
		assertEquals(3, result.getSize());
		assertEquals(3, result.getResult().size());

		result = folderDao.pageFolders(new User(2), 0, 1);
		assertNotNull(result);
		assertEquals(3, result.getSize());
		assertEquals(1, result.getResult().size());

		result = folderDao.pageFolders(new User(2), 1, 10);
		assertNotNull(result);
		assertEquals(3, result.getSize());
		assertEquals(2, result.getResult().size());
	}

	public void testUpdateFolder() throws Exception {
		Folder f1 = folderDao.getFolder(new User(2), "my folder1");
		Date before = f1.getLastUpdated();

		f1.setDescription("important data");
		f1.setLastUpdated(GregorianCalendar.getInstance().getTime());
		f1.setName("my new folder1 name");
		boolean isOkay = folderDao.updateFolder(f1);
		assertTrue(isOkay);

		f1 = folderDao.getFolder(f1.getId());
		assertEquals("important data", f1.getDescription());
		assertEquals("my new folder1 name", f1.getName());
		assertTrue(before.before(f1.getLastUpdated()));
	}

	public void testPageBookmarks() throws Exception {
		DaoResult<Bookmark> result = folderDao.pageBookmarks(new Folder(1), 0,
				10);
		assertNotNull(result);
		assertEquals(3, result.getSize());
		assertEquals(3, result.getResult().size());

		List<Bookmark> bmarks = result.getResult();
		assertEquals(303, bmarks.get(0).getId());
		assertEquals(301, bmarks.get(1).getId());
		assertEquals(300, bmarks.get(2).getId());

		Bookmark bm303 = bmarks.get(0);
		assertTrue(bm303.getFolderList().contains("my folder1"));

		result = folderDao.pageBookmarks(new Folder(1), 1, 10);
		assertNotNull(result);
		assertEquals(3, result.getSize());
		assertEquals(2, result.getResult().size());

		result = folderDao.pageBookmarks(new Folder(1), 1, 1);
		assertNotNull(result);
		assertEquals(3, result.getSize());
		assertEquals(1, result.getResult().size());
	}

	public void testPageBookmarks2() throws Exception {
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = null;
		Date d1 = null;
		Date d2 = null;
		Date d3 = null;

		result = folderDao.pageBookmarks(new Folder(1), 0, 3,
				FolderDao.SORT_BY_BMRK_CREATED_ON, FolderDao.ASCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getCreatedOn();
		d2 = bmarks.get(1).getCreatedOn();
		d3 = bmarks.get(2).getCreatedOn();
		assertTrue(d1.before(d2));
		assertTrue(d2.before(d3));

		result = folderDao.pageBookmarks(new Folder(1), 0, 3,
				FolderDao.SORT_BY_BMRK_CREATED_ON, FolderDao.DESCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getCreatedOn();
		d2 = bmarks.get(1).getCreatedOn();
		d3 = bmarks.get(2).getCreatedOn();
		assertTrue(d1.after(d2));
		assertTrue(d2.after(d3));
	}

	public void testPageBookmarks3() throws Exception {
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = null;
		Date d1 = null;
		Date d2 = null;
		Date d3 = null;

		result = folderDao.pageBookmarks(new Folder(1), 0, 3,
				FolderDao.SORT_BY_BMRK_LAST_UPDATED, FolderDao.DESCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		d3 = bmarks.get(2).getLastUpdated();
		assertTrue(d1.after(d2));
		assertTrue(d2.after(d3));

		result = folderDao.pageBookmarks(new Folder(1), 0, 3,
				FolderDao.SORT_BY_BMRK_LAST_UPDATED, FolderDao.ASCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		d3 = bmarks.get(2).getLastUpdated();
		assertTrue(d1.before(d2));
		assertTrue(d2.before(d3));
	}

	public void testPageBookmarks4() throws Exception {
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = null;

		result = folderDao.pageBookmarks(new Folder(1), 0, 3,
				FolderDao.SORT_BY_BMRK_FLDR_LAST_UPDATED, FolderDao.DESCENDING);
		bmarks = result.getResult();
		assertEquals(303, bmarks.get(0).getId());
		assertEquals(301, bmarks.get(1).getId());
		assertEquals(300, bmarks.get(2).getId());

		result = folderDao.pageBookmarks(new Folder(1), 0, 3,
				FolderDao.SORT_BY_BMRK_FLDR_LAST_UPDATED, FolderDao.ASCENDING);
		bmarks = result.getResult();
		assertEquals(300, bmarks.get(0).getId());
		assertEquals(301, bmarks.get(1).getId());
		assertEquals(303, bmarks.get(2).getId());

	}

	public void testAddBookmarksToFolder() throws Exception {
		Date now = GregorianCalendar.getInstance().getTime();

		Folder folderX = new Folder();
		folderX.setName("projectX");
		folderX.setUser(new User(1));
		folderX.setDescription("");
		folderX.setLastUpdated(now);

		int fid = folderDao.createFolder(folderX);
		assertTrue((fid > 0));
		folderX.setId(fid);

		List<Bookmark> bmark2add = new ArrayList<Bookmark>();
		bmark2add.add(new Bookmark(300));
		bmark2add.add(new Bookmark(301));
		bmark2add.add(new Bookmark(302));

		boolean[] opOkay = folderDao.addBookmarks(folderX, bmark2add, now);
		for(boolean b: opOkay){
			assertTrue(b);
		}

		folderX = folderDao.getFolder(fid);
		assertEquals(3, folderX.getSize());

		DaoResult<Bookmark> bmarks = folderDao.pageBookmarks(folderX, 0, 1);
		assertEquals(3, bmarks.getSize());
		assertEquals(1, bmarks.getResult().size());
		assertNotNull(bmarks.getResult().get(0).getUser());
	}
	
	public void testAddBookmarksToFolders2() throws Exception{
		Date now = GregorianCalendar.getInstance().getTime();
		Folder f = new Folder("somenewfolder",new User(1),"",now);
		int id = folderDao.createFolder(f);
		assertTrue(id > 0);
		
		assertFalse(folderDao.hasFolderTag(new Folder(id),new Tag(1)));
		assertFalse(folderDao.hasFolderTag(new Folder(id),new Tag(3)));
		
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(300));
		boolean[] opOkay = folderDao.addBookmarks(new Folder(id),bmarks,now);
		for(boolean b: opOkay){
			assertTrue(b);
		}
			
		assertTrue(folderDao.hasFolderTag(new Folder(id),new Tag(1)));
		assertTrue(folderDao.hasFolderTag(new Folder(id),new Tag(3)));
		
		List<FolderTag> folderTags = folderDao.findTagsInFolder(new Folder(id),1,FolderDao.SORT_BY_ALPHA,FolderDao.ASCENDING);
		assertEquals(2,folderTags.size());
		assertEquals(1,folderTags.get(0).getCount());
		assertEquals(1,folderTags.get(1).getCount());
		
		bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(300)); // add again shouldn't affect the count
		bmarks.add(new Bookmark(301));
		opOkay = folderDao.addBookmarks(new Folder(id),bmarks,now);
		for(boolean b: opOkay){
			assertTrue(b);
		}
	
		folderTags = folderDao.findTagsInFolder(new Folder(id),1,FolderDao.SORT_BY_ALPHA,FolderDao.ASCENDING);
		assertEquals(2,folderTags.size());
		assertEquals(2,folderTags.get(0).getCount());
		assertEquals(2,folderTags.get(1).getCount());
	}

	public void testRemoveAllBookmarks() throws Exception {
		Folder f1 = folderDao.getFolder(1);
		assertEquals(3, f1.getSize());

		int cnt = folderDao.removeAllBookmarks(f1);
		assertEquals(3, cnt);

		f1 = folderDao.getFolder(1);
		assertEquals(0, f1.getSize());

		cnt = folderDao.removeAllBookmarks(f1);
		assertEquals(0, cnt);
	}
	
	public void testRemoveAllBookmarks2() throws Exception {
		int cnt = folderDao.removeAllBookmarks(new Folder(1));
		assertEquals(3, cnt);
		
		List<FolderTag> folderTags = folderDao.findTagsInFolder(new Folder(1), 0, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(0,folderTags.size());
	}
	

	public void testRemoveBookmarks() throws Exception {
		Folder f1 = folderDao.getFolder(1);
		assertEquals(3, f1.getSize());

		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(300));
		bmarks.add(new Bookmark(303));

		boolean[] opOkay = folderDao.removeBookmarks(f1, bmarks);
		for(boolean b: opOkay){
			assertTrue(b);
		}
		
		f1 = folderDao.getFolder(1);
		assertEquals(1, f1.getSize());

		DaoResult<Bookmark> result = folderDao.pageBookmarks(f1, 0, 10);
		assertEquals(1, result.getSize());
		assertEquals(1, result.getResult().size());
		assertEquals(301, result.getResult().get(0).getId());
	}
	
	public void testRemoveBookmarks2() throws Exception {
		List<FolderTag> folderTags = folderDao.findTagsInFolder(new Folder(1), 0,FolderDao.SORT_BY_ALPHA,FolderDao.ASCENDING);
		assertEquals(3,folderTags.size());
		
		Folder f1 = folderDao.getFolder(1);
		assertEquals(3, f1.getSize());

		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(300));
		bmarks.add(new Bookmark(303));
		bmarks.add(new Bookmark(300));
		bmarks.add(new Bookmark(303)); // remove multiple times should cause no error

		boolean[] opOkay = folderDao.removeBookmarks(f1, bmarks);
		for(boolean b: opOkay){
			assertTrue(b);
		}

		f1 = folderDao.getFolder(1);
		assertEquals(1, f1.getSize());

		folderTags = folderDao.findTagsInFolder(new Folder(1), 1,FolderDao.SORT_BY_ALPHA,FolderDao.ASCENDING);
		assertEquals(2,folderTags.size());
		assertEquals(1,folderTags.get(0).getCount());
		assertEquals(1,folderTags.get(1).getCount());
		
		bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(301)); 
		opOkay = folderDao.removeBookmarks(f1, bmarks);
		for(boolean b: opOkay){
			assertTrue(b);
		}
		folderTags = folderDao.findTagsInFolder(new Folder(1), 1,FolderDao.SORT_BY_ALPHA,FolderDao.ASCENDING);
		assertEquals(0,folderTags.size());
	}
	

	public void testPageContainedInFolders() throws Exception {
		DaoResult<Folder> result = folderDao.pageContainedInFolders(
				new Bookmark(303), 0, 10);
		assertEquals(1, result.getSize());
		assertEquals("my folder1", result.getResult().get(0).getName());
	}

	public void testPageBookmarksWithTag() throws Exception {
		DaoResult<Bookmark> result = folderDao.pageBookmarks(new Folder(1),
				new Tag(1), 0, 10);
		assertEquals(3, result.getSize());

		result = folderDao.pageBookmarks(new Folder(1), new Tag(4), 0, 10);
		assertEquals(1, result.getSize());
		assertEquals(303, result.getResult().get(0).getId());
	}

	public void testPageBookmarksWithTag2() throws Exception {
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = null;
		Date d1 = null;
		Date d2 = null;
		Date d3 = null;

		result = folderDao.pageBookmarks(new Folder(1), new Tag(1), 0, 3,
				FolderDao.SORT_BY_BMRK_CREATED_ON, FolderDao.ASCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getCreatedOn();
		d2 = bmarks.get(1).getCreatedOn();
		d3 = bmarks.get(2).getCreatedOn();
		assertTrue(d1.before(d2));
		assertTrue(d2.before(d3));

		result = folderDao.pageBookmarks(new Folder(1), new Tag(1), 0, 3,
				FolderDao.SORT_BY_BMRK_CREATED_ON, FolderDao.DESCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getCreatedOn();
		d2 = bmarks.get(1).getCreatedOn();
		d3 = bmarks.get(2).getCreatedOn();
		assertTrue(d1.after(d2));
		assertTrue(d2.after(d3));
	}

	public void testPageBookmarksWithTag3() throws Exception {
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = null;
		Date d1 = null;
		Date d2 = null;
		Date d3 = null;

		result = folderDao.pageBookmarks(new Folder(1), new Tag(1), 0, 3,
				FolderDao.SORT_BY_BMRK_LAST_UPDATED, FolderDao.DESCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		d3 = bmarks.get(2).getLastUpdated();
		assertTrue(d1.after(d2));
		assertTrue(d2.after(d3));

		result = folderDao.pageBookmarks(new Folder(1), new Tag(1), 0, 3,
				FolderDao.SORT_BY_BMRK_LAST_UPDATED, FolderDao.ASCENDING);
		bmarks = result.getResult();
		d1 = bmarks.get(0).getLastUpdated();
		d2 = bmarks.get(1).getLastUpdated();
		d3 = bmarks.get(2).getLastUpdated();
		assertTrue(d1.before(d2));
		assertTrue(d2.before(d3));
	}

	public void testPageBookmarksWithTag4() throws Exception {
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = null;

		result = folderDao.pageBookmarks(new Folder(1), new Tag(1), 0, 3,
				FolderDao.SORT_BY_BMRK_FLDR_LAST_UPDATED, FolderDao.DESCENDING);
		bmarks = result.getResult();
		assertEquals(303, bmarks.get(0).getId());
		assertEquals(301, bmarks.get(1).getId());
		assertEquals(300, bmarks.get(2).getId());

		result = folderDao.pageBookmarks(new Folder(1), new Tag(1), 0, 3,
				FolderDao.SORT_BY_BMRK_FLDR_LAST_UPDATED, FolderDao.ASCENDING);
		bmarks = result.getResult();
		assertEquals(300, bmarks.get(0).getId());
		assertEquals(301, bmarks.get(1).getId());
		assertEquals(303, bmarks.get(2).getId());
	}

	
	public void testHasFolderTag() throws Exception{
		assertTrue(folderDao.hasFolderTag(new Folder(1), new Tag(1)));
		assertTrue(folderDao.hasFolderTag(new Folder(1), new Tag(3)));
		assertTrue(folderDao.hasFolderTag(new Folder(1), new Tag(4)));
		
		assertFalse(folderDao.hasFolderTag(new Folder(1), new Tag(2)));
		assertFalse(folderDao.hasFolderTag(new Folder(2), new Tag(2)));
	}
	
	public void testFindTagsInFolder() throws Exception{
		List<FolderTag> fldrTgs = folderDao.findTagsInFolder(new Folder(1), 
				0, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(3,fldrTgs.size());
		assertNotNull(fldrTgs.get(0).getFolder().getUser());
		assertNotNull(fldrTgs.get(1).getFolder().getLastUpdated());
		assertEquals(3,fldrTgs.get(0).getCount());
		assertEquals(1,fldrTgs.get(1).getCount());
		assertEquals(3,fldrTgs.get(2).getCount());		
		
		assertEquals("cnn",fldrTgs.get(0).getTag().getLabel());
		assertEquals(1,fldrTgs.get(0).getTag().getCount());
		
		assertEquals("foo",fldrTgs.get(1).getTag().getLabel());
		assertEquals(1,fldrTgs.get(1).getTag().getCount());
		
		assertEquals("news",fldrTgs.get(2).getTag().getLabel());
		assertEquals(2,fldrTgs.get(2).getTag().getCount());
	}
	
	public void testFindTagsInFolder2() throws Exception{
		List<FolderTag> fldrTgs = folderDao.findTagsInFolder(new Folder(1), 
				0, FolderDao.SORT_BY_ALPHA, FolderDao.DESCENDING);
		assertEquals(3,fldrTgs.size());
		
		assertEquals(3,fldrTgs.get(0).getCount());
		assertEquals(1,fldrTgs.get(1).getCount());
		assertEquals(3,fldrTgs.get(2).getCount());		
		
		assertEquals("news",fldrTgs.get(0).getTag().getLabel());
		assertEquals(2,fldrTgs.get(0).getTag().getCount());
		
		assertEquals("foo",fldrTgs.get(1).getTag().getLabel());
		assertEquals(1,fldrTgs.get(1).getTag().getCount());
		
		assertEquals("cnn",fldrTgs.get(2).getTag().getLabel());
		assertEquals(1,fldrTgs.get(2).getTag().getCount());
	}
	
	public void testFindTagsInFolder3() throws Exception{
		List<FolderTag> fldrTgs = folderDao.findTagsInFolder(new Folder(1), 
				0, FolderDao.SORT_BY_USAGE_FREQ, FolderDao.DESCENDING);
		assertEquals(3,fldrTgs.size());
		
		assertEquals(3,fldrTgs.get(0).getCount());
		assertEquals(3,fldrTgs.get(1).getCount());
		assertEquals(1,fldrTgs.get(2).getCount());		
	}
	
	public void testFindTagsInFolder4() throws Exception{
		List<FolderTag> fldrTgs = folderDao.findTagsInFolder(new Folder(1), 
				2, FolderDao.SORT_BY_ALPHA, FolderDao.DESCENDING);
		assertEquals(2,fldrTgs.size());
		
		assertEquals(3,fldrTgs.get(0).getCount());		
		assertEquals(3,fldrTgs.get(1).getCount());		
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(
				TestFolderDBDao.class
						.getResourceAsStream("/dbunit/folderdbdao/TestFolderDBDao-input.xml"));
	}

}
