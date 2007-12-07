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
package com.gnizr.core.web.action.clustermap;

import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.managers.TagManager;
import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestClusterUserFolder extends GnizrWebappTestBase {

	private ClusterUserFolder action;
	
	protected void setUp() throws Exception {
		super.setUp();
		TagManager tagManager = new TagManager(getGnizrDao());
		UserManager userManager = new UserManager(getGnizrDao());
		action = new ClusterUserFolder();
		action.setTagManager(tagManager);
		action.setUserManager(userManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestClusterUserFolder.class.getResourceAsStream("/TestClusterUserFolder-input.xml"));
	}

	public void testUsernameDefined() throws Exception{
		action.setUsername("hchen1");
		action.setFolderName("private folder");
		String code = action.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		
		List<Bookmark> bmarks = action.getBookmarks();
		assertEquals(3,bmarks.size());
		
		List<Tag> tags = action.getTags();
		assertEquals(4,tags.size());
		
		User u = action.getUser();
		assertNotNull(u);
		assertEquals(2,u.getId());
		
		Map<String,List<Integer>> cluster = action.getCluster();
		assertEquals(5,cluster.size());
		
		List<Integer> rootCluster = cluster.get("root");
		assertEquals(3,rootCluster.size());
		assertTrue(rootCluster.contains(1));
		assertTrue(rootCluster.contains(2));
		
		List<Integer> cnnCluster = cluster.get("1");
		assertEquals(2,cnnCluster.size());
		assertTrue(cnnCluster.contains(1));
		assertTrue(cnnCluster.contains(2));
		
		List<Integer> newsCluster = cluster.get("2");
		assertEquals(2,newsCluster.size());
		assertTrue(newsCluster.contains(1));
		assertTrue(newsCluster.contains(2));
		
		List<Integer> quesCluster = cluster.get("4");
		assertEquals(1,quesCluster.size());
		assertFalse(quesCluster.contains(1));
		assertTrue(quesCluster.contains(2));
		
		List<Integer> notagCluster = cluster.get("0");
		assertEquals(1,notagCluster.size());
		assertTrue(notagCluster.contains(4));
	}
	
}
