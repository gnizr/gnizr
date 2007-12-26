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
package com.gnizr.db.dao.tag;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.TagProperty;

public class TestTagPropertyDBDao extends GnizrDBTestBase {

	private TagPropertyDBDao dao;
	
	protected void setUp() throws Exception {
		super.setUp();
		dao = new TagPropertyDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagPropertyDBDao.class.getResourceAsStream("/dbunit/tagprptdbdao/TestTagPrptDBDao-input.xml"));
	}

	public void testGetTagProperty() throws Exception{
		TagProperty prpt = dao.getTagProperty(1);
		assertNotNull(prpt);
		assertEquals("related",prpt.getName());
		assertEquals("skos",prpt.getNamespacePrefix());
		assertEquals("default",prpt.getPropertyType());
		assertEquals(TagProperty.CARDINALITY_ONE_OR_MORE,prpt.getCardinality());
		assertNull(dao.getTagProperty(4000));
		assertNull(dao.getTagProperty(0));
		assertNull(dao.getTagProperty(-1032));
	}
	
	public void testDeleteTagProperty() throws Exception{
		TagProperty prpt = dao.getTagProperty(2);
		assertNotNull(prpt);
		assertTrue(dao.deleteTagProperty(2));
		assertNull(dao.getTagProperty(2));
	}
	
	public void testCreateTagProperty() throws Exception{
		TagProperty prpt = new TagProperty();
		prpt.setName("foobar");
		prpt.setNamespacePrefix("xx");	
		prpt.setPropertyType(TagProperty.TYPE_DEFAULT);

		int id = dao.createTagProperty(prpt);
		assertNotNull(id);
		assertTrue((id >0));
		TagProperty prptCopy = dao.getTagProperty(id);
		assertEquals(prpt.getName(),prptCopy.getName());
		assertEquals(prpt.getNamespacePrefix(),prptCopy.getNamespacePrefix());
		assertEquals(TagProperty.CARDINALITY_ONE_OR_MORE,prptCopy.getCardinality());
		assertEquals(TagProperty.TYPE_DEFAULT,prptCopy.getPropertyType());	
	}
	
	public void testUpdateTagProperty() throws Exception{
		TagProperty prpt = new TagProperty();
		prpt.setName("foobar");
		prpt.setNamespacePrefix("xx");	
		
		int id = dao.createTagProperty(prpt);
		prpt = dao.getTagProperty(id);
		assertEquals("foobar",prpt.getName());
		assertEquals(TagProperty.TYPE_DEFAULT,prpt.getPropertyType());
		
		prpt.setPropertyType(TagProperty.TYPE_SPATIAL);
		assertTrue(dao.updateTagProperty(prpt));
		
		prpt = dao.getTagProperty(id);
		assertEquals(TagProperty.TYPE_SPATIAL,prpt.getPropertyType());
	}
	
	public void testFindAllTagProperty() throws Exception{
		List<TagProperty> prpt = dao.findTagProperty();
		assertEquals(4,prpt.size());
	}
	
	public void testGetTagPropertyNSName() throws Exception{
		TagProperty tagPrpt = dao.getTagProperty("skos","related");
		assertNotNull(tagPrpt);
		assertEquals(1,tagPrpt.getId());
		
		tagPrpt = dao.getTagProperty("gn", "resident");
		assertNotNull(tagPrpt);
		assertEquals(4,tagPrpt.getId());
	}
	
	public void testFindTagPropertyOfType() throws Exception{
		List<TagProperty> prpt = dao.findTagProperty(TagProperty.TYPE_DEFAULT);
		assertEquals(3,prpt.size());
		
		prpt = dao.findTagProperty(TagProperty.TYPE_SPATIAL);
		assertEquals(1,prpt.size());
	}
	
}
