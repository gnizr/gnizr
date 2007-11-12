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
package com.gnizr.core.web.action.tag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.UserTag;
import com.opensymphony.xwork.ActionSupport;

public class TestGetUserTagGroups extends GnizrWebappTestBase {

	private GetUserTagGroups action;
	private UserManager userManager;
	private Map<Object,Object> session;
	protected void setUp() throws Exception {
		super.setUp();
		session = new HashMap<Object, Object>();
		userManager = new UserManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestGetUserTagGroups.class.getResourceAsStream("/TestGetUserTagGroups-input.xml"));
	}

	public void testDoGetAll() throws Exception{		
		action = new GetUserTagGroups();
		action.setSession(session);
		action.setUserManager(userManager);
		action.setUsername("hchen1");
		String code = action.doGetAll();
		assertEquals(ActionSupport.SUCCESS,code);
		
		Map<String, List<UserTag>> grps = action.getUserTagGroups();
		assertEquals(3,grps.size());
		
		List<UserTag> g1 = grps.get("g1");
		List<UserTag> g2 = grps.get("g2");
		List<UserTag> nogroup = grps.get("");
		assertEquals(4,g1.size());
		assertEquals(2,g2.size());
		assertEquals(2,nogroup.size());
	}
}
