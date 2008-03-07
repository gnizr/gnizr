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
package com.gnizr.db.dao.user;

import java.util.Calendar;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.User;

public class TestUserDBDao1 extends GnizrDBTestBase {
	
	public TestUserDBDao1(String name){
		super(name);
	}
	
	public void testDeleteUser() throws Exception{
		UserDBDao dao = new UserDBDao(getDataSource());
		User hchen1 = dao.getUser(1);
		assertNotNull(hchen1);
		
		assertTrue(dao.deleteUser(hchen1.getId()));
		hchen1 = dao.getUser(1);
		assertNull(hchen1);
	}
	
	public void testCreateUser() throws Exception{
		UserDBDao dao = new UserDBDao(getDataSource());
		User hchen1 = dao.getUser(1);
		int id = dao.createUser(hchen1);
		assertEquals(0,id);
		id = dao.createUser(hchen1);
		assertEquals(0,id);
		id = dao.createUser(hchen1);
		assertEquals(0,id);
		
		User hchen4 = new User();
		hchen4.setUsername("hchen4");
		hchen4.setPassword("hchen4password");
		hchen4.setFullname("Harry Chen");
		hchen4.setEmail("hchen4@umbc.edu");
		hchen4.setAccountStatus(0);
		hchen4.setCreatedOn(Calendar.getInstance().getTime());
		
		id = dao.createUser(hchen4);
		User hchen4Copy = dao.getUser(id);
		assertEquals(hchen4.getUsername(), hchen4Copy.getUsername());
		assertEquals(hchen4.getEmail(), hchen4Copy.getEmail());
		assertEquals(hchen4.getFullname(),hchen4Copy.getFullname());
		assertEquals(hchen4.getAccountStatus(),hchen4Copy.getAccountStatus());
		assertTrue(dao.deleteUser(hchen4Copy.getId()));
		hchen4 = dao.getUser(hchen4Copy.getId());
		assertNull(hchen4);
	}
	
	
	public void testGetUser() throws Exception{
		UserDBDao doa = new UserDBDao(getDataSource());
		User user = doa.getUser(1);
		assertNotNull(user);
		assertEquals("hchen1",user.getUsername());
		assertEquals("Harry Chen",user.getFullname());
		assertEquals("harry.chen@gmail.com",user.getEmail());
		assertEquals(new Integer(1),user.getAccountStatus());
		assertNotNull(user.getPassword());
	}
	
	public void testFindUserUsername() throws Exception{
		UserDBDao doa = new UserDBDao(getDataSource());
		List<User> users = doa.findUser("hchen1");
		assertNotNull(users);
		assertEquals(1,users.size());
		User user = users.get(0);
		assertEquals("hchen1",user.getUsername());
		assertEquals("Harry Chen",user.getFullname());
		assertEquals("harry.chen@gmail.com",user.getEmail());
		assertEquals(new Integer(1),user.getAccountStatus());
	}
	
	
	public void testFindUserUnamePwd() throws Exception{
		UserDBDao dao = new UserDBDao(getDataSource());
		List<User> users = dao.findUser("hchen1","hchen1");
		assertNotNull(users);
		assertEquals(1,users.size());
		
		users = dao.findUser("foo",null);
		assertNotNull(users);
		assertEquals(0,users.size());
			
		users = dao.findUser("hchen1",null);
		assertNotNull(users);
		assertEquals(0,users.size());
		
		users = dao.findUser(null,null);
		assertNotNull(users);
		assertEquals(0,users.size());
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserDBDao1.class.getResourceAsStream("/dbunit/userdbdao/TestUserDBDao1-input.xml"));
	}
	
	public void testUpdateUser() throws Exception{
		UserDBDao dao = new UserDBDao(getDataSource());		
		User u = dao.getUser(1);
		
		String oldPasswordMd5 = u.getPassword();
		
		u.setPassword("foobar");
		dao.updateUser(u);
		
		u = dao.getUser(1);
		String newPasswordMd5 = u.getPassword();
		assertFalse((newPasswordMd5.equals(oldPasswordMd5)));
		oldPasswordMd5 = newPasswordMd5;
		
		// change user's fullname
		u.setFullname("hc");
		// set password to NUL, indicating no password change is required
		u.setPassword(null);
		dao.updateUser(u);
		
		u = dao.getUser(1);
		newPasswordMd5 = u.getPassword();
		assertEquals("hc",u.getFullname());		
		assertEquals(newPasswordMd5,oldPasswordMd5);
	}

	
	public void testListUsers() throws Exception{
		UserDBDao dao = new UserDBDao(getDataSource());
		
		List<User> users = dao.listUsers();
		assertEquals(2, users.size());
	}
}
