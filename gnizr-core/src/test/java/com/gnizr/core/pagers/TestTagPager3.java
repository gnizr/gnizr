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
package com.gnizr.core.pagers;

import java.util.ArrayList;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.dao.Tag;

public class TestTagPager3 extends GnizrCoreTestBase {

	private TagPager tagPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagPager = new TagPager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagPager3.class.getResourceAsStream("/TestTagPager3-input.xml"));
	}

	public void testGetPopularRelatedTagsByGnizrUser() throws Exception{
		List<Tag> relTags = tagPager.getPopularRelatedTagsByGnizrUser("games",1);
		assertEquals(2,relTags.size());
		List<String> tagLabel = new ArrayList<String>();
		for(Tag t : relTags){
			tagLabel.add(t.getLabel());
		}
		assertTrue(tagLabel.contains("wii"));
		assertTrue(tagLabel.contains("online"));
		
		relTags = tagPager.getPopularRelatedTagsByGnizrUser("games",5);
		assertEquals(1,relTags.size());
		assertEquals("online",relTags.get(0).getLabel());
	}
	
	
}
