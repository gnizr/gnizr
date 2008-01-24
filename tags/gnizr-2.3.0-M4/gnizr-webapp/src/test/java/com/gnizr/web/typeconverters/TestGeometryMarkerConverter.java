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
package com.gnizr.web.typeconverters;

import junit.framework.TestCase;

import com.gnizr.db.dao.PointMarker;
import com.gnizr.web.typeconverters.GeometryMarkerConverter;

public class TestGeometryMarkerConverter extends TestCase {

	private GeometryMarkerConverter converter;
	
	protected void setUp() throws Exception {
		super.setUp();
		converter = new GeometryMarkerConverter();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testFromJSON2Bean() throws Exception{
		String json = "{'id':13,'notes':'foobar foo','point':'10,-20.00','iconId':5}";
		PointMarker pm = (PointMarker)converter.convertFromString(null,new String[]{json},PointMarker.class);
		assertEquals(13,pm.getId());
		assertEquals("foobar foo", pm.getNotes());
		assertEquals(-20.00,pm.getPoint().getY());
		assertEquals(10.0,pm.getPoint().getX());
		assertEquals(5,pm.getMarkerIconId());
	}
	
	public void testFromBean2JSON() throws Exception{
		PointMarker pm = new PointMarker();
		pm.setId(33);
		pm.setMarkerIconId(2);
		pm.setNotes("abc");
		pm.setPoint(33.00, -55.00);
		String s = (String)converter.convertToString(null, pm);
		assertNotNull(s);
	}

}
