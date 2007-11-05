package com.gnizr.core.web.action.search;

import java.util.List;

import junit.framework.TestCase;

public class TestOpenSearchDirectory extends TestCase {

	private String[] servicesUrl;
	
	protected void setUp() throws Exception {
		super.setUp();
		String srv1 = TestOpenSearchDirectory.class.getResource("/TestOpenSearchDirectory-srv1-dsp.xml").toString();
		String srv2 = TestOpenSearchDirectory.class.getResource("/TestOpenSearchDirectory-srv2-dsp.xml").toString();
		servicesUrl = new String[]{srv1,srv2};
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetServices() throws Exception{
		OpenSearchDirectory osDirectory = new OpenSearchDirectory(servicesUrl);
		List<OpenSearchService> services = osDirectory.getServices();
		assertFalse(services.isEmpty());		
	}

}
