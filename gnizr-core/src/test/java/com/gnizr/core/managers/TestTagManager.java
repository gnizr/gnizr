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
package com.gnizr.core.managers;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.tag.TagManager;
import com.gnizr.core.user.TestUserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.TagProperty;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.tag.TagAssertionDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.tag.TagPropertyDao;
import com.gnizr.db.dao.user.UserDao;

public class TestTagManager extends GnizrCoreTestBase {

	private TagManager manager;

	
	protected void setUp() throws Exception {
		super.setUp();
		manager = new TagManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserManager.class.getResourceAsStream("/TestTagManager-input.xml"));
	}
	
	public void testAddTag() throws Exception{
		Tag t = new Tag("t1234567");
		int id = manager.addTag(t);
		assertTrue((id > 0));
	}
	
	public void testCreateUserTag() throws Exception{
		UserTag ut = manager.createUserTag(new User(1),"javadevelopment");
		assertNotNull(ut);
		assertEquals("javadevelopment",ut.getTag().getLabel());

		UserTag ut2 = manager.createUserTag(new User(1),"javadevelopment");
		assertNotNull(ut2);
		assertEquals("javadevelopment",ut2.getTag().getLabel());
		assertEquals(ut.getId(),ut2.getId());
	}
	
	public void testGetTag() throws Exception {
		Tag t = manager.getTag("videogame");
		assertNotNull(t);
		assertEquals(3,t.getId());
	}
	
	public void testAddTagProperty() throws Exception{
		TagProperty prpt = new TagProperty();
		prpt.setName("newprpt");
		prpt.setNamespacePrefix("gn");
		assertTrue(manager.addTagProperty(prpt));
		
		TagPropertyDao prptDao = getGnizrDao().getTagPropertyDao();
		prpt = prptDao.getTagProperty("gn","newprpt");
		Boolean okay = prptDao.deleteTagProperty(prpt.getId());
		assertTrue(okay);
	}

	public void testDeleteTagProperty() throws Exception{
		TagProperty prpt = new TagProperty(1);
		assertTrue(manager.deleteTagProperty(prpt));
		TagPropertyDao prptDao = getGnizrDao().getTagPropertyDao();
		assertNull(prptDao.getTagProperty("skos","related"));
		assertFalse(manager.deleteTagProperty(new TagProperty((3939))));
	}
	
	public void testListTagProperty() throws Exception{
		assertEquals(4,manager.listTagProperty().size());
	}
	
	public void testListTagAssertion() throws Exception{
		List<TagAssertion> asrts = manager.listTagAssertion(new User("hchen1"));
		assertNotNull(asrts);
		assertEquals(4, asrts.size());
		assertNotNull(asrts.get(0).getUser().getFullname());
		assertNotNull(asrts.get(1).getSubject().getTag().getLabel());
	}
	
	public void testDeleteTagAssertion() throws Exception{
		User user = new User("hchen1");
		UserTag subjTag = new UserTag("hchen1","webwork");
		TagProperty prpt = new TagProperty(1);
		UserTag objTag = new UserTag("hchen1","wii");
		TagAssertion asrt = new TagAssertion(subjTag,prpt,objTag,user);
		
		List<TagAssertion> asrts = manager.listTagAssertion(user);
		assertEquals(4,asrts.size());
		
		boolean okay = manager.deleteTagAssertion(asrt);
		assertTrue(okay);
		
		asrts = manager.listTagAssertion(user);
		assertEquals(3,asrts.size());
	}

	public void testIsGeoTagTypeAssertion() throws Exception{
		TagAssertion assertion = manager.getTagAssertion(3);
		assertFalse(manager.isGeoTagTypeAssertion(assertion));
		assertion = manager.getTagAssertion(4);
		assertTrue(manager.isGeoTagTypeAssertion(assertion));
	}
	
	public void testAddTagAssertion() throws Exception{
		User user = new User("hchen1");
		UserTag subjTag = new UserTag("hchen1","videogame");
		TagProperty prpt = new TagProperty(1);
		UserTag objTag = new UserTag("hchen1","wii");
		TagAssertion asrt = new TagAssertion(subjTag,prpt,objTag,user);
		
		// we shouldnt be able to delete a non-existing tag assertion
		boolean caughtException = false;
		try{
			manager.deleteTagAssertion(asrt);
		}catch(Exception e){
			caughtException = true;
		}
		assertTrue(caughtException);
		
		// before adding, we should have 4 assertions
		List<TagAssertion> asrts = manager.listTagAssertion(user);
		assertEquals(4,asrts.size());
			
		// add a new assertion
		boolean okay = manager.addTagAssertion(asrt);
		assertTrue(okay);
		
		// after adding, we should have 5 assertions
		asrts = manager.listTagAssertion(user);
		assertEquals(5,asrts.size());
		
		// delete the newly added assertion
		okay = manager.deleteTagAssertion(asrt);
		assertTrue(okay);
		
		// back to 4
		asrts = manager.listTagAssertion(user);
		assertEquals(4,asrts.size());
	}
	
	public void testAddTagAssertionForceTagCreate() throws Exception{
		User user = new User("hchen1");
		UserTag subjTag = new UserTag("hchen1","java");
		TagProperty prpt = new TagProperty(1);
		UserTag objTag = new UserTag("hchen1","java.logging");
		TagAssertion asrt = new TagAssertion(subjTag,prpt,objTag,user);
		
		boolean ok = manager.addTagAssertion(asrt,true);
		assertTrue(ok);
		ok = manager.deleteTagAssertion(asrt);
		assertTrue(ok);
		
		UserDao userDao = getGnizrDao().getUserDao();
		TagDao tagDao = getGnizrDao().getTagDao();
		GnizrDaoUtil.fillId(tagDao, userDao,subjTag);
		GnizrDaoUtil.fillId(tagDao,userDao,objTag);
		
		tagDao.deleteTag(subjTag.getTag().getId());
		tagDao.deleteTag(objTag.getTag().getId());
	}
	
	public void testGetUserTag() throws Exception{
		UserTag ut = manager.getUserTag(new User("hchen1"),new Tag("wii"));
		assertNotNull(ut);
		assertEquals((2),ut.getTag().getId());
		assertEquals("Harry Chen",ut.getUser().getFullname());
		
		ut = manager.getUserTag(new User((1)),new Tag("webwork"));
		assertNotNull(ut);
		assertEquals((1),ut.getTag().getId());
		assertEquals("hchen1",ut.getUser().getUsername());
		
		UserTag ut2 = manager.getUserTag(ut.getId());
		assertEquals(ut,ut2);
	}
	
	public void testGetTagAssertion() throws Exception{
		TagAssertion a = manager.getTagAssertion(2);
		assertNotNull(a);
		assertEquals(2,a.getSubject().getId());
		assertEquals(1,a.getProperty().getId());
		assertEquals(1,a.getObject().getId());
	}
	

	public void testAddSKOSRelatedTags() throws Exception{
		User user = new User(1);
		UserTag subjectTag = new UserTag(1);
		List<String> tags2add = new ArrayList<String>();
		tags2add.add("abc");
		tags2add.add("funstuff");
		tags2add.add("123_092");
		
		boolean isOkay = manager.addSKOSRelatedTags(user, subjectTag,tags2add);
		assertTrue(isOkay);
		
		List<TagAssertion> result = manager.listTagAssertion(user);
		assertEquals(7,result.size());
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		
		UserTag abcUserTag = manager.createUserTag(user, "abc");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosRelatedProperty(),abcUserTag);
		assertEquals(1,result.size());
		
		UserTag funstuffUserTag = manager.createUserTag(user, "funstuff");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosRelatedProperty(),funstuffUserTag);
		assertEquals(1,result.size());
	}
	
	public void testAddSKOSRelatedTags2() throws Exception{
		User user = new User(1);
		UserTag subjectTag = new UserTag(1);
		List<String> tags2add = new ArrayList<String>();
		tags2add.add("tag:gnizr/abc");
		tags2add.add("funstuff");
		tags2add.add("tag:123_092");
		
		boolean isOkay = manager.addSKOSRelatedTags(user, subjectTag,tags2add);
		assertTrue(isOkay);
		
		List<TagAssertion> result = manager.listTagAssertion(user);
		assertEquals(7,result.size());
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		
		UserTag abcUserTag = manager.createUserTag(new User(2), "abc");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosRelatedProperty(),abcUserTag);
		assertEquals(1,result.size());
		
		UserTag funstuffUserTag = manager.createUserTag(user, "funstuff");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosRelatedProperty(),funstuffUserTag);
		assertEquals(1,result.size());
		
		UserTag tag123_092 = manager.createUserTag(user, "123_092");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosRelatedProperty(),tag123_092);
		assertEquals(1,result.size());
	}
	
	
	public void testDeleteSKOSRelatedTags() throws Exception{
		User user = new User(1);
		// subjectTag is "webwork"
		UserTag subjectTag = new UserTag(1);
		
		List<String> tags2del = new ArrayList<String>();
		tags2del.add("wii");
		tags2del.add("java");
		tags2del.add("programming");
		// only "wii" is currently associated
		// "java" and "programming" are just for testing
		// the call should not fail even if those tags dont already exist
		boolean isOkay = manager.deleteSKOSRelatedTags(user, subjectTag,tags2del);
		assertTrue(isOkay);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();		
		UserTag wiiUserTag = manager.createUserTag(user, "wii");
		List<TagAssertion> result = 
			dao.findTagAssertion(user, subjectTag,manager.getSkosRelatedProperty(),wiiUserTag);
		assertEquals(0,result.size());
	}
	
	
	public void testDeleteSKOSRelatedTagsSymmetric() throws Exception{
		User user = new User(1);
		UserTag subjectTag = new UserTag(2);
		
		List<String> tags2del = new ArrayList<String>();
		tags2del.add("webwork");
		
		boolean isOkay = manager.deleteSKOSRelatedTags(user, subjectTag, tags2del);
		assertTrue(isOkay);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();		
		UserTag wiiUserTag = manager.createUserTag(user, "wii");
		List<TagAssertion> result = 
			dao.findTagAssertion(user, subjectTag,manager.getSkosRelatedProperty(),wiiUserTag);
		assertEquals(0,result.size());
	}
	
	
	public void testAddBroaderTags() throws Exception {
		User user = new User(1);
		UserTag subjectTag = new UserTag(1);
		List<String> tags2add = new ArrayList<String>();
		tags2add.add("abc");
		tags2add.add("funstuff");
		tags2add.add("123_092");
		
		boolean isOkay = manager.addSKOSBroaderTags(user, subjectTag,tags2add);
		assertTrue(isOkay);
		
		List<TagAssertion> result = manager.listTagAssertion(user);
		assertEquals(7,result.size());
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		
		UserTag abcUserTag = manager.createUserTag(user, "abc");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosBroaderProperty(),abcUserTag);
		assertEquals(1,result.size());
		
		UserTag funstuffUserTag = manager.createUserTag(user, "funstuff");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosBroaderProperty(),funstuffUserTag);
		assertEquals(1,result.size());		
		
		result = dao.findTagAssertion(user, subjectTag,manager.getSkosBroaderProperty(),null);
		assertEquals(3,result.size());
	}
	
	
	public void testDeleteSKOSBroaderTags() throws Exception{
		User user = new User(1);
		// subjectTag is "webwork"
		UserTag subjectTag = new UserTag(1);
		
		List<String> tags2add = new ArrayList<String>();
		tags2add.add("wii");
		
		manager.addSKOSBroaderTags(user, subjectTag, tags2add);
		
		List<String> tags2del = new ArrayList<String>();
		tags2del.add("wii");
		tags2del.add("java");
		tags2del.add("programming");
		// only "wii" is currently associated
		// "java" and "programming" are just for testing
		// the call should not fail even if those tags dont already exist
		boolean isOkay = manager.deleteSKOSBroaderTags(user, subjectTag,tags2del);
		assertTrue(isOkay);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();		
		UserTag wiiUserTag = manager.createUserTag(user, "wii");
		List<TagAssertion> result = 
			dao.findTagAssertion(user, subjectTag,manager.getSkosBroaderProperty(),wiiUserTag);
		assertEquals(0,result.size());
	}
	
	public void testAddNarrowerTags() throws Exception {
		User user = new User(1);
		UserTag subjectTag = new UserTag(1);
		List<String> tags2add = new ArrayList<String>();
		tags2add.add("abc");
		tags2add.add("funstuff");
		tags2add.add("123_092");
		
		boolean isOkay = manager.addSKOSNarrowerTags(user, subjectTag,tags2add);
		assertTrue(isOkay);
		
		List<TagAssertion> result = manager.listTagAssertion(user);
		assertEquals(7,result.size());
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		
		UserTag abcUserTag = manager.createUserTag(user, "abc");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosNarrowerProperty(),abcUserTag);
		assertEquals(1,result.size());
		
		UserTag funstuffUserTag = manager.createUserTag(user, "funstuff");
		result = dao.findTagAssertion(user,subjectTag,manager.getSkosNarrowerProperty(),funstuffUserTag);
		assertEquals(1,result.size());		
		
		result = dao.findTagAssertion(user, subjectTag,manager.getSkosNarrowerProperty(),null);
		assertEquals(3,result.size());
	}
	
	
	public void testDeleteSKOSNarrowerTags() throws Exception{
		User user = new User(1);
		// subjectTag is "webwork"
		UserTag subjectTag = new UserTag(1);
		
		List<String> tags2add = new ArrayList<String>();
		tags2add.add("wii");
		
		manager.addSKOSNarrowerTags(user, subjectTag, tags2add);
		
		List<String> tags2del = new ArrayList<String>();
		tags2del.add("wii");
		tags2del.add("java");
		tags2del.add("programming");
		// only "wii" is currently associated
		// "java" and "programming" are just for testing
		// the call should not fail even if those tags dont already exist
		boolean isOkay = manager.deleteSKOSNarrowerTags(user, subjectTag,tags2del);
		assertTrue(isOkay);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();		
		UserTag wiiUserTag = manager.createUserTag(user, "wii");
		List<TagAssertion> result = 
			dao.findTagAssertion(user, subjectTag,manager.getSkosNarrowerProperty(),wiiUserTag);
		assertEquals(0,result.size());
	}
	
	public void testAddRDFTypeTags() throws Exception{
		User user = new User(1);
		UserTag subjectTag = new UserTag(2);
		
		List<String> tags2add = new ArrayList<String>();
		tags2add.add("funstuff");
		boolean isOkay = manager.addRDFTypeTags(user, subjectTag,tags2add);
		assertTrue(isOkay);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		List<TagAssertion> result = dao.findTagAssertion(user, subjectTag,manager.getRdfTypeProperty(),null);
		assertEquals(2,result.size());
	}
	
	public void testAddRDFInstanceTags() throws Exception{
		User user = new User(1);
		UserTag classTag = new UserTag(3);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		List<TagAssertion> result = dao.findTagAssertion(user, null,manager.getRdfTypeProperty(),classTag);
		assertEquals(1,result.size());
		
		List<String> instTags = new ArrayList<String>();
		instTags.add("funstuff");
		instTags.add("stufffun");
		assertTrue(manager.addRDFInstanceTags(user, classTag, instTags));
		
		result = dao.findTagAssertion(user, null,manager.getRdfTypeProperty(),classTag);
		assertEquals(3,result.size());
	}
	
	public void testDeleteRDFInstanceTags() throws Exception{
		User user = new User(1);
		UserTag classTag = new UserTag(3);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		List<TagAssertion> result = dao.findTagAssertion(user, null,manager.getRdfTypeProperty(),classTag);
		assertEquals(1,result.size());
		
		List<String> instTags = new ArrayList<String>();
		instTags.add("funstuff");
		instTags.add("stufffun");
		assertTrue(manager.addRDFInstanceTags(user, classTag, instTags));
		
		result = dao.findTagAssertion(user, null,manager.getRdfTypeProperty(),classTag);
		assertEquals(3,result.size());
		
		assertTrue(manager.deleteRDFInstanceTags(user, classTag, instTags));
		result = dao.findTagAssertion(user, null,manager.getRdfTypeProperty(),classTag);
		assertEquals(1,result.size());
	}
	
	
	public void testDeleteRDFTypeTags() throws Exception{
		User user = new User(1);
		UserTag subjectTag = new UserTag(2);
		
		List<String> tags2del = new ArrayList<String>();
		tags2del.add("videogame");
		
		boolean isOkay = manager.deleteRDFTypeTags(user, subjectTag, tags2del);
		assertTrue(isOkay);
		
		TagAssertionDao dao = getGnizrDao().getTagAssertionDao();
		List<TagAssertion> result = dao.findTagAssertion(user, subjectTag,manager.getRdfTypeProperty(),null);
		assertEquals(0,result.size());
	}
	
	
	
	
}
