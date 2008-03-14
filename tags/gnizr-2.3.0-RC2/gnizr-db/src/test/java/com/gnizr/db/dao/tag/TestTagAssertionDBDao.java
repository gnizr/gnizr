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
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.TagProperty;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;


public class TestTagAssertionDBDao extends GnizrDBTestBase {

	private TagAssertionDBDao dao;
	
	protected void setUp() throws Exception {
		super.setUp();
		dao = new TagAssertionDBDao(getDataSource());
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagAssertionDBDao.class.getResourceAsStream("/dbunit/tagassertiondbdao/TestTagAssertionDBDao-input.xml"));
	}

	public void testGetTagAssertion() throws Exception{
		TagAssertion asrt = dao.getTagAssertion((1));
		assertNotNull(asrt);
		assertEquals((100),asrt.getSubject().getId());
		assertEquals((1),asrt.getProperty().getId());
		assertEquals((101),asrt.getObject().getId());
		assertEquals((1),asrt.getUser().getId());
	}
	
	public void testDeleteTagAssertion() throws Exception{
		TagAssertion asrt = dao.getTagAssertion((1));
		assertNotNull(asrt);
		assertTrue(dao.deleteTagAssertion(asrt.getId()));
		asrt = dao.getTagAssertion((1));
		assertNull(asrt);
		assertNull(dao.getTagAssertion((1000000)));
	}
	
	public void testCreateTagAssertion() throws Exception{
		TagAssertion asrt = new TagAssertion();
		asrt.setSubject(new UserTag((104)));
		asrt.setProperty(new TagProperty((1)));
		asrt.setObject(new UserTag((103)));
		asrt.setUser(new User((1)));
		int id = dao.createTagAssertion(asrt);
		assertNotNull(id);
		assertTrue((id >0));
		
		int id2 = dao.createTagAssertion(asrt);
		assertNotNull(id);
		assertTrue((id >0));
		assertEquals(id,id2);
		
		TagAssertion asrtCopy = dao.getTagAssertion(id);
		assertEquals(asrt.getSubject().getId(),asrtCopy.getSubject().getId());
		assertEquals(asrt.getProperty().getId(),asrtCopy.getProperty().getId());
		assertEquals(asrt.getObject().getId(),asrtCopy.getObject().getId());
		assertEquals(asrt.getUser().getId(),asrtCopy.getUser().getId());
		assertTrue(dao.deleteTagAssertion(id));
	}
	
	public void testUpdateTagAssertion() throws Exception{
		TagAssertion asrt = dao.getTagAssertion((1));
		assertNotNull(asrt);
		assertEquals((1),asrt.getProperty().getId());
		asrt.setProperty(new TagProperty((3)));
		assertTrue(dao.updateTagAssertion(asrt));
		asrt = dao.getTagAssertion((1));
		assertEquals((3),asrt.getProperty().getId());
	}
	
	public void testFindTagAssertionUser() throws Exception{
		List<TagAssertion> assertions = dao.findTagAssertion(new User((1)));
		assertEquals(2,assertions.size());
		
		TagAssertion ta1 = assertions.get(0);
		assertEquals(100,ta1.getSubject().getId());
		assertEquals(101,ta1.getObject().getId());
		assertEquals(1,ta1.getProperty().getId());
		assertEquals(1,ta1.getSubject().getTag().getId());
		assertEquals(2,ta1.getObject().getTag().getId());
		assertEquals("cnn",ta1.getSubject().getTag().getLabel());
		assertEquals("news",ta1.getObject().getTag().getLabel());
		assertEquals("hchen1",ta1.getUser().getUsername());
		assertEquals("hchen1",ta1.getSubject().getUser().getUsername());
		assertEquals("hchen1",ta1.getObject().getUser().getUsername());
	}
	
	public void testFindTagAssertionSubject() throws Exception{
		User hchen1 = new User((1));
		UserTag hchen1_news = new UserTag((101));
		List<TagAssertion> assertions = dao.findTagAssertion(hchen1,hchen1_news,null,null);
		assertEquals(1,assertions.size());
	}
	
	public void testFindTagAssertionObject() throws Exception{
		User hchen1 = new User((1));
		UserTag gnizr_brknews = new UserTag((103));
		List<TagAssertion> assertions = dao.findTagAssertion(hchen1,null,null,gnizr_brknews);
		assertEquals(1,assertions.size());
	}
	
	public void testFindTagAssertion() throws Exception{
		User user = new User((1));
		UserTag subjectTag = new UserTag((100));
		TagProperty tagPrpt = new TagProperty((1));
		UserTag objectTag = new UserTag((101));
		List<TagAssertion> assertions = dao.findTagAssertion(user, subjectTag, tagPrpt, objectTag);
		assertEquals(1,assertions.size());
	}
	
	public void testFindTagAssertionSubjectPrpt() throws Exception{
		User hchen1 = new User((1));
		UserTag cnn = new UserTag((100));
		TagProperty related = new TagProperty((1));
		List<TagAssertion> assertions = dao.findTagAssertion(hchen1,cnn,related,null);
		assertEquals(1,assertions.size());
	}
	
	public void testFindTagAssertionPrptObject() throws Exception{
		User hchen1 = new User((1));
		UserTag breakingnews = new UserTag((103));
		TagProperty narrower = new TagProperty((3));
		List<TagAssertion> assertions = dao.findTagAssertion(hchen1,null,narrower,breakingnews);
		assertEquals(1,assertions.size());
	}
	
	public void testFindTagAssertionPrpt() throws Exception{
		User hchen1 = new User((1));
		TagProperty prpt = new TagProperty((1));
		List<TagAssertion> assertions = dao.findTagAssertion(hchen1,null,prpt,null);
		assertEquals(1,assertions.size());
	}
	
	public void testDeleteSKOSRelatedAssertion1() throws Exception {
		User user = new User(3);
		UserTag subjectTag = new UserTag(200);
		UserTag[] objectTag = {new UserTag(201)};
		boolean isOkay = dao.deleteSKOSRelatedAssertion(user, subjectTag, objectTag);
		assertTrue(isOkay);
		List<TagAssertion> result = dao.findTagAssertion(user, subjectTag,new TagProperty(1),null);
		assertEquals(0,result.size());
	}
	
	public void testDeleteSKOSRelatedAssertion2() throws Exception {
		User user = new User(3);
		UserTag subjectTag = new UserTag(202);
		UserTag[] objectTag = {new UserTag(201)};
		boolean isOkay = dao.deleteSKOSRelatedAssertion(user, subjectTag, objectTag);
		assertTrue(isOkay);
		List<TagAssertion> result = dao.findTagAssertion(user, subjectTag,new TagProperty(1),null);
		assertEquals(0,result.size());
	}
	
	public void testDeleteSKOSBroaderAssertion() throws Exception{
		User user = new User(3);
		UserTag subjectTag = new UserTag(200);
		UserTag[] objectTag = {new UserTag(201)};
		boolean isOkay = dao.deleteSKOSBroaderAssertion(user, subjectTag, objectTag);
		assertTrue(isOkay);
		List<TagAssertion> result = dao.findTagAssertion(user, subjectTag, new TagProperty(2),new UserTag(201));
		assertEquals(0,result.size());
		
		result = dao.findTagAssertion(user, new UserTag(201), new TagProperty(3), subjectTag);
		assertEquals(0,result.size());
	}
	
	public void testDeleteRDFTypeAssertion() throws Exception {
		User user = new User(3);
		UserTag subjectTag = new UserTag(200);
		UserTag[] objectTag = {new UserTag(201)};
		boolean isOkay = dao.deleteRDFTypeAssertion(user,subjectTag,objectTag);
		assertEquals(true,isOkay);
		
		List<TagAssertion> result = null;
		result = dao.findTagAssertion(user,subjectTag,new TagProperty(4),new UserTag(201));
		assertEquals(0,result.size());
		
		result = dao.findTagAssertion(user, subjectTag, new TagProperty(4), new UserTag(202));
		assertEquals(1,result.size());

	}
	
	public void testDeleteNarrowerAssertion() throws Exception{
		User user = new User(3);
		UserTag subjectTag = new UserTag(202);
		UserTag[] objectTag = {new UserTag(201),new UserTag(203),new UserTag(200)};
		boolean isOkay = dao.deleteSKOSNarrowerAssertion(user,subjectTag,objectTag);
		assertEquals(true,isOkay);
		
		List<TagAssertion> result = null;
		result = dao.findTagAssertion(user,subjectTag,new TagProperty(3),new UserTag(201));
		assertEquals(0,result.size());
		
		result = dao.findTagAssertion(user, subjectTag, new TagProperty(3), new UserTag(203));
		assertEquals(0,result.size());
		
		result = dao.findTagAssertion(user, subjectTag, new TagProperty(3), new UserTag(200));
		assertEquals(0, result.size());
	}
	
	public void testCreateTagAssertionBatch() throws Exception{
		TagAssertion[] ta = new TagAssertion[1];
		ta[0] = new TagAssertion(new UserTag(100),new TagProperty(1),new UserTag(103),new User(1));
		
		boolean isOkay = dao.createTagAssertion(ta);
		assertTrue(isOkay);
		
		List<TagAssertion> result = dao.findTagAssertion(new User(1),new UserTag(100),new TagProperty(1),new UserTag(103));
		assertEquals(1,result.size());
	}
	
	public void testCreateTagAssertionBatch2() throws Exception{
		TagAssertion[] ta = new TagAssertion[3];
		ta[0] = new TagAssertion(new UserTag(100),new TagProperty(1),new UserTag(103),new User(1));
		ta[1] = new TagAssertion(new UserTag(101),new TagProperty(1),new UserTag(103),new User(1));
		ta[2] = new TagAssertion(new UserTag(100),new TagProperty(1),new UserTag(103),new User(1));
		
		boolean isOkay = dao.createTagAssertion(ta);
		assertTrue(isOkay);
		
		List<TagAssertion> result = dao.findTagAssertion(new User(1),new UserTag(100),new TagProperty(1),new UserTag(103));
		assertEquals(1,result.size());
		
		result = dao.findTagAssertion(new User(1),new UserTag(101),new TagProperty(1),new UserTag(103));
		assertEquals(1,result.size());
	}

	public void testDeleteRDFTypeClassAssertion(){
		User user3 = new User(3);
		UserTag classTag201 = new UserTag(201);
		List<TagAssertion> r  = dao.findTagAssertion(user3,null,new TagProperty(4),classTag201);
		assertEquals(2,r.size());
		
		boolean isOkay = dao.deleteRDFTypeClassAssertion(new User(3), classTag201);
		assertTrue(isOkay);		
		
		r = dao.findTagAssertion(user3,null,new TagProperty(4),classTag201);
		assertEquals(0,r.size());
	}
	
	public void testDeleteRDFTypeAssertion2() throws Exception{
		UserTag classTag201 = new UserTag(201);
		User user3 = new User(3);
		List<TagAssertion> r  = dao.findTagAssertion(user3,null,new TagProperty(4),classTag201);
		assertEquals(2,r.size());
		
		UserTag[] subjTags = new UserTag[]{ new UserTag(200),new UserTag(202)};
		assertTrue(dao.deleteRDFTypeAssertion(user3, subjTags, classTag201));
		
		r  = dao.findTagAssertion(user3,null,new TagProperty(4),classTag201);
		assertEquals(0,r.size());
	}
	
	
}
