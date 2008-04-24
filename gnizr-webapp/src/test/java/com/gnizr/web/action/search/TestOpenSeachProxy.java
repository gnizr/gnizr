package com.gnizr.web.action.search;

import junit.framework.TestCase;
import net.sf.json.JSONObject;

import com.opensymphony.xwork.ActionSupport;

public class TestOpenSeachProxy extends TestCase {

	private String url = null;
	private OpenSearchProxy action;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		url = TestOpenSeachProxy.class.getResource("/community-search-output.xml").toString();
		action = new OpenSearchProxy();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGo() throws Exception{
		action.setSearchUrl(url);
		assertEquals(ActionSupport.SUCCESS,action.execute());
		JSONObject jsonObj = action.getJsonResult();
		assertNotNull(jsonObj.toString());		
	}
	
	

}
