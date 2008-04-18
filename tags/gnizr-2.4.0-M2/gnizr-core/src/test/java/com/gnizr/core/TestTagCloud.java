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
package com.gnizr.core;

import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.tag.TagCloud;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class TestTagCloud extends GnizrCoreTestBase {

	private TagCloud tagCloud;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagCloud = new TagCloud(getGnizrDao());
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagCloud.class.getResourceAsStream("/TestTagCloud-input.xml"));
	}

	public void testGetFreqCloud() throws Exception{
		User user = new User("hchen1");
		
		List<UserTag> cloud = tagCloud.getFreqCloud(user,1);
		assertFalse(cloud.isEmpty());
		assertEquals(3,cloud.size());
		
		cloud = tagCloud.getFreqCloud(user,5);
		assertEquals(1,cloud.size());
		UserTag ut = cloud.get(0);
		assertEquals("for:fjdksjfl",ut.getTag().getLabel());
		assertEquals((21), ut.getTag().getCount());
		assertEquals("hchen1",ut.getUser().getUsername());
		assertEquals((6),ut.getCount());
	}
	
	public void testGetPopularTagCloud() throws Exception {
		Map<Tag,Integer> cloud = tagCloud.getPopularTagCloudSortAlph();
		assertEquals(6,cloud.size());
	}
	
	public void testGetCommonTagCloud() throws Exception{
		List<LinkTag> cloud = tagCloud.getCommonTagCloud(new Link(101),100);
		assertEquals(3,cloud.size());
		assertEquals(100,cloud.get(0).getCount());
		assertNotNull(cloud.get(0).getLink().getUrl());
		assertEquals(87,cloud.get(1).getCount());
		assertEquals(16,cloud.get(2).getCount());
	}
	
	
	public void testFilterOut() throws Exception{
		User user = new User("hchen1");
		
		List<UserTag> cloud = tagCloud.getFreqCloud(user,1);
		assertFalse(cloud.isEmpty());
		assertEquals(3,cloud.size());
		
		String[] filterPattern = {"for:.*"};
		cloud = tagCloud.getFreqCloudSortAlph(user,1,filterPattern);
		assertFalse(cloud.isEmpty());
		assertEquals(2,cloud.size());
	}
}
