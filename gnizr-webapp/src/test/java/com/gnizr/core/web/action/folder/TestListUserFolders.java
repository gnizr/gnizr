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

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Folder;
import com.opensymphony.xwork.ActionSupport;

public class TestListUserFolders extends GnizrWebappTestBase {

	private FolderManager folderManager;
	private ListUserFolders action;
	
	protected void setUp() throws Exception {
		super.setUp();
		folderManager = new FolderManager(getGnizrDao());
		action = new ListUserFolders();
		action.setFolderManager(folderManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestListUserFolders.class.getResourceAsStream("/TestListUserFolders-input.xml"));
	}
	
	public void testDoListBookmarkFolders() throws Exception{
		action.setUsername("hchen1");
		action.setUrl("http://www.gamefaqs.com/console/wii/game/928519.html");
		String code = action.doListBookmarkFolders();
		assertEquals(ActionSupport.SUCCESS,code);
		List<Folder> folders = action.getFolders();
		assertEquals(3,folders.size());
		assertEquals(3,action.getNumberOfFolders());
		
		action.setCount(1);
		code = action.doListBookmarkFolders();
		assertEquals(ActionSupport.SUCCESS,code);
		folders = action.getFolders();
		assertEquals(1,folders.size());
		assertEquals(3,action.getNumberOfFolders());
		
		action.setUsername("fjdskljfdas");
		code = action.doListBookmarkFolders();
		folders = action.getFolders();
		assertEquals(ActionSupport.SUCCESS,code);
		assertEquals(0,folders.size());
		assertEquals(0,action.getNumberOfFolders());
	}
	
	

}
