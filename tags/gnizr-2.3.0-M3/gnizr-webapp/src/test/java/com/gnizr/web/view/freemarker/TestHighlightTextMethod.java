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
package com.gnizr.web.view.freemarker;

import java.util.ArrayList;
import java.util.List;

import com.gnizr.web.view.freemarker.HighlightTextMethod;

import junit.framework.TestCase;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;

public class TestHighlightTextMethod extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testExec() throws Exception{
		List args = new ArrayList();
		SimpleSequence texts = new SimpleSequence();
		texts.add("abc");
	
		SimpleSequence cssClass = new SimpleSequence();
		cssClass.add("matched_text");
		cssClass.add("search-term");
		
		//args.add(new SimpleScalar("abc 123 <img src=\"http://foo.abc.com/abcworld\">abc</a> 123 kkkabckk"));
		args.add(new SimpleScalar("<span style=\"font-weight: bold;\"><span class=\"Title\"></span>Mikes writes: " +
				"</span>A lengthy article in Spiegel explores the possibility that global warming might make life on Earth better, " +
				"not just for humans, but all species. The article argues that 'worst-case scenarios' " +
				"are often the result of inaccurate <strong>simulations</strong> " +
				"made in the 1980s. While climate change is a reality, as far as the " +
				"article is concerned, some planning and forethought may mean that more " +
				"benefits than drawbacks will result from higher temperatures. <font color=\"red\">From " +
						"the article</font>:'The medical benefits of higher average temperatures have also " +
						"been ignored. According to Richard Tol, an environmental economist, &quot;warming " +
						"temperatures will mean that in 2050 there will be about 40,000 fewer deaths " +
						"in Germany attributable to cold-related illnesses like the flu.&quot;<ul>" + 
    "<li> Another widespread fear about global warming -- that it will cause super-storms that could devastate" +
    		"towns and villages with unprecedented fury -- also appears to be unfounded. </li>" + 
    "<li>Current long-term simulations, at any rate, do not suggest that such a trend will in fact materialize.'&quot;</li></ul>"));
		args.add(texts);
		args.add(new SimpleScalar("SPAN"));
		args.add(cssClass);

		HighlightTextMethod method = new HighlightTextMethod();
		String output = (String) method.exec(args);
		assertNotNull(output);
		//System.out.println(output);		
	}
	
	@SuppressWarnings("unchecked")
	public void testExec2() throws Exception{
		List args = new ArrayList();
		SimpleSequence texts = new SimpleSequence();
		texts.add("abc");
	
		SimpleSequence cssClass = new SimpleSequence();
		cssClass.add("matched_text");
		cssClass.add("search-term");
		args.add(new SimpleScalar("<img src=\"http://abc\">abc</img>jfkds abc ABC aBC fjdksj29<span>abc</span></a>"));
		args.add(texts);
		args.add(new SimpleScalar("SPAN"));
		args.add(cssClass);

		HighlightTextMethod method = new HighlightTextMethod();
		String output = (String) method.exec(args);
		assertNotNull(output);
		//System.out.println(output);			
	}
	
}
