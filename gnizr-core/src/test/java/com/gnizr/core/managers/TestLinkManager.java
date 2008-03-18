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

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.link.LinkManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Link;

public class TestLinkManager extends GnizrCoreTestBase {

	private LinkManager linkManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		linkManager = new LinkManager(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestLinkManager.class.getResourceAsStream("/TestLinkHistory-input.xml"));
	}
	
	public void testFirstMatchedBookmark() throws Exception{
		Bookmark b = linkManager.getFirstMatchedBookmark("http://www.springframework.org/docs/reference/beans.html");
		assertNotNull(b);
		assertEquals("Spring Framework: Inversion of Control (IoC)",b.getTitle());
	}
	
	public void testPageLinkHistory() throws Exception{
		DaoResult<Bookmark> result = linkManager.pageLinkHistory(new Link(209), 0, 10);
		assertEquals(2,result.getSize());
		assertEquals(2,result.getResult().size());
		
		result = linkManager.pageLinkHistory(new Link(209), 1, 10);
		assertEquals(2,result.getSize());
		assertEquals(1,result.getResult().size());
	}
}
