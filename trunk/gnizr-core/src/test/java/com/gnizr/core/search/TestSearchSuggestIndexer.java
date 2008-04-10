package com.gnizr.core.search;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;

public class TestSearchSuggestIndexer extends GnizrCoreTestBase {

	private SearchIndexProfile profile;
	private SearchSuggestIndexer indexer;
	
	protected void setUp() throws Exception {
		super.setUp();		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testInit() throws CorruptIndexException, IOException {
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/testSearchSuggestIndexer-data");
		profile.setSearchSuggestDataFile("src/test/resources/dictionary/default.txt");
		profile.setSuggestPopularTagsEnabled(false);
		indexer = new SearchSuggestIndexer();
		indexer.setSearchIndexProfile(profile);
		indexer.init();
		IndexReader reader = indexer.openSuggestIndexReader();
		assertEquals(11, reader.numDocs());
	}

	public void testInit2() throws Exception{
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/search-data");		
		profile.setSuggestPopularTagsEnabled(true);
		indexer = new SearchSuggestIndexer();
		indexer.setGnizrDao(getGnizrDao());
		indexer.setSearchIndexProfile(profile);
		indexer.init();
		IndexReader reader = indexer.openSuggestIndexReader();
		assertEquals(4, reader.numDocs());
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSearchSuggestIndexer.class.getResourceAsStream("/TestSearchSuggestIndexer-input.xml"));
	}

}
