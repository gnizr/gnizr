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
package com.gnizr.db.dao.tag;

import java.util.List;

import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.tag.TagsParser;

import junit.framework.TestCase;

public class TestTagParser extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParse() throws Exception{	
		TagsParser tagParser = new TagsParser("aa \"aa\" \"java programming\" 雅虎香港  f f geo:loc=\"usa, china\"");
		List<String> tagList = tagParser.getTags();
		List<MachineTag> macList = tagParser.getMachineTags();
		assertEquals(9,tagList.size());
		assertEquals(1,macList.size());
	}
	
	public void testParse2() throws Exception{
		TagsParser tagParser = new TagsParser("   a  123 38.28 a geo:loc=\"usa\" for:user   ");
		List<String> tagList = tagParser.getTags();
		List<MachineTag> macList = tagParser.getMachineTags();
		assertEquals(6,tagList.size());
		assertEquals(2,macList.size());
	}
	
	public void testParse3() throws Exception{
		TagsParser tagParser = new TagsParser("abc,203 ,pop,,,, 11111.000","[\\s,]+");
		List<String> tagList = tagParser.getTags();
		tagList.contains("abc");
		tagList.contains("203");
		tagList.contains("11111.000");
		tagList.contains("pop");
		assertEquals(4,tagList.size());
	}
	
	public void testParse4() throws Exception{
		TagsParser tagParser = new TagsParser("tag:geonames:united_states gn:tag=geonames:21045,md");
		List<String> tags = tagParser.getTags();
		assertTrue(tags.contains("tag:geonames:united_states"));
		assertTrue(tags.contains("gn:tag=geonames:21045,md"));
		
		List<MachineTag> mtags = tagParser.getMachineTags();
		MachineTag m1 = mtags.get(0);
		assertEquals("geonames:united_states",m1.getValue());
		MachineTag m2 = mtags.get(1);
		assertEquals("geonames:21045,md",m2.getValue());
	}
}
