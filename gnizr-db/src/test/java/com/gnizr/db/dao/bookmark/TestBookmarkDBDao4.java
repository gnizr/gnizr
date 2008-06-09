package com.gnizr.db.dao.bookmark;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.GnizrDBTestBase;

public class TestBookmarkDBDao4 extends GnizrDBTestBase {

	private BookmarkDao bookmarkDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		bookmarkDao = new BookmarkDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkDBDao4.class.getResourceAsStream("/dbunit/bmarkdbdao/TestBookmarkDBDao4-input.xml"));
	}

	public void testGetPopularCommunityBookmarks() throws Exception{
		
		SimpleDateFormat dformat = new SimpleDateFormat("yyyy-MM-dd");
		Date start = dformat.parse("2007-03-01");
		Date end = dformat.parse("2007-04-24");
		
		List<Bookmark> bmarks = bookmarkDao.getPopularCommunityBookmarks(start,end,2);
		assertEquals(2,bmarks.size());
		Bookmark bm1 = bmarks.get(0);
		assertEquals(3,bm1.getLink().getId());
		Bookmark bm2 = bmarks.get(1);
		assertEquals(5,bm2.getLink().getId());
	}
	
}
