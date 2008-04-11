package com.gnizr.core.search;

import java.io.File;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;

public class TestSearchTermSuggestion extends GnizrCoreTestBase {

	private SearchIndexProfile profile;
	private SearchSuggestIndexer indexer;
	private SearchTermSuggestion suggest;
	
	private SearchIndexManager manager;
	
	private BookmarkPager bookmarkPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/testSearchTermSuggestion");
		profile.setSearchSuggestDataFile("src/test/resources/dictionary/default.txt");
		
		manager = new SearchIndexManager(true);
		manager.setProfile(profile);
		manager.init();
		
		indexer = new SearchSuggestIndexer();
		indexer.setSearchIndexProfile(profile);
		indexer.init();
		suggest = new SearchTermSuggestion();
		suggest.setSearchSuggestIndexer(indexer);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		manager.destroy();
	}
	
	public void testSuggest() throws Exception{
		String[] results = suggest.suggest("test");
		assertNotNull(results);
		assertEquals(4,results.length);
		results = suggest.suggest("class:");
		assertEquals(4,results.length);		
	}
	
	public void testSuggestWithFreqFilter() throws Exception{
		bookmarkPager = new BookmarkPager(getGnizrDao());
		
		DaoResult<Bookmark> result = bookmarkPager.pageAllBookmark(new User(2));
		List<Bookmark> bmarks = result.getResult();
		for(Bookmark bm : bmarks){
			Document doc = DocumentCreator.createDocument(bm);			
			manager.addIndex(doc);
		}
		
		result = bookmarkPager.pageAllBookmark(new User(3));
		bmarks = result.getResult();
		
		for(Bookmark bm : bmarks){
			Document doc = DocumentCreator.createDocument(bm);
			manager.addIndex(doc);
		}
		while(manager.isIndexProcessActive() == true){
			Thread.sleep(5000);
		}
		
		File indexDir = manager.getIndexDirectory();
		IndexReader indexReader = IndexReader.open(indexDir);
		String[] words = suggest.suggest("test", indexReader, DocumentCreator.FIELD_TEXT);
		assertEquals(0,words.length);
	
		words = suggest.suggest("full",indexReader,DocumentCreator.FIELD_TEXT);
		assertEquals(1,words.length);
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSearchTermSuggestion.class.getResourceAsStream("/TestSearchTermSuggestion-input.xml"));
	}

}
