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
package com.gnizr.web.action.search;

import java.util.List;

import net.sf.json.JSON;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.tag.TagManager;
import com.gnizr.core.tag.TagPager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.opensymphony.xwork.ActionSupport;

public class TestSuggestSearchTags extends GnizrWebappTestBase {

	private SuggestSearchTags action;	
	private TagManager tagManager;
	private TagPager tagPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagManager = new TagManager(getGnizrDao());
		tagPager = new TagPager(getGnizrDao());
		action = new SuggestSearchTags();
		action.setTagPager(tagPager);
		action.setTagManager(tagManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSuggestSearchTags.class.getResourceAsStream("/TestSuggestSearchTags-input.xml"));
	}

	public void testGoEmptySearchTerm() throws Exception{
		action.setQ("");
		String okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		List<String> tags = action.getSkosRelatedTags();
		assertTrue(tags.isEmpty());
		JSON json = action.getJsonResult();
		assertEquals("{}",json.toString());
	}
	
	public void testGo() throws Exception{
		action.setQ("games");
		String okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		List<String> tags= action.getSkosRelatedTags();
		assertFalse(tags.isEmpty());
		assertEquals(1,tags.size());		
		assertTrue(tags.contains("online"));
		System.out.println(action.getJsonResult().toString());
		
		tags = action.getSkosNarrowerTags();
		assertEquals(3,tags.size());
		assertTrue(tags.contains("boardgames"));
		assertTrue(tags.contains("videogame"));
		assertTrue(tags.contains("wii"));
		
		tags = action.getSkosBroaderTags();
		assertEquals(0,tags.size());
	}
	
	
}
