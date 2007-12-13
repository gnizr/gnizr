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

import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.tag.TagPager;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class TestTagPager2 extends GnizrCoreTestBase {

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
		return new FlatXmlDataSet(TestTagPager2.class.getResourceAsStream("/TestTagPager2-input.xml"));
	}
	
	public void testGetRDFTypeTagGroups() throws Exception{
		Map<String,List<UserTag>> tags = tagPager.getRDFTypeTagGroups(new User(1));
		assertEquals(2,tags.size());
		List<UserTag> g1 = tags.get("g1");
		List<UserTag> g2 = tags.get("g2");
		assertEquals(4,g1.size());
		assertEquals(2,g2.size());
		
		UserTag ut = g1.get(3);
		assertEquals(1,ut.getUser().getId());
	}
	
	
	
	

}
