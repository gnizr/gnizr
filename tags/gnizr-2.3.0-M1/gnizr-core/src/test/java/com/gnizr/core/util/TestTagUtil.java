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
package com.gnizr.core.util;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.gnizr.db.dao.UserTag;

public class TestTagUtil extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testMakeSafeTagString() throws Exception{
		
		String s = TagUtil.makeSafeTagString(null);
		assertNull(s);
		
		s = TagUtil.makeSafeTagString("java");
		assertEquals("java",s);
		
		s = TagUtil.makeSafeTagString("java programming");
		assertEquals("java_programming",s);
		
		s = TagUtil.makeSafeTagString("abc ..            .. 123,");
		assertEquals("abc_.._.._123,",s);
		
		s = TagUtil.makeSafeTagString("a&b&c");
		assertEquals("abc",s);
		
		s = TagUtil.makeSafeTagString("/");
		assertEquals("",s);
		
		s = TagUtil.makeSafeTagString("./.");
		assertEquals("",s);
		
		s = TagUtil.makeSafeTagString("..");
		assertEquals("",s);
		
		s = TagUtil.makeSafeTagString(".java");
		assertEquals(".java",s);
		
		s = TagUtil.makeSafeTagString("aa/");
		assertEquals("aa",s);
		
		s = TagUtil.makeSafeTagString("ac/dc");
		assertEquals("ac/dc",s);
		
		s = TagUtil.makeSafeTagString("add\\add\\poo");
		assertEquals("addaddpoo",s);
		
		s = TagUtil.makeSafeTagString("12%");
		assertEquals("12",s);
		
		s = TagUtil.makeSafeTagString("ff??ff??");
		assertEquals("ffff",s);
		
		s = TagUtil.makeSafeTagString("12%?ff&a");
		assertEquals("12ffa",s);
	}
	
	public void testIsPrefixUserTag() throws Exception{
		boolean f = false;
		
		f = TagUtil.isPrefixedUserTag("gn:tag=hchen1/java");
		assertTrue(f);
		
		f = TagUtil.isPrefixedUserTag("tag:hchen1/java:foo");
		assertTrue(f);
		
		f = TagUtil.isPrefixedUserTag("foo:tag=jd/jfd");
		assertFalse(f);
		
		f = TagUtil.isPrefixedUserTag("tag:java");
		assertTrue(f);
	}
	
	public void testParsePrefixUserTag() throws Exception{
		UserTag userTag = TagUtil.parsePrefixedUserTag("gn:tag=hchen1/java");
		assertNotNull(userTag);
		
		assertEquals("java",userTag.getTag().getLabel());
		assertEquals("hchen1",userTag.getUser().getUsername());
		
		userTag = TagUtil.parsePrefixedUserTag("tag:hchen1/java:program");
		assertEquals("java:program",userTag.getTag().getLabel());
		assertEquals("hchen1",userTag.getUser().getUsername());
		
		userTag = TagUtil.parsePrefixedUserTag("tag:java");
		assertEquals("java", userTag.getTag().getLabel());
		assertTrue((userTag.getUser().getId() <= 0));
		assertNull(userTag.getUser().getUsername());
	}
	
	public void testSystemTags() throws Exception{
		List<String> tags = new ArrayList<String>();
		tags.add("folder:foo");
		tags.add("gn:folder=abc..fkf");
		tags.add("889889");
		tags.add("subscribe:this");
		tags.add("gn:subscribe=this");
		tags.add("gn:subscribe=that");
		tags.add("for:hchen1");
		tags.add("gn:for=jofjds");
		
		List<String> sysTags = TagUtil.systemTags(tags);
		assertEquals(6,sysTags.size());
		
	}
	
	public void testMapUnderscoreToSpace() throws Exception{
		String s = TagUtil.mapUnderscoreToSpace("java_programming");
		assertEquals("java programming",s);
		
		s = TagUtil.mapUnderscoreToSpace("loc:java___programming");
		assertEquals("loc:java   programming",s);
	}
	

}
