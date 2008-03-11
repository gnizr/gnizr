package com.gnizr.web.action.search;

import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.search.IndexStoreProfile;
import com.gnizr.core.search.SearchIndexManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;

public class TestIndexBookmark extends GnizrWebappTestBase {

	private IndexStoreProfile profile;
	private SearchIndexManager searchIndexManager;
	private BookmarkPager bookmarkPager;
	
	protected void setUp() throws Exception {
		super.setUp();
		profile = new IndexStoreProfile();
		profile.setDirectoryPath("target/search-data");
		profile.setOverwrite(true);
		
		searchIndexManager = new SearchIndexManager();
		searchIndexManager.setProfile(profile);
		searchIndexManager.init();
		
		bookmarkPager = new BookmarkPager(getGnizrDao());
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		searchIndexManager.destroy();
	}

	public void testGo() throws Exception{
		Map<Object, Object> session = new HashMap<Object, Object>();
		
		
		IndexBookmark action = new IndexBookmark();
		action.setBookmarkPager(bookmarkPager);
		action.setSearchIndexManager(searchIndexManager);
		action.setUserManager(new UserManager(getGnizrDao()));
		action.setUser(new User(1));
		action.setSession(session);
		
		String doc1Hash = "d1a8e491759cb30d11357c4776be9c66";
		Document d1 = searchIndexManager.findLeadDocument(doc1Hash);
		assertNull(d1);
		
		assertEquals(ActionSupport.SUCCESS, action.execute());
		Thread.sleep(10000);
		d1 = searchIndexManager.findLeadDocument(doc1Hash);
		assertNotNull(d1);
		
		assertNotNull(session.get("status"));
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestIndexBookmark.class.getResourceAsStream("/TestIndexBookmark-input.xml"));
	}

}
