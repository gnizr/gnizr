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

import junit.framework.TestCase;

public class TestFolder extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testConstructor() throws Exception{
		Folder f1 = new Folder();
		assertNull(f1.getName());
		assertNull(f1.getDescription());
		assertNull(f1.getUser());
		assertEquals(-1, f1.getId());
	}
	
	public void testConstructor2() throws Exception{
		Folder f1 = new Folder(10);
		assertNull(f1.getName());
		assertNull(f1.getDescription());
		assertNull(f1.getUser());		
		assertEquals(10, f1.getId());
	}
	
	public void testConstructor3() throws Exception{
		Date d = GregorianCalendar.getInstance().getTime();
		Folder f1 = new Folder(10,"folder1",new User(1),"stuff",d,0);
		assertEquals(10,f1.getId());
		assertEquals("folder1",f1.getName());
		assertEquals(1,f1.getUser().getId());
		assertEquals("stuff",f1.getDescription());
		assertEquals(d,f1.getLastUpdated());
	}
	
	public void testConstructor4() throws Exception{
		Folder f1 = new Folder(10,"folder1",new User(1),"stuff",null,0);
		Folder f2 = new Folder(f1);
		
		assertEquals(10,f2.getId());
		assertEquals("folder1",f2.getName());
		assertEquals(1,f2.getUser().getId());
		assertEquals("stuff",f2.getDescription());		
	}

}
