package com.gnizr.core.search;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TestOpenSearchDirectory extends TestCase {

	private List<String> servicesUrl;
	
	protected void setUp() throws Exception {
		super.setUp();
		String srv1 = TestOpenSearchDirectory.class.getResource("/TestOpenSearchDirectory-srv1-dsp.xml").toString();
		String srv2 = TestOpenSearchDirectory.class.getResource("/TestOpenSearchDirectory-srv2-dsp.xml").toString();
		servicesUrl = new ArrayList<String>();
		servicesUrl.add(srv1);
		servicesUrl.add(srv2);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetServices() throws Exception{
		OpenSearchDirectory osDirectory = new OpenSearchDirectory(servicesUrl);
		List<OpenSearchService> services = osDirectory.getServices();
		assertFalse(services.isEmpty());		
		OpenSearchService srv1 = services.get(0);
		OpenSearchService srv2 = services.get(1);
		
		assertEquals("Gnizr Community 2",srv1.getShortName());
		assertEquals("Search bookmarks in gnizr",srv1.getDescription());
		assertEquals(2,srv1.getTags().length);
		assertEquals("text/xml",srv1.getType());
		assertTrue(srv1.isSupportsPageBased());
		assertFalse(srv1.isSupportsIndexBased());
		assertTrue(srv1.isLoginRequired());
		assertEquals("http://gnizr.com/data/atom/community/search.action?queryString={searchTerms}&start={startPage}",srv1.getServiceUrlPattern());
		
		
		assertEquals("Gnizr Community",srv2.getShortName());
		assertEquals("Search bookmarks in gnizr",srv2.getDescription());
		assertEquals(2,srv2.getTags().length);
		assertTrue(srv2.isSupportsIndexBased());
		assertFalse(srv2.isSupportsPageBased());
		assertEquals("text/xml",srv2.getType());
		assertEquals("http://gnizr.com/data/atom/community/search.action?queryString={searchTerms}&page={startIndex}",srv2.getServiceUrlPattern());
	}

}
