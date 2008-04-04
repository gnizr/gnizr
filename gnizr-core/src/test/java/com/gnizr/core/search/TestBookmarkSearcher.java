package com.gnizr.core.search;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.User;

public class TestBookmarkSearcher extends GnizrCoreTestBase {

	private SearchIndexProfile profile;
	private SearchIndexManager searchIndexManager;
	
	private BookmarkPager bookmarkPager;
	
	private BookmarkSearcher searcher;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		profile = new SearchIndexProfile();
		profile.setSearchIndexDirectory("target/search-data");
		profile.setOverwrite(true);
		
		searchIndexManager = new SearchIndexManager();
		searchIndexManager.setProfile(profile);
		searchIndexManager.init();
		
		searcher = new BookmarkSearcher();
		searcher.setSearchIndexProfile(profile);
		searcher.init();
		
		bookmarkPager = new BookmarkPager(getGnizrDao());
		
		DaoResult<Bookmark> result = bookmarkPager.pageAllBookmark(new User(2));
		List<Bookmark> bmarks = result.getResult();
		for(Bookmark bm : bmarks){
			searchIndexManager.addIndex(DocumentCreator.createDocument(bm));
		}
		
		result = bookmarkPager.pageAllBookmark(new User(3));
		bmarks = result.getResult();
		for(Bookmark bm : bmarks){
			searchIndexManager.addIndex(DocumentCreator.createDocument(bm));
		}

		
		while(searchIndexManager.isIndexProcessActive() == true){
			Thread.sleep(5000);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		searchIndexManager.destroy();
	}

	public void testSearchAll() throws Exception{
		DaoResult<BookmarkDoc> result = searcher.searchAll("MySQL +\"Part 2\"",0,10);
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
		
		result = searcher.searchAll("MySQL Security",0,10);
		assertEquals(3,result.getSize());
		
		result = searcher.searchAll("mysql security",2,10);
		assertEquals(3,result.getSize());
		assertEquals(1,result.getResult().size());
		
		result = searcher.searchAll("security mysql geo", 0, 1);
		assertEquals(4,result.getSize());
		assertEquals(1,result.getResult().size());
		
		result = searcher.searchAll("security mysql geo", 1, 1);
		assertEquals(4,result.getSize());
		assertEquals(1,result.getResult().size());
		
		result = searcher.searchAll("security mysql geo", 2, 1);
		assertEquals(4,result.getSize());
		assertEquals(1,result.getResult().size());
		
		result = searcher.searchAll("security mysql geo", 3, 1);
		assertEquals(4,result.getSize());
		assertEquals(1,result.getResult().size());
		
		result = searcher.searchAll("security mysql geo", 4, 1);
		assertEquals(4,result.getSize());
		assertEquals(0,result.getResult().size());
	}

	public void testSearchUser() throws Exception{
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
		return new FlatXmlDataSet(TestBookmarkSearcher.class.getResourceAsStream("/TestBookmarkSearcher-input.xml"));
	}

}
