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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class TestTagPager extends GnizrCoreTestBase {

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
		return new FlatXmlDataSet(TestTagPager.class.getResourceAsStream("/TestTagPager-input.xml"));
	}
	
	public void testFindSKOSRelated() throws Exception{
		User user = new User((1));
		UserTag tag = new UserTag("hchen1","games");
		List<UserTag> relatedTags = tagPager.findSKOSRelated(user, tag);
		assertEquals(2,relatedTags.size());
	}
	
	public void testFindUserTagClass() throws Exception{
		User user = new User((1));
		List<UserTag> classTags = tagPager.findUserTagClass(user);
		assertEquals(1,classTags.size());
		assertEquals("videogame",classTags.get(0).getTag().getLabel());
		assertEquals("hchen1",classTags.get(0).getUser().getUsername());
	}
	
	public void testFindSKOSNarrower() throws Exception{
		User user = new User((1));
		UserTag tag = new UserTag("hchen1","games");
		List<UserTag> narrowerTags = tagPager.findSKOSNarrower(user,tag);
		assertEquals(2,narrowerTags.size());
		UserTag videoGame = narrowerTags.get(0);
		assertEquals("videogame",videoGame.getTag().getLabel());
		UserTag wii = narrowerTags.get(1);
		assertEquals("wii",wii.getTag().getLabel());
	}
	
	public void testFindSKOSBroader() throws Exception{
		User user = new User((1));
		UserTag tag = new UserTag("hchen1","wii");
		List<UserTag> broaderTags = tagPager.findSKOSBroader(user,tag);
		assertEquals(2,broaderTags.size());
		UserTag videoGame = broaderTags.get(0);
		assertEquals("videogame",videoGame.getTag().getLabel());
		UserTag games = broaderTags.get(1);
		assertEquals("games",games.getTag().getLabel());
	}
	
	public void testFindUserTagInstance() throws Exception{
		User user = new User(1);
		UserTag clsTag = new UserTag(3);
		List<UserTag> results = tagPager.findUserTagInstance(user,clsTag);
		assertFalse(results.isEmpty());
		assertEquals(1,results.size());
		UserTag ut = results.get(0);
		assertEquals(2,ut.getId());
	}
	
	public void testFindRDFType() throws Exception{
		User user = new User(1);
		UserTag userTag = new UserTag(2);
		List<UserTag> results = tagPager.findRDFType(user, userTag);
		assertEquals(1,results.size());
		UserTag clsTag = results.get(0);
		assertEquals(3,clsTag.getId());
		assertEquals("videogame",clsTag.getTag().getLabel());
	}

}
