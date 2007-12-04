package com.gnizr.core.web.action.search;

import java.util.List;

import net.sf.json.JSON;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.tag.TagManager;
import com.gnizr.core.tag.TagPager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.opensymphony.xwork.ActionSupport;

public class TestSuggestSearchTags extends GnizrWebappTestBase {

	private SuggestSearchTags action;	
	private TagManager tagManager;
	private TagPager tagPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		tagManager = new TagManager(getGnizrDao());
		tagPager = new TagPager(getGnizrDao());
		action = new SuggestSearchTags();
		action.setTagPager(tagPager);
		action.setTagManager(tagManager);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSuggestSearchTags.class.getResourceAsStream("/TestSuggestSearchTags-input.xml"));
	}

	public void testGoEmptySearchTerm() throws Exception{
		action.setQ("");
		String okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		List<String> tags = action.getSkosRelatedTags();
		assertTrue(tags.isEmpty());
		JSON json = action.getJsonResult();
		assertEquals("{}",json.toString());
	}
	
	public void testGo() throws Exception{
		action.setQ("games");
		String okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		List<String> tags= action.getSkosRelatedTags();
		assertFalse(tags.isEmpty());
		assertEquals(1,tags.size());		
		assertTrue(tags.contains("online"));
		System.out.println(action.getJsonResult().toString());
		
		tags = action.getSkosNarrowerTags();
		assertEquals(3,tags.size());
		assertTrue(tags.contains("boardgames"));
		assertTrue(tags.contains("videogame"));
		assertTrue(tags.contains("wii"));
		
		tags = action.getSkosBroaderTags();
		assertEquals(0,tags.size());
	}
	
	
}
