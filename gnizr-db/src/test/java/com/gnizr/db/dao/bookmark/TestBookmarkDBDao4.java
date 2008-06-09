package com.gnizr.db.dao.bookmark;

import java.util.GregorianCalendar;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

public class TestBookmarkDBDao4 extends GnizrDBTestBase {

	private BookmarkDao bookmarkDao;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		bookmarkDao = new BookmarkDBDao(getDataSource());
		for(int i = 1; i < 6; i++){
			for(int j = 1; j < 4; j++){
				Bookmark bm1= new Bookmark();
				bm1.setLink(new Link(i));
				bm1.setTitle("BM_TITLE_l"+i+"_u"+j);
				bm1.setUser(new User(j));
				bm1.setCreatedOn(GregorianCalendar.getInstance().getTime());
				bm1.setLastUpdated(GregorianCalendar.getInstance().getTime());
				bookmarkDao.createBookmark(bm1);
				Thread.sleep(100);
				if((j % 2)>0){
					Bookmark bm2= new Bookmark();
					bm2.setLink(new Link(8));
					bm2.setTitle("BM_TITLE_l8_u"+j);
					bm2.setUser(new User(j));
					bm2.setCreatedOn(GregorianCalendar.getInstance().getTime());
					bm2.setLastUpdated(GregorianCalendar.getInstance().getTime());
					bookmarkDao.createBookmark(bm2);
					Thread.sleep(100);
				}
			}
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkDBDao4.class.getResourceAsStream("/dbunit/bmarkdbdao/TestBookmarkDBDao4-input.xml"));
	}

	public void testGetPopularCommunityBookmarks() throws Exception{
		List<Bookmark> bmarks = bookmarkDao.getPopularCommunityBookmarks(1,2);
		assertEquals(2,bmarks.size());
		Bookmark bm1 = bmarks.get(0);
		assertEquals(2,bm1.getLink().getId());
		
		bmarks = bookmarkDao.getPopularCommunityBookmarks(1,4);
		assertEquals(4,bmarks.size());
	}
	
}
