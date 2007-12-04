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
package com.gnizr.core.web.action.timeline;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.managers.UserManager;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestUserBookmarkTimeline extends DatabaseTestCase {

	private UserManager userManager;
	private UserBookmarkTimeline action;
	private BasicDataSource dataSource;
	private GnizrDao gnizrDao;
	
	public TestUserBookmarkTimeline(){
		dataSource = new BasicDataSource();
		dataSource.setUsername("gnizr");
		dataSource.setPassword("gnizr");
		dataSource.setUrl("jdbc:mysql://localhost/gnizr_test");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		
		gnizrDao = GnizrDao.getInstance(dataSource);
		
		userManager = new UserManager(gnizrDao);	
	}
	
	protected void setUp() throws Exception {
		super.setUp();

		action = new UserBookmarkTimeline();
		action.setUserManager(userManager);	
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserBookmarkTimeline.class.getResourceAsStream("/TestUserBookmarkTimeline-input.xml"));
	}

	public void testExecute() throws Exception{		
		action.setUsername("hchen1");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		
		action.setUser(null);
		action.setLoggedInUser(new User("hchen1"));
		assertEquals(ActionSupport.SUCCESS,action.execute());
		
		action.setUser(null);
		action.setLoggedInUser(new User(1));
		assertEquals(ActionSupport.SUCCESS,action.execute());
	}

	@Override
	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(dataSource.getConnection());
	}
	
}
