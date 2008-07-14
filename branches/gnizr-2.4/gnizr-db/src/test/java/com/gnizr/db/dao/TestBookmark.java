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

import java.util.GregorianCalendar;
import java.util.List;


import junit.framework.TestCase;

public class TestBookmark extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCopyConstructor() throws Exception{
		Bookmark b = new Bookmark();
		b.setCreatedOn(GregorianCalendar.getInstance().getTime());
		b.setId((20933));
		b.setLastUpdated(GregorianCalendar.getInstance().getTime());
		b.setLink(new Link("http://abc.com"));
		b.setNotes(null);
		b.setTags("tag fjkdsl fdjsk fdjsk");
		b.setTitle("jfdksljfldskj jflkdsj f");
		b.setUser(new User("fjdksl","jfdlksjfs"));
		b.setFolders("f1,f2");
		Bookmark b2 = new Bookmark(b);
		assertEquals(b.getId(),b2.getId());
		assertEquals(b.getCreatedOn(), b2.getCreatedOn());
		assertEquals(b.getLastUpdated(),b2.getLastUpdated());		
		assertEquals(b.getNotes(),b2.getNotes());
		assertEquals(b.getTags(),b2.getTags());
		assertEquals(b.getTitle(),b2.getTitle());
		assertEquals(b.getFolders(),b2.getFolders());
		assertEquals(b.getUser().getUsername(),b2.getUser().getUsername());			
		
		assertEquals(b,b2);
	}
	
	public void testGetFilteredMachineTags() throws Exception{
		Bookmark b = new Bookmark();
		b.setTags("gn:geonames=united_states geonames:293 ks:geonames=232 subscribe:this aaaa gn");
		List<MachineTag> result = b.getMachineTagList();
		assertEquals(4,result.size());
		
		result = b.getMachineTagList(null,null);
		assertEquals(4,result.size());
		
		result = b.getMachineTagList("gn", null);
		assertEquals(3,result.size());
		
		result = b.getMachineTagList("gn","geonames");
		assertEquals(2,result.size());
		
		result = b.getMachineTagList(null, "geonames");
		assertEquals(3,result.size());
		
		result = b.getMachineTagList("gn", "subscribe");
		assertEquals(1,result.size());
	}
}
