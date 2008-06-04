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
package com.gnizr.core.user;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.user.UserDao;
import com.gnizr.db.vocab.AccountStatus;

public class TestUserManager extends GnizrCoreTestBase {

	private UserManager userManager;
	private UserDao userDao;
	protected void setUp() throws Exception {
		super.setUp();		
		userManager = new UserManager(getGnizrDao());
		userDao = getGnizrDao().getUserDao();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testIsAuthorized() throws Exception{
		assertTrue(userManager.isAuthorized(new User("hchen1","hchen1")));
		assertFalse(userManager.isAuthorized(new User("","hchen1")));
		assertFalse(userManager.isAuthorized(new User("hchen1",null)));
		assertFalse(userManager.isAuthorized(new User(null,null)));
		assertFalse(userManager.isAuthorized(new User(" "," ")));
		assertFalse(userManager.isAuthorized(new User(null,"jfdksjfsl")));
	}
	
	public void testCreateUserAccount() throws Exception{
		User harrychen = new User("harrychen", "123","Harry Chen","harry.chen@gmail.com",AccountStatus.INACTIVE,GnizrDaoUtil.getNow());
		Boolean okay = userManager.createUser(harrychen);		
		assertTrue(okay);
		boolean exceptionCaught = false;
		try{
			userManager.createUser(new User("harrychen", "123","Harry Chen","harry.chen@gmail.com"));
		}catch(Exception e){
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
		User u = userDao.findUser("harrychen").get(0);
		userDao.deleteUser(u.getId());
		assertNull(userDao.getUser(u.getId()));
	}
	
	public void testGetAccountStatus() throws Exception{
		assertEquals(AccountStatus.INACTIVE,userManager.getAccountStatus(new User("hchen1")));
		boolean ec = false;
		try{
			userManager.getAccountStatus(new User("foobar"));
		}catch(Exception e){
			ec = true;
		}
		assertTrue(ec);
	}
	
	public void testActivateUserAccount() throws Exception{
		User u = new User("hchen1","foobar");
		assertTrue(userManager.changePassword(u));
		
		assertEquals(AccountStatus.INACTIVE,userManager.getAccountStatus(new User("hchen1")));
		assertTrue(userManager.activateUserAccount(new User("hchen1")));
		assertTrue(userManager.activateUserAccount(new User("hchen1")));		
		assertEquals(AccountStatus.ACTIVE,userManager.getAccountStatus(new User("hchen1")));
		
		boolean ec = false;
		try{
			assertTrue(userManager.activateUserAccount(new User("fdsfs")));
		}catch(Exception e){
			ec = true;
		}
		assertTrue(ec);
		
		u = userManager.getUser("hchen1","foobar");
	}
	
	public void testInactivateUserAccount() throws Exception{
		User u = new User("hchen1","foobar");
		assertTrue(userManager.changePassword(u));
		
		assertEquals(AccountStatus.INACTIVE,userManager.getAccountStatus(new User("hchen1")));
		assertTrue(userManager.activateUserAccount(new User("hchen1")));	
		assertEquals(AccountStatus.ACTIVE,userManager.getAccountStatus(new User("hchen1")));
		assertTrue(userManager.inactivateUserAccount(new User("hchen1")));		
		assertEquals(AccountStatus.INACTIVE,userManager.getAccountStatus(new User("hchen1")));	
	}
	
	public void testChangePassword() throws Exception{
		User u = userManager.getUser("hchen1");
		u.setPassword("foobar");
		assertTrue(userManager.changePassword(u));
		assertTrue(userManager.isAuthorized(u));		
	}
	
	public void testChangeProfile() throws Exception{
		User u = userManager.getUser("hchen1");
		u.setFullname("hc");
		u.setEmail("hc@email.com");
		assertTrue(userManager.changeProfile(u));
		
		u = userManager.getUser("hchen1");
		assertEquals("hc",u.getFullname());
		assertEquals("hc@email.com",u.getEmail());
	}
	
	public void testDeleteUser1() throws Exception{
		boolean isOkay = userManager.deleteUser(new User("hchen1"));
		assertEquals(true,isOkay);
	}
	
	public void testDeleteUser2() throws Exception{
		boolean isOkay = userManager.deleteUser(new User(1));
		assertEquals(true,isOkay);
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserManager.class.getResourceAsStream("/TestUserManager-input.xml"));
	}

}
