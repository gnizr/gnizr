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
package com.gnizr.core.bookmark;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;

public class TestGeonamesTagListener extends GnizrCoreTestBase {

	private GeonamesTagListener listener;
	private BookmarkManager bookmarkManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		bookmarkManager = new BookmarkManager(getGnizrDao());
		listener = new GeonamesTagListener();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestGeonamesTagListener.class.getResourceAsStream("/TestGeonamesTagListener-input.xml"));
	}
	
	public void testNotifyAdded() throws Exception {
		Bookmark bm = new Bookmark();
		bm.setLink(new Link(202));
		bm.setUser(new User(2));
		bm.setTags("gn:geonames=united_states geonames:US CNNNews");
		
		int id = bookmarkManager.addBookmark(bm);
		assertTrue(id > 0);
		bm.setId(id);
		
		List<PointMarker> pointMarkers= bookmarkManager.getPointMarkers(bm);
		assertEquals(0,pointMarkers.size());
		
		listener.notifyAdded(bookmarkManager,bm);
		
		pointMarkers = bookmarkManager.getPointMarkers(bm);
		assertEquals(2,pointMarkers.size());
		
		assertTrue(bookmarkManager.deleteBookmark(bm));
	}
	
	public void testNotifyUpdated() throws Exception {
		Bookmark oldBm = new Bookmark();
		oldBm.setLink(new Link(202));
		oldBm.setUser(new User(2));
		oldBm.setTags("CNNNews");
		
		int id = bookmarkManager.addBookmark(oldBm);
		assertTrue(id > 0);
		oldBm = bookmarkManager.getBookmark(id);
		
		List<PointMarker> pointMarkers= bookmarkManager.getPointMarkers(oldBm);
		assertEquals(0,pointMarkers.size());
		
		Bookmark newBm = new Bookmark(oldBm);
		newBm.setTags("geonames:united_states");
		assertTrue(bookmarkManager.updateBookmark(newBm));
		
		listener.notifyUpdated(bookmarkManager,oldBm, newBm);
		
		pointMarkers = bookmarkManager.getPointMarkers(oldBm);
		assertEquals(1,pointMarkers.size());
		
		PointMarker pm = pointMarkers.get(0);
		assertEquals("united states", pm.getNotes());
		
		assertTrue(bookmarkManager.deleteBookmark(oldBm));
	}
	

}
