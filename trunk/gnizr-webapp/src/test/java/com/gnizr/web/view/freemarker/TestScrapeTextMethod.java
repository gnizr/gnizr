package com.gnizr.web.view.freemarker;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import freemarker.template.SimpleScalar;

public class TestScrapeTextMethod extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testExec() throws Exception{
		List<Object> args = new ArrayList<Object>();
		SimpleScalar htmlCode = new SimpleScalar("<H1>hello<h3></H1> <br> <p>java programming");
		args.add(htmlCode);
		
		ScrapeTextMethod method = new ScrapeTextMethod();
		String output = (String)method.exec(args);
		assertNotNull(output);
		assertFalse(output.contains("h3"));
		assertFalse(output.contains("H1"));
		assertFalse(output.contains("<p>"));
	}
	

}
