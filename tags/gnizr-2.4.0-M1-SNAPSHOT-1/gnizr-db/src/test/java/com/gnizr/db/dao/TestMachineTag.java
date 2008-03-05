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

import com.gnizr.db.dao.MachineTag;

import junit.framework.TestCase;

public class TestMachineTag extends TestCase {

	public void testToString() throws Exception{
		MachineTag mt = new MachineTag();
		assertEquals("",mt.toString());
		
		mt = new MachineTag("ns","predicate","value");
		assertEquals("ns:predicate=value",mt.toString());
		
		mt = new MachineTag(null,"predicate","value");
		assertEquals("gn:predicate=value",mt.toString());
		
		mt = new MachineTag(null,"predicate","\"value value\"");
		assertEquals("gn:predicate=\"value value\"",mt.toString());
		
		mt = new MachineTag("ns","predicate","\"value value\"");
		assertEquals("ns:predicate=\"value value\"",mt.toString());
	}
	
	public void testEquals() throws Exception{
		MachineTag m1 = new MachineTag(null,"pred","value");
		MachineTag m2 = new MachineTag("gn","pred","value");
		MachineTag m3 = new MachineTag(null,"ppp","value");
		
		assertTrue(m1.equals(m2));
		assertFalse(m1.equals(m3));
		assertFalse(m2.equals(m3));
	}
}
