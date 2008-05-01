package com.gnizr.core.util;

import java.io.InputStream;

import junit.framework.TestCase;

public class TestFormatUtil extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testExtractTextFromHtml() throws Exception{
		InputStream is = TestFormatUtil.class.getResourceAsStream("/TestFormatUtil-dummyhtml.html");
		int x = is.available();
		byte b[] = new byte[x];
		is.read(b);
		String content = new String(b);
	
		String text = FormatUtil.extractTextFromHtml(content);
		assertFalse(text.contains("<HTML>"));
	}
	
	
	public void testRemoveLongWord() throws Exception{
		String t = "jajdjdjdjdjjdjdjdjdjdjd 123 djdjdjdjdjdjdjjdd";
		assertEquals("123",FormatUtil.removeLongWord(t,4).trim());
	}
	
	public void testHighlightStarEnclosedText() throws Exception {
		String t = "hello *world* *abc*";
		String t2 = FormatUtil.highlightStarEnclosedText(t,"<b>", "</b>");
		assertEquals("hello <b>world</b> <b>abc</b>",t2);
		
		t = "*dk* abc";
		t2 = FormatUtil.highlightStarEnclosedText(t,"<b>", "</b>");
		assertEquals("<b>dk</b> abc",t2);
	}
}
