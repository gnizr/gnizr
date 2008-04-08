package com.gnizr.core.search;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;

public class TestSearchSuggestIndexer extends TestCase {

	private SearchIndexProfile profile;
	private SearchSuggestIndexer indexer;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/search-data");
		profile.setSearchSuggestDataFile("src/test/resources/dictionary/default.txt");
		indexer = new SearchSuggestIndexer();
		indexer.setSearchIndexProfile(profile);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws CorruptIndexException, IOException {
		indexer.init();
		File indexDir = indexer.getSuggestIndexDirectory();
		assertNotNull(indexDir);
		IndexReader reader = IndexReader.open(indexDir);
		assertEquals(8, reader.numDocs());
	}

}
