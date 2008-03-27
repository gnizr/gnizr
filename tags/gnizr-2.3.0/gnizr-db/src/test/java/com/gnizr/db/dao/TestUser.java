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
package com.gnizr.db.dao;

import java.util.Date;
import java.util.GregorianCalendar;

import com.gnizr.db.vocab.AccountStatus;

import junit.framework.TestCase;

public class TestUser extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCopyConstructor() throws Exception{
		User u = new User();
		u.setAccountStatus(AccountStatus.ACTIVE);
		u.setCreatedOn(GregorianCalendar.getInstance().getTime());
		u.setEmail("harry.chen@gmail.com");
		u.setFullname("Harry");
		u.setId(1012);
		u.setPassword("fdsfa");
		u.setUsername("hchen1");
		
		User u2 = new User(u);
		assertEquals(u.getId(),u2.getId());
		assertEquals(u.getAccountStatus(),u2.getAccountStatus());
		assertEquals(u.getCreatedOn(),u2.getCreatedOn());
		assertEquals(u.getFullname(),u2.getFullname());
		assertEquals(u.getEmail(),u2.getEmail());
		assertEquals(u.getPassword(),u2.getPassword());
		assertEquals(u.getUsername(),u2.getUsername());
	}

	public void testEquals() throws Exception{
		Date d = GregorianCalendar.getInstance().getTime();
		User u1 = new User("hchen1","hchen1","harry chen","harry.chen@gmail.com",new Integer(1),d);
		User u2 = new User("hchen1","hchen1","harry chen","harry.chen@gmail.com",new Integer(1),d);
		assertEquals(u1,u1);
		assertEquals(u1,u2);
		assertEquals(u2,u1);
	}
	
	public void testHashCode() throws Exception{
		Date d = GregorianCalendar.getInstance().getTime();
		User u1 = new User("hchen1","hchen1","harry chen","harry.chen@gmail.com",new Integer(1),d);
		User u2 = new User("hchen1","hchen1","harry chen","harry.chen@gmail.com",new Integer(1),d);
		assertEquals(u1.hashCode(),u2.hashCode());
	}
}
