package com.gnizr.core.search;

import junit.framework.TestCase;

public class TestSearchTermSuggestion extends TestCase {

	private SearchIndexProfile profile;
	private SearchSuggestIndexer indexer;
	private SearchTermSuggestion suggest;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/search-data");
		profile.setSearchSuggestDataFile("src/test/resources/dictionary/default.txt");
		indexer = new SearchSuggestIndexer();
		indexer.setSearchIndexProfile(profile);
		indexer.init();
		suggest = new SearchTermSuggestion();
		suggest.setSearchSuggestIndexer(indexer);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testSuggest() throws Exception{
		String[] results = suggest.suggest("test");
		assertNotNull(results);
		assertEquals(4,results.length);
		results = suggest.suggest("class:");
		assertEquals(4,results.length);
	}

}
