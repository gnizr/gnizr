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

import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.action.SessionConstants;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.UserTag;
import com.opensymphony.xwork.ActionSupport;

public class TestGetUserTagCloud extends GnizrWebappTestBase {

	private GetUserTagCloud action;
	private UserManager userManager;
	private Map<Object, Object> session;
	
	protected void setUp() throws Exception {
		super.setUp();
		session = new HashMap<Object,Object>();
		userManager = new UserManager(getGnizrDao());
		action = new GetUserTagCloud();
		action.setUserManager(userManager);
		action.setSession(session);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetTagCloud1() throws Exception{
		action.setUsername("gnizr");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		List<UserTag> userTags = action.getUserTags();
		assertEquals(3,userTags.size());
	}
	
	public void testGetTagCloud2() throws Exception{
		session.put(SessionConstants.MIN_TAG_FREQ, 2);
		action.setUsername("gnizr");		
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		List<UserTag> userTags = action.getUserTags();
		assertEquals(2,userTags.size());
	}
	
	public void testGetTagCloud3() throws Exception{
		session.put(SessionConstants.TAG_SORT_BY,SessionConstants.SORT_FREQ);
		action.setUsername("gnizr");		
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		List<UserTag> userTags = action.getUserTags();
		assertEquals(3,userTags.size());
		assertEquals("webwork",userTags.get(0).getLabel());
		assertEquals("g1",userTags.get(1).getLabel());
		assertEquals("wii",userTags.get(2).getLabel());
	}
	
	public void testGetTagCloud4() throws Exception{
		action.setUsername("gnizr");
		session.put(SessionConstants.TAG_SORT_BY,SessionConstants.SORT_ALPH);
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		List<UserTag> userTags = action.getUserTags();
		assertEquals(3,userTags.size());
		assertEquals("g1",userTags.get(0).getLabel());
		assertEquals("webwork",userTags.get(1).getLabel());
		assertEquals("wii",userTags.get(2).getLabel());
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestGetUserTagCloud.class.getResourceAsStream("/TestGetUserTagCloud-input.xml"));
	}

}
