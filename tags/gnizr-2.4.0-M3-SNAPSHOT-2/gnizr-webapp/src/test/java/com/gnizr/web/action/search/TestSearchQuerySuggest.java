package com.gnizr.web.action.search;

import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.search.DocumentCreator;
import com.gnizr.core.search.SearchIndexManager;
import com.gnizr.core.search.SearchIndexProfile;
import com.gnizr.core.search.SearchSuggestIndexer;
import com.gnizr.core.search.SearchTermSuggestion;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestSearchQuerySuggest extends GnizrWebappTestBase {

	private SearchIndexProfile profile;
	private SearchSuggestIndexer indexer;
	private SearchTermSuggestion searchTermSuggestion;
	private SearchIndexManager searchIndexManager;
	private SearchQuerySuggest action;
	private BookmarkPager bookmarkPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/testSearchQuerySuggest");
		profile.setSearchSuggestDataFile("src/test/resources/dictionary/test-searchquery-default.txt");
		
		searchIndexManager = new SearchIndexManager(true);
		searchIndexManager.setProfile(profile);
		searchIndexManager.init();
		
		indexer = new SearchSuggestIndexer();
		indexer.setSearchIndexProfile(profile);
		indexer.init();
		
		searchTermSuggestion = new SearchTermSuggestion();
		searchTermSuggestion.setSearchSuggestIndexer(indexer);
		
		action = new SearchQuerySuggest();
		action.setSearchIndexManager(searchIndexManager);
		action.setSearchTermSuggestion(searchTermSuggestion);

		bookmarkPager = new BookmarkPager(getGnizrDao());
		
		DaoResult<Bookmark> result = bookmarkPager.pageAllBookmark(new User(2));
		List<Bookmark> bmarks = result.getResult();
		for(Bookmark bm : bmarks){
			Document doc = DocumentCreator.createDocument(bm);			
			searchIndexManager.addIndex(doc);
		}
		
		result = bookmarkPager.pageAllBookmark(new User(3));
		bmarks = result.getResult();
		
		for(Bookmark bm : bmarks){
			Document doc = DocumentCreator.createDocument(bm);
			searchIndexManager.addIndex(doc);
		}
		
		Thread.sleep(10000);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void test1() throws Exception{
		Map<String,String> keywords  = null;
		String okay = null;
		
		action.setQ("full");
		okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		keywords = action.getKeywords();
		assertEquals(1,keywords.size());	
	}
	
	public void test2() throws Exception{
		Map<String,String> keywords  = null;
		String okay = null;
		
		action.setQ("text:full");
		okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		keywords = action.getKeywords();
		assertEquals(1,keywords.size());	
	}
	
	public void test3() throws Exception{
		Map<String,String> keywords  = null;
		String okay = null;
		
		action.setQ("tag:class");
		okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		keywords = action.getKeywords();
		assertEquals(1,keywords.size());	
	}
	
	public void test4() throws Exception{
		Map<String,String> keywords  = null;
		String okay = null;
		
		action.setQ("tag:class\\:a");
		okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		keywords = action.getKeywords();
		assertEquals(1,keywords.size());	
		boolean contains = keywords.keySet().contains("tag:class\\:animal");
		assertTrue(contains);
	}
	
	public void test5() throws Exception{
		Map<String,String> keywords  = null;
		String okay = null;
		
		action.setQ("tag:class\\:w");
		okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		keywords = action.getKeywords();
		assertEquals(0,keywords.size());	
	}
	
	public void test6() throws Exception{
		Map<String,String> keywords  = null;
		String okay = null;
		
		action.setQ("class");
		okay = action.execute();
		assertEquals(ActionSupport.SUCCESS,okay);
		keywords = action.getKeywords();
		assertEquals(0,keywords.size());	
	}
	

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestSearchQuerySuggest.class.getResourceAsStream("/TestSearchQuerySuggest-input.xml"));
	}
	
}
