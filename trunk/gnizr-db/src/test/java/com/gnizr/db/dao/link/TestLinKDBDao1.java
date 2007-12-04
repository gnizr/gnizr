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
package com.gnizr.db.dao.link;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.user.TestUserDBDao1;
import com.gnizr.db.vocab.MIMEType;

public class TestLinKDBDao1 extends GnizrDBTestBase {
	
	private LinkDBDao linkDao;
	public TestLinKDBDao1(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		linkDao = new LinkDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestUserDBDao1.class.getResourceAsStream("/dbunit/linkdbdao/TestLinkDBDao1-input.xml"));
	}
	
	public void testCreateLink() throws Exception{
		Link link = new Link();
		link.setMimeTypeId(MIMEType.TEXT_HTML);
		link.setUrl("http://hchen1.com");
		int id = linkDao.createLink(link);
		assertTrue((id>0));
		
		Link linkCopy = linkDao.getLink(id);
		assertNotNull(linkCopy);
		assertEquals(link.getUrl(),linkCopy.getUrl());
		
		assertTrue(linkDao.deleteLink(id));
		assertFalse(linkDao.deleteLink(id));		
	}
	
	public void testDeleteLink() throws Exception{
		Link link = linkDao.getLink((202));
		assertTrue(linkDao.deleteLink(link.getId()));
		assertFalse(linkDao.deleteLink(link.getId()));
	}
	
	public void testGetLink() throws Exception{
		Link link = linkDao.getLink((202));
		assertNotNull(link);
		assertEquals(1003,link.getMimeTypeId());
		assertEquals("http://www.csee.umbc.edu/~finin/",link.getUrl());
		assertEquals("2c96b19ed544ab90b24830493a5efdd3",link.getUrlHash());
		assertEquals(link.getUrlHash(),Link.computeUrlHash(link.getUrl()));
	}
	
	public void testFindLink() throws Exception{
		List<Link> links = linkDao.findLink("http://foo.com");
		assertNotNull(links);
		assertEquals(0,links.size());
		
		links = linkDao.findLink("http://www.csee.umbc.edu/~finin/");
		assertNotNull(links);
		assertEquals(1,links.size());
		assertEquals(1,links.get(0).getCount());
	}

	
	public void testFindLinkUrlHash() throws Exception{
		String urlHash = Link.computeUrlHash("http://www.housingmaps.com/");
		List<Link> links = linkDao.findLinkByUrlHash(urlHash);
		assertEquals(1,links.size());
		Link l = links.get(0);
		assertEquals(3,l.getCount());
		assertEquals(0,l.getMimeTypeId());
	}
	
	
	public void testUpdateLink() throws Exception{
		Link link1 = linkDao.getLink(1);
		link1.setMimeTypeId(1003);
		link1.setUrl("http://foo.bar.com");
		
		assertTrue(linkDao.updateLink(link1));
		
		link1 = linkDao.getLink(1);
		assertEquals(1003,link1.getMimeTypeId());
		assertEquals("http://foo.bar.com",link1.getUrl());
	}
}
