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

import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.GnizrDBTestBase;

public class TestFolderDBDao1 extends GnizrDBTestBase {

	
	private FolderDBDao folderDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		folderDao = new FolderDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(
				TestFolderDBDao1.class
						.getResourceAsStream("/dbunit/folderdbdao/TestFolderDBDao1-input.xml"));
	}
	
	public void testListTagGroups() throws Exception{
		Map<String, List<FolderTag>> tagGroups = 
			folderDao.listTagGroups(new Folder(1), 1, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);		
		assertEquals(3,tagGroups.size());
		
		List<FolderTag> g1 = tagGroups.get("g1");
		assertEquals(2,g1.size());
		assertEquals("cnn",g1.get(0).getLabel());
		assertEquals("news",g1.get(1).getLabel());		
		
		List<FolderTag> g2 = tagGroups.get("g2");
		assertEquals("cnn",g2.get(0).getLabel());
		assertEquals("news",g2.get(1).getLabel());
		
		List<FolderTag> nogrp = tagGroups.get("");
		assertEquals(1,nogrp.size());
		assertEquals("media",nogrp.get(0).getLabel());
	}
	
	public void testListTagGroups1() throws Exception{
		Map<String, List<FolderTag>> tagGroups = 
			folderDao.listTagGroups(new Folder(1), 1, FolderDao.SORT_BY_ALPHA, FolderDao.DESCENDING);		
		assertEquals(3,tagGroups.size());
		
		List<FolderTag> g1 = tagGroups.get("g1");
		assertEquals(2,g1.size());
		assertEquals("news",g1.get(0).getLabel());
		assertEquals("cnn",g1.get(1).getLabel());		
		
		List<FolderTag> g2 = tagGroups.get("g2");
		assertEquals("news",g2.get(0).getLabel());
		assertEquals("cnn",g2.get(1).getLabel());
		
		List<FolderTag> nogrp = tagGroups.get("");
		assertEquals(1,nogrp.size());
		assertEquals("media",nogrp.get(0).getLabel());
	}

	
	public void testListTagGroups3() throws Exception{
		Map<String, List<FolderTag>> tagGroups = 
			folderDao.listTagGroups(new Folder(1), 1, FolderDao.SORT_BY_USAGE_FREQ, FolderDao.DESCENDING);		
		assertEquals(3,tagGroups.size());
		
		List<FolderTag> g1 = tagGroups.get("g1");
		assertEquals(2,g1.size());
		assertEquals("news",g1.get(0).getLabel());
		assertEquals("cnn",g1.get(1).getLabel());		
		
		List<FolderTag> g2 = tagGroups.get("g2");
		assertEquals("news",g2.get(0).getLabel());
		assertEquals("cnn",g2.get(1).getLabel());
		
		List<FolderTag> nogrp = tagGroups.get("");
		assertEquals(1,nogrp.size());
		assertEquals("media",nogrp.get(0).getLabel());
	}
	
	public void testListTagGroups4() throws Exception{
		Map<String, List<FolderTag>> tagGroups = 
			folderDao.listTagGroups(new Folder(1), 0, FolderDao.SORT_BY_USAGE_FREQ, FolderDao.DESCENDING);		
		assertEquals(3,tagGroups.size());
		
		List<FolderTag> g1 = tagGroups.get("g1");
		assertEquals(3,g1.size());
		assertEquals("news",g1.get(0).getLabel());
		assertEquals("cnn",g1.get(1).getLabel());		
		assertEquals("web",g1.get(2).getLabel());
		
		List<FolderTag> g2 = tagGroups.get("g2");
		assertEquals("news",g2.get(0).getLabel());
		assertEquals("cnn",g2.get(1).getLabel());
		
		List<FolderTag> nogrp = tagGroups.get("");
		assertEquals(1,nogrp.size());
		assertEquals("media",nogrp.get(0).getLabel());
	}
}
