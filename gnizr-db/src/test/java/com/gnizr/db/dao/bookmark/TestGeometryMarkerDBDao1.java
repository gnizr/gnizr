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
package com.gnizr.db.dao.bookmark;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class TestGeometryMarkerDBDao1 extends GnizrDBTestBase {

	private GeometryMarkerDao geomMarkerDao;	
	private BookmarkDao bookmarkDao;
	private GeometryFactory factory;
	private PointMarker m1 = new PointMarker();
	private PointMarker m2 = new PointMarker();
	
	protected void setUp() throws Exception {
		super.setUp();
		geomMarkerDao = new GeometryMarkerDBDao(getDataSource());
		bookmarkDao = new BookmarkDBDao(getDataSource());
		factory = new GeometryFactory();
		createDummyPointMarker();	
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		deleteDummyPointMarker();
	}
	
	private void createDummyPointMarker() throws Exception{
		// create some dummy data
		m1.setMarkerIconId(0);
		m1.setNotes("marker1");
		m1.setPoint(factory.createPoint(new Coordinate(10,10)));
		int id = geomMarkerDao.createPointMarker(m1);
		m1.setId(id);
		
		m2.setMarkerIconId(0);
		m2.setNotes("marker2");
		m2.setPoint(factory.createPoint(new Coordinate(20,20)));
		id = geomMarkerDao.createPointMarker(m2);
		m2.setId(id);	
	}

	private void deleteDummyPointMarker() throws Exception{
		geomMarkerDao.deletePointMarker(m1.getId());
		geomMarkerDao.deletePointMarker(m2.getId());
	}
	
	public void testCreatePointMarker() throws Exception{
		assertTrue(m1.getId() > 0);
		assertTrue(m2.getId() > 0);
	}
	
	public void testDeletePointMarker() throws Exception{
		assertTrue(geomMarkerDao.deletePointMarker(m1.getId()));
		assertTrue(geomMarkerDao.deletePointMarker(m2.getId()));
	}
	
	public void testGetPointMarker() throws Exception{
		PointMarker pm = geomMarkerDao.getPointMarker(m1.getId());
		assertNotNull(pm);
		assertEquals(m1.getId(),pm.getId());
		assertEquals(m1.getMarkerIconId(),pm.getMarkerIconId());
		assertEquals(m1.getNotes(),pm.getNotes());
		assertTrue(m1.getPoint().equals(pm.getPoint()));
	}	
	
	public void testUpdatePointMarker() throws Exception{
		PointMarker pm = geomMarkerDao.getPointMarker(m1.getId());
		pm.setMarkerIconId(10);
		pm.setNotes("newnotes");
		Point p3 = factory.createPoint(new Coordinate(30,30));
		pm.setPoint(p3);
		assertTrue(geomMarkerDao.updatePointMarker(pm));
		pm = geomMarkerDao.getPointMarker(m1.getId());
		assertEquals(m1.getId(),pm.getId());
		assertEquals(10,pm.getMarkerIconId());
		assertEquals("newnotes",pm.getNotes());
	}
	
	public void testAddRmvPointMarker() throws Exception{
		Bookmark bm = new Bookmark(299);
		assertTrue(geomMarkerDao.addPointMarker(bm,m1));
		assertTrue(geomMarkerDao.addPointMarker(bm,m1));
		assertTrue(geomMarkerDao.addPointMarker(bm,m2));
		
		assertTrue(geomMarkerDao.removePointMarker(bm,m1));
		assertTrue(geomMarkerDao.removePointMarker(bm,m1));
		assertTrue(geomMarkerDao.removePointMarker(bm,m2));
	}
	
	public void testListPointMarkers() throws Exception{
		Bookmark bm = new Bookmark(299);
		assertTrue(geomMarkerDao.addPointMarker(bm,m1));
		assertTrue(geomMarkerDao.addPointMarker(bm,m2));
		
		List<PointMarker> ptMrkrs = geomMarkerDao.listPointMarkers(bm);
		assertEquals(2,ptMrkrs.size());		
		
		assertTrue(geomMarkerDao.removePointMarker(bm,m1));
		assertTrue(geomMarkerDao.removePointMarker(bm,m2));
		
		ptMrkrs = geomMarkerDao.listPointMarkers(bm);
		assertEquals(0,ptMrkrs.size());	
	}
	
	
	public void testDeleteBookmark() throws Exception{
		Bookmark bm = new Bookmark(299);
		assertTrue(geomMarkerDao.addPointMarker(bm,m1));
		assertTrue(geomMarkerDao.addPointMarker(bm,m1));
		assertTrue(geomMarkerDao.addPointMarker(bm,m2));
		
		assertNotNull(bookmarkDao.getBookmark(299));		
		assertTrue(bookmarkDao.deleteBookmark(299));
		assertNull(bookmarkDao.getBookmark(299));
		assertNull(geomMarkerDao.getPointMarker(m1.getId()));
		assertNull(geomMarkerDao.getPointMarker(m2.getId()));			
	}
	
	public void testPageBookmarksInArchive() throws Exception{
		Bookmark bm299 = new Bookmark(299);
		Bookmark bm300 = new Bookmark(300);
		assertTrue(geomMarkerDao.addPointMarker(bm299,m1));
		assertTrue(geomMarkerDao.addPointMarker(bm300,m2));
		
		DaoResult<Bookmark> result = geomMarkerDao.pageBookmarksInArchive(new User(1), 0, 10);
		assertEquals(2,result.getSize());
		Bookmark bm = result.getResult().get(0);
		assertNotNull(bm.getUser().getUsername());
		assertNotNull(bm.getLink().getUrl());
	}
	
	public void testPageBookmarksInFolder() throws Exception{
		Bookmark bm299 = new Bookmark(299);
		Bookmark bm300 = new Bookmark(300);
		assertTrue(geomMarkerDao.addPointMarker(bm299,m1));
		assertTrue(geomMarkerDao.addPointMarker(bm300,m2));
		
		DaoResult<Bookmark> result = geomMarkerDao.pageBookmarksInFolder(new Folder(1),0,0);
		assertEquals(1,result.getSize());
		assertEquals(0,result.getResult().size());
		
		result = geomMarkerDao.pageBookmarksInFolder(new Folder(1), 0, 10);
		assertEquals(1,result.getSize());
		assertEquals(1,result.getResult().size());
		
		Bookmark bm = result.getResult().get(0);
		assertEquals(300, bm.getId());
		assertNotNull(bm.getUser().getUsername());
		assertNotNull(bm.getLink().getUrl());
	}
	
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestGeometryMarkerDBDao1.class.getResourceAsStream("/dbunit/bmarkdbdao/TestGeometryMarkerDBDao1-input.xml"));
	}

	
}
