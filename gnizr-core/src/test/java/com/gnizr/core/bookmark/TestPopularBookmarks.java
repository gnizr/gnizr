package com.gnizr.core.bookmark;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.dao.Bookmark;

public class TestPopularBookmarks extends GnizrCoreTestBase {

	private PopularBookmarks popularBookmarks;
	
	protected void setUp() throws Exception {
		super.setUp();
		popularBookmarks = new PopularBookmarks(getGnizrDao());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestPopularBookmarks.class
				.getResourceAsStream("/TestPopularBookmarks-input.xml"));
	}

	public void testTop10AllTime() throws Exception{
		List<Bookmark> bookmark = popularBookmarks.getTop10AllTime();
		assertEquals(10,bookmark.size());
	}
	
}
