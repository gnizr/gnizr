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
package com.gnizr.core.web.action.tag;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.tag.TagManager;
import com.gnizr.core.tag.TagPager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;


public class TestSuggestTags extends GnizrWebappTestBase {

	private TagPager tagPager;
	private SuggestTags suggestTags;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tagPager = new TagPager(getGnizrDao());
		suggestTags = new SuggestTags();
		suggestTags.setTagPager(tagPager);
		suggestTags.setTagManager(new TagManager(getGnizrDao()));
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSuggestTags.class.getResourceAsStream("/TestSuggestTags-input.xml"));
	}
	
	public void testAction() throws Exception{		
		suggestTags.setUser(new User(1));
		suggestTags.setTag("wii");
		suggestTags.execute();
		
		List<UserTag> tags = suggestTags.getSkosRelatedTags();
		assertFalse(tags.isEmpty());
		assertEquals(2,tags.size());
	
		List<String> tagstr = new ArrayList<String>();
		for(Iterator<UserTag> it = tags.iterator();it.hasNext();){
			UserTag ut = it.next();
			tagstr.add(ut.getTag().getLabel());
		}
		assertTrue(tagstr.contains("webwork"));
		assertTrue(tagstr.contains("games"));
	}

}
