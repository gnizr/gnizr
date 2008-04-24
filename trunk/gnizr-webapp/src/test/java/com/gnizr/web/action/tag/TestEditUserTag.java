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
package com.gnizr.web.action.tag;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.tag.TagManager;
import com.gnizr.core.tag.TagPager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.opensymphony.xwork.ActionSupport;

public class TestEditUserTag extends GnizrWebappTestBase {

	private TagManager tagManager;
	private TagPager tagPager;
	private UserManager userManager;
	private EditUserTag action;
	
	
	protected void setUp() throws Exception {
		super.setUp();
		tagManager = new TagManager(getGnizrDao());
		tagPager = new TagPager(getGnizrDao());
		userManager = new UserManager(getGnizrDao());
		action = new EditUserTag();
		action.setTagManager(tagManager);
		action.setTagPager(tagPager);				
		action.setUserManager(userManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestEditUserTag.class.getResourceAsStream("/TestEditUserTags-input.xml"));		
	}
	
	public void testAddRelatedTags() throws Exception{
		action.setLoggedInUser(new User(1));
		action.setTag("webwork");
		action.setRelatedTags("wii java");
		action.setNarrowerTags(null);
		action.setBroaderTags(null);
		action.setInstanceTags("");		
		assertEquals(ActionSupport.SUCCESS,action.execute());		
		
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		String related = action.getRelatedTags();
		assertTrue(related.contains("java"));
		assertTrue(related.contains("wii"));
		
		action.setRelatedTags("java wii tag:hchen1/play  tag:joe tag:    ");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		related = action.getRelatedTags();
		assertTrue(related.contains("java"));
		assertTrue(related.contains("wii"));	
		assertTrue(related.contains("tag:hchen1/play"));
		assertTrue(related.contains("tag:"));
		assertTrue(related.contains("joe"));
	}
	
	public void testDeleteRelatedTags() throws Exception{
		action.setLoggedInUser(new User(1));
		action.setTag("webwork");
		action.setRelatedTags("wii gn:tag=hchen1/play java tag: ");
		action.setNarrowerTags(null);
		action.setBroaderTags(null);
		action.setInstanceTags("");		
		assertEquals(ActionSupport.SUCCESS,action.execute());		
		
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		String related = action.getRelatedTags();
		assertTrue(related.contains("java"));
		assertTrue(related.contains("wii"));
		assertTrue(related.contains("tag:"));
		assertTrue(related.contains("tag:hchen1/play"));
		
		action.setRelatedTags("wii");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		related = action.getRelatedTags();
		assertFalse(related.contains("java"));
		assertTrue(related.contains("wii"));	
		
		action.setRelatedTags("");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		related = action.getRelatedTags();
		assertFalse(related.contains("java"));
		assertFalse(related.contains("wii"));	
	}
	
	public void testUpdateRelatedTags() throws Exception{
		action.setLoggedInUser(new User(1));
		action.setTag("webwork");
		action.setRelatedTags("wii java");
		action.setNarrowerTags(null);
		action.setBroaderTags(null);
		action.setInstanceTags("");		
		assertEquals(ActionSupport.SUCCESS,action.execute());		
		
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		String related = action.getRelatedTags();
		assertTrue(related.contains("java"));
		assertTrue(related.contains("wii"));
		
		action.setRelatedTags("wii");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		related = action.getRelatedTags();
		assertFalse(related.contains("java"));
		assertTrue(related.contains("wii"));	
		
		action.setRelatedTags("programming semanticweb java");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		related = action.getRelatedTags();
		assertTrue(related.contains("java"));
		assertTrue(related.contains("programming"));
		assertTrue(related.contains("semanticweb"));
		assertFalse(related.contains("wii"));	
	}
	

	public void testUpdateNarrowerTags() throws Exception{
		action.setLoggedInUser(new User(1));
		action.setTag("webwork");
		action.setRelatedTags("");
		action.setNarrowerTags("actions interceptors");
		action.setBroaderTags(null);
		action.setInstanceTags("");		
		assertEquals(ActionSupport.SUCCESS,action.execute());		
		
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		String narrower = action.getNarrowerTags();
		assertTrue(narrower.contains("actions"));
		assertTrue(narrower.contains("interceptors"));
		
		action.setNarrowerTags("web programming actions");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		narrower = action.getNarrowerTags();
		assertTrue(narrower.contains("web"));
		assertTrue(narrower.contains("programming"));
		assertTrue(narrower.contains("actions"));
		
		assertEquals("",action.getRelatedTags());
		assertEquals("",action.getInstanceTags());
		assertEquals("videogame",action.getBroaderTags());
	}
	
	public void testUpdateBroaderTags() throws Exception{
		action.setLoggedInUser(new User(1));
		action.setTag("webwork");
		action.setRelatedTags("");
		action.setNarrowerTags("");
		action.setBroaderTags("programming 'java \"123      a:b_aaa ");
		action.setInstanceTags("");		
		assertEquals(ActionSupport.SUCCESS,action.execute());		
		
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		String broader = action.getBroaderTags();
		assertTrue(broader.contains("programming"));
		assertTrue(broader.contains("'java"));
		assertTrue(broader.contains("\"123"));
		assertTrue(broader.contains("a:b_aaa"));
		
		action.setBroaderTags("a.:,..");
		assertEquals(ActionSupport.SUCCESS,action.execute());
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		broader = action.getBroaderTags();
		assertTrue(broader.contains("a.:,.."));
		assertFalse(broader.contains("programming"));
		assertFalse(broader.contains("actions"));
		assertFalse(broader.contains("'java"));
		assertFalse(broader.contains("\"123"));
		assertFalse(broader.contains("a:b_aaa"));
		
		assertEquals("",action.getRelatedTags());
		assertEquals("",action.getInstanceTags());
		assertEquals("",action.getNarrowerTags());
	}
	
	public void testUpdateInstanceTags() throws Exception{
		action.setLoggedInUser(new User(1));
		action.setTag("webwork");
		action.setRelatedTags("");
		action.setNarrowerTags("");
		action.setBroaderTags("");
		action.setInstanceTags("ins1 ins2 abc:ins2");		
		assertEquals(ActionSupport.SUCCESS,action.execute());		
		
		String clsTags = action.getInstanceTags();
		assertTrue(clsTags.contains("ins1"));
		assertTrue(clsTags.contains("ins2"));
		assertTrue(clsTags.contains("abc:ins2"));
		
		action.setInstanceTags("ins1");
		action.setRelatedTags("ins1 ins2");
		assertEquals(ActionSupport.SUCCESS,action.execute());	
		
		clsTags = action.getInstanceTags();
		assertTrue(clsTags.contains("ins1"));
		assertFalse(clsTags.contains("ins2"));
		
		String related = action.getRelatedTags();
		assertTrue(related.contains("ins1"));
		assertTrue(related.contains("ins2"));		
	}
	
	
	public void testFetchEditData() throws Exception{
		action.setTag("webwork");
		action.setLoggedInUser(new User(1));
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		String related = action.getRelatedTags();
		assertEquals("wii",related);
		
		String narrower = action.getNarrowerTags();
		assertEquals("videogame",narrower);
		
		String broader = action.getBroaderTags();
		assertEquals("videogame",broader);
		
		String cls = action.getInstanceTags();
		assertEquals("games",cls);
	}
	
	
	public void testFetchEditData2() throws Exception{
		action.setTag("semanticweb");
		action.setLoggedInUser(new User(1));
		assertEquals(ActionSupport.SUCCESS,action.fetchEditData());
		
		UserTag ut = action.getEditUserTag();
		assertTrue((ut.getId()>0));
		
		String related = action.getRelatedTags();
		assertTrue((related.length() == 0));
		
		String narrower = action.getNarrowerTags();
		assertEquals("",narrower);
		
		String broader = action.getBroaderTags();
		assertEquals("",broader);
		
		String cls = action.getInstanceTags();
		assertEquals("",cls);	
	}
	
}
