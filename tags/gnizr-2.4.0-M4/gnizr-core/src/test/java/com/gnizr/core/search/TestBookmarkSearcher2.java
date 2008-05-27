package com.gnizr.core.search;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;

public class TestBookmarkSearcher2 extends GnizrCoreTestBase {
	private static final Logger logger = Logger.getLogger(TestBookmarkSearcher2.class);
	private SearchIndexProfile profile;
	private SearchIndexManager searchIndexManager;
	
	private BookmarkPager bookmarkPager;
	
	private BookmarkSearcher searcher;

	protected void setUp() throws Exception {
		super.setUp();
		
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/testBookmarkSearch-data-2");
		
		searchIndexManager = new SearchIndexManager(true);
		searchIndexManager.setProfile(profile);
		searchIndexManager.init();		
		
		searcher = new BookmarkSearcher();
		searcher.setSearchIndexManager(searchIndexManager);
		searcher.init();
		
		bookmarkPager = new BookmarkPager(getGnizrDao());
		
		DaoResult<Bookmark> result = bookmarkPager.pageAllBookmark(new User(2));
		List<Bookmark> bmarks = result.getResult();
		for(Bookmark bm : bmarks){
			searchIndexManager.addIndex(DocumentCreator.createDocument(bm));
		}		
		Thread.sleep(5000);
		result = bookmarkPager.pageAllBookmark(new User(3));
		bmarks = result.getResult();
		for(Bookmark bm : bmarks){
			searchIndexManager.addIndex(DocumentCreator.createDocument(bm));
		}
		Thread.sleep(5000);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		searchIndexManager.destroy();
	}

	public void testSearchUser() throws Exception{
		String idxPath = searchIndexManager.getIndexDirectory().getAbsolutePath();
		if(IndexReader.isLocked(idxPath)){
			logger.debug("idx locked: " + idxPath);
		}else{
			logger.debug("idx is not unlocked: " + idxPath);
		}
		DaoResult<BookmarkDoc> result = searcher.searchUser("MySQL","hchen1",0,10);
		assertNotNull(result);
		assertEquals(1,result.getSize());
		assertEquals(1,result.getResult().size());
		BookmarkDoc doc = result.getResult().get(0);
		assertEquals("hchen1",doc.getUsername());
		assertEquals(3,doc.getBookmarkId());
		assertNotNull(doc.getTitle());
		assertNull(doc.getNotes());
		assertNotNull(doc.getUrl());
		assertNotNull(doc.getUrlHash());
		
		result = searcher.searchUser("Wii machine", "hchen1", 0, 10);
		assertEquals(0,result.getSize());
		assertEquals(0,result.getResult().size());
		
		result = searcher.searchUser("Wii machine", "joe", 0, 10);
		assertEquals(1,result.getSize());
		assertEquals(1,result.getResult().size());
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkSearcher2.class.getResourceAsStream("/TestBookmarkSearcher-input.xml"));
	}

}
