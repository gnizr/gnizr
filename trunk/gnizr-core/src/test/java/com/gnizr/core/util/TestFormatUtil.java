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
	
	public void testTidyHTML() throws Exception{
		InputStream is = TestFormatUtil.class.getResourceAsStream("/TestFormatUtil-badhtml.html");
		int x = is.available();
		byte b[] = new byte[x];
		is.read(b);
		String content = new String(b);	
		String text = FormatUtil.tidyHTML(content, true,"utf8");
		assertFalse(text.contains("<HTML>"));
		assertTrue(text.contains("</p>"));
		assertTrue(text.contains("</ul>"));
		assertTrue(text.contains("</script>"));
	}
	
	public void testRemoveAbusiveTags() throws Exception{
		InputStream is = TestFormatUtil.class.getResourceAsStream("/TestFormatUtil-badhtml.html");
		int x = is.available();
		byte b[] = new byte[x];
		is.read(b);
		String content = new String(b);	
		String text = FormatUtil.tidyHTML(content, true,"utf8");
		text = FormatUtil.removeAbusiveTags(text);
		assertFalse(text.contains("<HTML>"));
		assertTrue(text.contains("</p>"));
		assertTrue(text.contains("</ul>"));
		assertFalse(text.contains("javascript"));
		assertFalse(text.contains("H1"));
		assertFalse(text.contains("/H1"));
		assertTrue(text.contains("H3"));
		assertFalse(text.contains("frame"));
		assertFalse(text.contains("iframe"));
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
