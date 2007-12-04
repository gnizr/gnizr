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
package com.gnizr.core.managers;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class TestUserManager2 extends GnizrCoreTestBase {

	private UserManager userManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserManager2.class.getResourceAsStream("/TestUserManager2-input.xml"));
	}

	
	public void testGetTagSortByAlpha() throws Exception{
		List<UserTag> userTags = userManager.getTagsSortByAlpha(new User(1), 1);
		assertEquals(3,userTags.size());
		assertEquals("foobar",userTags.get(0).getLabel());
		assertEquals("news",userTags.get(1).getLabel());
		assertEquals("ppp",userTags.get(2).getLabel());
	}
	
	public void testGetTagSortByFreq() throws Exception{
		List<UserTag> userTags = userManager.getTagsSortByFreq(new User(1), 1);
		assertEquals(3,userTags.size());
		assertEquals("ppp",userTags.get(0).getLabel());
		assertEquals("foobar",userTags.get(1).getLabel());
		assertEquals("news",userTags.get(2).getLabel());	
	}
}
