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
package com.gnizr.web.action.tag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.SessionConstants;
import com.opensymphony.xwork.ActionSupport;

public class TestGetFolderTagCloud extends GnizrWebappTestBase {

	private GetFolderTagCloud action;
	private FolderManager folderManager;
	private UserManager userManager;
	private Map session;
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
		folderManager = new FolderManager(getGnizrDao());
		session = new HashMap<Object,Object>();
		action = new GetFolderTagCloud();
		action.setSession(session);
		action.setFolderManager(folderManager);
		action.setUserManager(userManager);
		
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(new Bookmark(302));
		bmarks.add(new Bookmark(304));
		bmarks.add(new Bookmark(306));
		folderManager.addBookmarks(new User(2), "my folder1",bmarks);		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetFolderTagCloud() throws Exception{
		action.setFolderName("my folder1");
		action.setUsername("hchen1");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		List<FolderTag> ftags = action.getFolderTags();
		assertEquals(3,ftags.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testGetFolderTagCloud2() throws Exception{
		session.put(SessionConstants.TAG_SORT_BY,SessionConstants.SORT_ALPH);
		session.put(SessionConstants.MIN_TAG_FREQ,2);
		
		action.setFolderName("my folder1");
		action.setUsername("hchen1");	
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		List<FolderTag> ftags = action.getFolderTags();
		assertEquals(1,ftags.size());
		assertEquals("webwork",ftags.get(0).getLabel());
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestGetFolderTagCloud.class.getResourceAsStream("/TestGetFolderTagCloud-input.xml"));
	}

}
