/*
 * gnizr is a trademark of Image Matters LLC in the United States.
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either expressed or implied. See the License
 * for the specific language governing rights and limitations under the License.
 * 
 * The Initial Contributor of the Original Code is Image Matters LLC.
 * Portions created by the Initial Contributor are Copyright (C) 2007
 * Image Matters LLC. All Rights Reserved.
 */
package com.gnizr.core.bookmark;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.bookmark.GeometryMarkerDao;
import com.gnizr.db.dao.folder.FolderDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.user.UserDao;

public class TestBookmarkManager extends GnizrCoreTestBase {

	private BookmarkManager manager;
	private BookmarkDao bookmarkDao;
	private FolderDao folderDao;
	private TagDao tagDao;
	private UserDao userDao;
	private LinkDao linkDao;
	private GeometryMarkerDao geomMarkerDao;

	protected void setUp() throws Exception {
		super.setUp();
		manager = new BookmarkManager(getGnizrDao());
		bookmarkDao = getGnizrDao().getBookmarkDao();
		tagDao = getGnizrDao().getTagDao();
		userDao = getGnizrDao().getUserDao();
		linkDao = getGnizrDao().getLinkDao();
		folderDao = getGnizrDao().getFolderDao();
		geomMarkerDao = getGnizrDao().getGeometryMarkerDao();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestBookmarkManager.class
				.getResourceAsStream("/TestBookmarkManager-input.xml"));
	}

	public void testAddBookmark() throws Exception {
		User gUser = new User("hchen1");
		Bookmark gBookmark = new Bookmark();
		gBookmark.setUser(gUser);
		gBookmark.setLink(new Link("http://www.umbc.edu"));
		gBookmark.setTitle("UMBC");
		gBookmark.setNotes("UMBC homepage");		
		int id = manager.addBookmark(gBookmark);
		assertTrue(id > 0);
		BookmarkDao bookmarkDao = getGnizrDao().getBookmarkDao();		
		
		GnizrDaoUtil.fillObject(bookmarkDao,userDao,linkDao,gBookmark);
		// ensure we can delete and add the same bookmark
		assertTrue(manager.deleteBookmark(gBookmark));
		assertTrue((manager.addBookmark(gBookmark)>0));
	}
		
	public void testDeleteBookmark() throws Exception{
		User user = new User(2);
		Link link = new Link(204);
		Bookmark bookmark = new Bookmark(user,link);
		bookmark.setTitle("webwork learning");
		bookmark.setTags("webwork programming learning");
		assertTrue((manager.addBookmark(bookmark)>0));
		
		bookmark = bookmarkDao.findBookmark(user, link).get(0);
		assertNotNull(bookmark);
		
		UserTag ut = new UserTag("hchen1","learning");
		GnizrDaoUtil.fillId(tagDao,userDao,ut);
		GnizrDaoUtil.fillObject(tagDao, userDao, ut);
		assertEquals((1),ut.getCount());
		assertEquals((1),ut.getTag().getCount());
		
		LinkTag lt = new LinkTag("http://www.learntechnology.net/webwork-crud-lm.do","learning");
		GnizrDaoUtil.fillId(tagDao, linkDao,lt);
		GnizrDaoUtil.fillObject(tagDao, linkDao,lt);
		assertEquals((1),lt.getCount());
		assertEquals((1),lt.getTag().getCount());
		
		assertTrue(manager.deleteBookmark(bookmark));

		List<Bookmark> r = bookmarkDao.findBookmark(user, link);
		assertTrue(r.isEmpty());
		GnizrDaoUtil.fillId(tagDao,userDao,ut);
		GnizrDaoUtil.fillObject(tagDao, userDao, ut);
		assertEquals((0),ut.getCount());
		assertEquals((0),ut.getTag().getCount());
			
		GnizrDaoUtil.fillId(tagDao, linkDao,lt);
		GnizrDaoUtil.fillObject(tagDao, linkDao,lt);
		assertEquals((0),lt.getCount());
		assertEquals((0),lt.getTag().getCount());		
	}
	
	
	public void testUpdateBookmark() throws Exception{
		Bookmark bm = bookmarkDao.getBookmark(300);
		assertEquals(202,bm.getLink().getId());
		
		// change the link that this bookmark ref
		Link link = new Link(204);
		bm.setLink(link);
		
		// change tag counts
		bm.setTags("foobar");
		
		// change the URL link that this bookmark reference
		Link ln = new Link("http://newlink.com");
		bm.setLink(ln);
		
		assertTrue(manager.updateBookmark(bm));
		
		bm = bookmarkDao.getBookmark(300);
		GnizrDaoUtil.fillObject(bookmarkDao, userDao, linkDao, bm);
		assertEquals("foobar",bm.getTags());
		Tag cnn = tagDao.findTag("cnn").get(0);
		assertEquals(0,cnn.getCount());
		Tag news = tagDao.findTag("news").get(0);
		assertEquals(0,news.getCount());
		Tag foobar = tagDao.findTag("foobar").get(0);
		assertEquals(1,foobar.getCount());
		UserTag foobar_u = tagDao.findUserTag(new User(1), foobar).get(0);
		assertEquals(1,foobar_u.getCount());
		
		Link bmLink = bm.getLink();
		assertEquals(ln.getUrl(),bmLink.getUrl());
		assertEquals(Link.computeUrlHash(ln.getUrl()),bmLink.getUrlHash());
		
		// change bookmark's notes
		bm.setNotes("hello world");
		// erase the id of the link id. make sure
		// BookmarkManager knows how to work out id from
		// the URL of the link.
		bm.getLink().setId(-1);
		assertTrue(manager.updateBookmark(bm));
		bm = bookmarkDao.getBookmark(300);
		GnizrDaoUtil.fillObject(bookmarkDao, userDao, linkDao, bm);
		assertEquals("hello world",bm.getNotes());
		assertEquals(ln.getUrl(),bm.getLink().getUrl());
		assertEquals(Link.computeUrlHash(ln.getUrl()),bm.getLink().getUrlHash());
		assertTrue((bm.getLink().getId() > 0));
	}
	
	/**
	 * Tests the count of indivdiual user tags are properly updated
	 * after each update bookmark transaction. 
	 * @throws Exception
	 */
	public void testUpdateBookmark2() throws Exception{
		Bookmark bm = manager.getBookmark(300);
		bm.setTags("news foobar abdc");
		assertTrue(manager.updateBookmark(bm));
		
		UserTag ut = tagDao.findUserTag(bm.getUser(),new Tag(5)).get(0);
		assertEquals(1,ut.getCount());
		
		Tag foobar = tagDao.findTag("foobar").get(0);
		assertEquals(1,foobar.getCount());
		Tag abdc = tagDao.findTag("abdc").get(0);
		assertEquals(1,abdc.getCount());
		
		Tag cnn = tagDao.findTag("cnn").get(0);
		assertEquals(0,cnn.getCount());
		
		UserTag userCnn = tagDao.findUserTag(bm.getUser(),cnn).get(0);
		assertEquals(0,userCnn.getCount());
		
		UserTag userFoobar = tagDao.findUserTag(bm.getUser(),foobar).get(0);
		assertEquals(1,userFoobar.getCount());
		
		bm.setTags("");
		assertTrue(manager.updateBookmark(bm));
		
		ut = tagDao.findUserTag(bm.getUser(),new Tag(5)).get(0);
		assertEquals(0,ut.getCount());
	}
	

	public void testGetBookmark() throws Exception{
		Bookmark bm = manager.getBookmark(300);
		assertNotNull(bm.getTitle());
		assertNotNull(bm.getUser().getUsername());
		assertNotNull(bm.getLink().getUrl());
		assertEquals("gnizr", bm.getUser().getFullname());
	}
	
	public void testGetBookmarkId() throws Exception{
		String url = "http://zirr.us/";
		int id = manager.getBookmarkId(new User("gnizr"),url);
		assertEquals(300,id);
		
		id = manager.getBookmarkId(new User(1), url);
		assertEquals(300,id);
	}
	
	public void testUpdateBookmark3() throws Exception{
		Bookmark bm1 = new Bookmark(new User(2),new Link(204));
		bm1.setTitle("bookmark_1_title");
		bm1.setTags("tag1 tag2");
		
		Bookmark bm2 = new Bookmark(new User(3),new Link(204));
		bm2.setTitle("bookmark_2_title");
		bm2.setTags("tag1");
		
		int id1 = manager.addBookmark(bm1);
		int id2 = manager.addBookmark(bm2);
		assertTrue((id1 > 0));
		assertTrue((id2 > 0));
		
		bm1 = manager.getBookmark(id1);
		bm2 = manager.getBookmark(id2);
		
		bm1.setTags("tag1");
		assertTrue(manager.updateBookmark(bm1));
			
		Tag tag1 = tagDao.findTag("tag1").get(0);
		assertEquals(2,tag1.getCount());
		
		UserTag user1tag1 = tagDao.findUserTag(new User(2), tag1).get(0);
		assertEquals(1,user1tag1.getCount());
	
		bm2.setTags("");;
		assertTrue(manager.updateBookmark(bm2));
		Tag tag2 = tagDao.findTag("tag2").get(0);
		assertEquals(0,tag2.getCount());
	}
	
	public void testUpdateBookmark4() throws Exception{
		Bookmark bm1 = new Bookmark(new User(2),new Link(204));
		bm1.setTitle("bookmark_1_title");
		bm1.setTags("loc:columbia,md locality:baltimore,maryland");
		
		Bookmark bm2 = new Bookmark(new User(3),new Link(204));
		bm2.setTitle("bookmark_2_title");
		bm2.setTags("loc:columbia,md");
		
		int id1 = manager.addBookmark(bm1);
		int id2 = manager.addBookmark(bm2);
		assertTrue((id1 > 0));
		assertTrue((id2 > 0));
		
		bm1 = manager.getBookmark(id1);
		bm2 = manager.getBookmark(id2);
		
		bm1.setTags("locality:baltimore,maryland");
		assertTrue(manager.updateBookmark(bm1));
			
		Tag tag1 = tagDao.findTag("loc:columbia,md").get(0);
		assertEquals(1,tag1.getCount());
		
		UserTag user1tag1 = tagDao.findUserTag(new User(2), tag1).get(0);
		assertEquals(0,user1tag1.getCount());
	
		bm2.setTags("");;
		assertTrue(manager.updateBookmark(bm2));
		tag1 = tagDao.findTag("loc:columbia,md").get(0);
		assertEquals(0,tag1.getCount());
	}
	
	public void testUpdateBookmark5() throws Exception{
		Bookmark bm300 = manager.getBookmark(300);
		bm300.setTags("abc.df 12/13 \\019 ..ppp [0-x] | ?11?? *** p+ (.*)");
		assertTrue(manager.updateBookmark(bm300));
		
		bm300 = manager.getBookmark(300);
		assertEquals("abc.df 12/13 \\019 ..ppp [0-x] | ?11?? *** p+ (.*)",bm300.getTags());
		assertEquals(10,bm300.getTagList().size());
		
		bm300.setTags("abc df abc 12 13 \\ 019 ppp ppp [ 0-x ] | ?11 ?? * ** p + ( .* )");
		assertTrue(manager.updateBookmark(bm300));
		
		bm300 = manager.getBookmark(300);
		assertEquals("abc df 12 13 \\ 019 ppp [ 0-x ] | ?11 ?? * ** p + ( .* )",bm300.getTags());		
		assertEquals(20,bm300.getTagList().size());
		
		Tag tag1 = tagDao.findTag("abc").get(0);
		assertEquals(1,tag1.getCount());
		
		Tag tag2 = tagDao.findTag("df").get(0);
		assertEquals(1,tag2.getCount());
		
		Tag tag3 = tagDao.findTag("\\").get(0);
		assertEquals(1,tag3.getCount());
		
		Tag tag4 = tagDao.findTag("(").get(0);
		assertEquals(1,tag4.getCount());
		
		Tag tag5 = tagDao.findTag("\\019").get(0);
		assertEquals(0,tag5.getCount());
	}
	
	
	
	public void testRenameTag() throws Exception {
		// get an existing bookmark
		Bookmark bmark300 = manager.getBookmark(300);
		assertEquals("cnn",bmark300.getTags());
		
		// verify the baseline data
		Tag cnnTag = tagDao.findTag("cnn").get(0);	
		Tag newsTag = tagDao.findTag("news").get(0);
		assertEquals(1,cnnTag.getCount());
		assertEquals(0,newsTag.getCount());
		
		UserTag cnnUserTag = tagDao.findUserTag(new User(1),cnnTag).get(0);
		assertEquals(1,cnnUserTag.getCount());
		assertEquals(0,tagDao.findUserTag(new User(1),newsTag).size());
			
		// rename "cnn" to "news"
		boolean okay = manager.renameTag(new User(1),"cnn",new String[]{"news"});
		assertTrue(okay);
		
		// should differ from the baseline data
		cnnTag = tagDao.findTag("cnn").get(0);
		assertEquals(0,cnnTag.getCount());
		
		newsTag = tagDao.findTag("news").get(0);
		assertEquals(1,newsTag.getCount());
	}
	
	public void testRenameTag2() throws Exception{
		// get an existing bookmark
		Bookmark bmark300 = manager.getBookmark(300);
		assertEquals("cnn",bmark300.getTags());
		
		// verify the baseline data
		Tag cnnTag = tagDao.findTag("cnn").get(0);	
		Tag newsTag = tagDao.findTag("news").get(0);
		assertEquals(1,cnnTag.getCount());
		assertEquals(0,newsTag.getCount());
		
		UserTag cnnUserTag = tagDao.findUserTag(new User(1),cnnTag).get(0);
		assertEquals(1,cnnUserTag.getCount());
		assertEquals(0,tagDao.findUserTag(new User(1),newsTag).size());
			
		// rename "cnn" to "news"
		boolean okay =  manager.renameTag(new User(1),"cnn",new String[]{"breakingnews","cnn","headlines"});
		assertTrue(okay);
		
		cnnTag = tagDao.findTag("cnn").get(0);
		assertEquals(1,cnnTag.getCount());
		
		Tag breakingnewsTag = tagDao.findTag("breakingnews").get(0);
		assertEquals(1,breakingnewsTag.getCount());
		
		Tag headlinesTag = tagDao.findTag("headlines").get(0);
		assertEquals(1,headlinesTag.getCount());
	
		cnnUserTag = tagDao.findUserTag(new User(1),cnnTag).get(0);
		assertEquals(1,cnnUserTag.getCount());
		
		UserTag breakingnewsUserTag = tagDao.findUserTag(new User(1),breakingnewsTag).get(0);
		assertEquals(1,breakingnewsUserTag.getCount());
		
		UserTag headlinesUserTag = tagDao.findUserTag(new User(1),headlinesTag).get(0);
		assertEquals(1,headlinesUserTag.getCount());
	}
	
	public void testRenameTag3() throws Exception{
		Bookmark bm = new Bookmark(new User(1),new Link(204));
		bm.setTags("cnn");
		int bmId = manager.addBookmark(bm);
		assertTrue((bmId >0));
		
		boolean okay = manager.renameTag(new User(1),"cnn",new String[]{"cnn_news"});
		assertTrue(okay);
		
		Tag cnn_newsTag = tagDao.findTag("cnn_news").get(0);
		assertEquals(2,cnn_newsTag.getCount());
	}
	
	public void testRenameTag4() throws Exception{
		Bookmark bm = new Bookmark(new User(1),new Link(204));
		bm.setTags("java.logging");
		bm.setTitle("foio");
		int id = manager.addBookmark(bm);
		assertTrue((id > 0));
		
		boolean okay = manager.renameTag(new User(1),"java.logging",new String[]{"java","logging"});
		assertTrue(okay);
		
		Tag javaLoggingTag = tagDao.findTag("java.logging").get(0);
		assertEquals(0,javaLoggingTag.getCount());
		
		Tag javaTag = tagDao.findTag("java").get(0);
		assertEquals(1,javaTag.getCount());
		
		Tag loggingTag = tagDao.findTag("logging").get(0);
		assertEquals(1,loggingTag.getCount());
	}
	
	public void testRenameTag5() throws Exception{
		Bookmark bm300 = manager.getBookmark(300);
		bm300.setTags("tag1 tag2 tag3");
		assertTrue(manager.updateBookmark(bm300));
		bm300 = manager.getBookmark(300);
		assertTrue(bm300.getTagList().contains("tag1"));
		assertTrue(bm300.getTagList().contains("tag2"));
		assertTrue(bm300.getTagList().contains("tag3"));
		
		List<FolderTag> fTags = folderDao.findTagsInFolder(new Folder(1), 0, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(0,fTags.size());
		
		
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(bm300);
		boolean[] opOkay = folderDao.addBookmarks(new Folder(1), bmarks,GregorianCalendar.getInstance().getTime());
		assertEquals(true,opOkay[0]);
		
		fTags = folderDao.findTagsInFolder(new Folder(1), 1, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(3,fTags.size());
		
		boolean okay = manager.renameTag(new User(1), "tag1", new String[]{"tag0"});
		assertTrue(okay);
		
		okay = manager.renameTag(new User(1), "tag2", new String[]{"tag0"});
		assertTrue(okay);
		
		okay = manager.renameTag(new User(1), "tag3", new String[]{"tag3","web"});
		assertTrue(okay);
		
		fTags = folderDao.findTagsInFolder(new Folder(1), 1, FolderDao.SORT_BY_ALPHA, FolderDao.ASCENDING);
		assertEquals(3,fTags.size());

		FolderTag t1 = fTags.get(0);
		FolderTag t2 = fTags.get(1);
		FolderTag t3 = fTags.get(2);
		assertEquals("tag0",t1.getTag().getLabel());
		assertEquals("tag3",t2.getTag().getLabel());
		assertEquals("web",t3.getTag().getLabel());
		assertEquals(1,t1.getCount());
		assertEquals(1,t2.getCount());
		assertEquals(1,t3.getCount());
	}
	
	
	public void testDeleteTag() throws Exception{
		boolean okay =  manager.deleteTag(new User(1),"cnn");
		assertTrue(okay);
		
		Tag cnnTag = tagDao.findTag("cnn").get(0);			
		assertEquals(0,cnnTag.getCount());
		
		Bookmark bm = manager.getBookmark(300);
		assertEquals("",bm.getTags());
	}
	
	
	public void testUpdateBookmark6() throws Exception{
		Bookmark bm300 = manager.getBookmark(300);
		bm300.setTags("CNN");
		assertTrue(manager.updateBookmark(bm300));
		bm300 = manager.getBookmark(300);
		assertEquals("CNN",bm300.getTags());
		
		Tag CNN = tagDao.findTag("CNN").get(0);
		assertEquals(1,CNN.getCount());
		
		Tag cnn = tagDao.findTag("cnn").get(0);
		assertEquals(0,cnn.getCount());
	}
	
	public void testAddPointMarkers() throws Exception{
		PointMarker pm1 = new PointMarker();
		pm1.setMarkerIconId(5);
		pm1.setNotes("marker1");
		pm1.setPoint(10,11);
		
		int id = geomMarkerDao.createPointMarker(pm1);
		assertTrue(id > 0);
		pm1.setId(id);			
		
		PointMarker pm2 = new PointMarker();
		pm2.setMarkerIconId(2);
		pm2.setNotes("marker2");
		pm2.setPoint(20.03,21.11);
		
		List<PointMarker> ptMarkers = new ArrayList<PointMarker>();
		ptMarkers.add(pm1);
		ptMarkers.add(pm2);
		
		Bookmark bm300 = manager.getBookmark(300);
		List<PointMarker> addedPtMarkers = manager.addPointMarkers(bm300, ptMarkers);
		assertEquals(2,addedPtMarkers.size());
		assertEquals(id,addedPtMarkers.get(0).getId());
		assertTrue(addedPtMarkers.get(1).getId() > 0);
		
		ptMarkers = geomMarkerDao.listPointMarkers(bm300);
		assertEquals(2,ptMarkers.size());
		
		for(PointMarker pm : ptMarkers){
			if(pm.getId() == pm1.getId()){
				assertEquals(5,pm.getMarkerIconId());
				assertEquals("marker1",pm.getNotes());
				assertEquals(10.0,pm.getPoint().getX());
				assertEquals(11.0,pm.getPoint().getY());
			}else if(pm.getId() == pm2.getId()){
				assertEquals(2,pm2.getMarkerIconId());
				assertEquals("marker2",pm2.getNotes());
				assertEquals(20.03,pm2.getPoint().getX());
				assertEquals(21.11,pm2.getPoint().getY());
			}else{
				assertFalse(true);
			}
		}
		pm1 = ptMarkers.get(0);	
		pm2 = ptMarkers.get(1);
		
		assertTrue(geomMarkerDao.deletePointMarker(pm1.getId()));
		assertTrue(geomMarkerDao.deletePointMarker(pm2.getId()));
	}
	
	public void testRemovePointMarkers() throws Exception{
		PointMarker pm1 = new PointMarker();
		pm1.setMarkerIconId(5);
		pm1.setNotes("marker1");
		pm1.setPoint(10,11);
		
		int id = geomMarkerDao.createPointMarker(pm1);
		assertTrue(id > 0);
		pm1.setId(id);			
		
		PointMarker pm2 = new PointMarker();
		pm2.setMarkerIconId(2);
		pm2.setNotes("marker2");
		pm2.setPoint(20.03,21.11);
		
		List<PointMarker> ptMarkers = new ArrayList<PointMarker>();
		ptMarkers.add(pm1);
		ptMarkers.add(pm2);
		
		Bookmark bm300 = manager.getBookmark(300);
		List<PointMarker> addedPtMarkers = manager.addPointMarkers(bm300, ptMarkers);
		assertEquals(2,addedPtMarkers.size());
		ptMarkers = geomMarkerDao.listPointMarkers(bm300);
		assertEquals(2,ptMarkers.size());
		
		ptMarkers.clear();
		ptMarkers.add(pm1);
		
		List<PointMarker> rmved = manager.removePointMarkers(bm300,ptMarkers);
		assertEquals(1,rmved.size());
		
		ptMarkers = geomMarkerDao.listPointMarkers(bm300);
		assertEquals(1,ptMarkers.size());
		
		pm2 = ptMarkers.get(0);
		assertEquals(2,pm2.getMarkerIconId());
		assertEquals("marker2",pm2.getNotes());
		assertEquals(20.03,pm2.getPoint().getX());
		assertEquals(21.11,pm2.getPoint().getY());
		
		assertTrue(geomMarkerDao.deletePointMarker(pm2.getId()));
		assertNull(geomMarkerDao.getPointMarker(pm1.getId()));
	}
	
	public void testGetPointMarkers() throws Exception{
		PointMarker pm1 = new PointMarker();
		pm1.setMarkerIconId(5);
		pm1.setNotes("marker1");
		pm1.setPoint(10,11);
		
		int id = geomMarkerDao.createPointMarker(pm1);
		assertTrue(id > 0);
		pm1.setId(id);			
		
		PointMarker pm2 = new PointMarker();
		pm2.setMarkerIconId(2);
		pm2.setNotes("marker2");
		pm2.setPoint(20.03,21.11);
		
		List<PointMarker> ptMarkers = new ArrayList<PointMarker>();
		ptMarkers.add(pm1);
		ptMarkers.add(pm2);
		
		Bookmark bm300 = manager.getBookmark(300);
		List<PointMarker> addedPtMarkers = manager.addPointMarkers(bm300, ptMarkers);
		assertEquals(2,addedPtMarkers.size());
		
		ptMarkers = manager.getPointMarkers(bm300);
		assertEquals(2,ptMarkers.size());
		for(PointMarker pm : ptMarkers){
			if(pm.getId() == pm1.getId()){
				assertEquals(5,pm.getMarkerIconId());
				assertEquals("marker1",pm.getNotes());
				assertEquals(10.0,pm.getPoint().getX());
				assertEquals(11.0,pm.getPoint().getY());
			}else if(pm.getId() == pm2.getId()){
				assertEquals(2,pm2.getMarkerIconId());
				assertEquals("marker2",pm2.getNotes());
				assertEquals(20.03,pm2.getPoint().getX());
				assertEquals(21.11,pm2.getPoint().getY());
			}else{
				assertFalse(true);
			}
		}
		
		assertTrue(geomMarkerDao.deletePointMarker(ptMarkers.get(0).getId()));
		assertTrue(geomMarkerDao.deletePointMarker(ptMarkers.get(1).getId()));
	}
	
	public void testPageBookmarkHasGeomMarker() throws Exception{
		DaoResult<Bookmark> result = manager.pageBookmarkHasGeomMarker(new User(1), 0,10);
		assertEquals(0,result.getSize());
		assertEquals(0,result.getResult().size());
		
		PointMarker pm1 = new PointMarker();
		pm1.setMarkerIconId(5);
		pm1.setNotes("marker1");
		pm1.setPoint(10,11);
		
		int id = geomMarkerDao.createPointMarker(pm1);
		assertTrue(id > 0);
		pm1.setId(id);			
		
		PointMarker pm2 = new PointMarker();
		pm2.setMarkerIconId(2);
		pm2.setNotes("marker2");
		pm2.setPoint(20.03,21.11);
		
		List<PointMarker> ptMarkers = new ArrayList<PointMarker>();
		ptMarkers.add(pm1);
		ptMarkers.add(pm2);
		
		Bookmark bm300 = manager.getBookmark(300);
		List<PointMarker> addedPtMarkers = manager.addPointMarkers(bm300, ptMarkers);
		assertEquals(2,addedPtMarkers.size());		
		ptMarkers = manager.getPointMarkers(bm300);
		
		result = manager.pageBookmarkHasGeomMarker(new User(1), 0, 10);
		assertEquals(1,result.getSize());
		
		assertTrue(geomMarkerDao.deletePointMarker(ptMarkers.get(0).getId()));
		assertTrue(geomMarkerDao.deletePointMarker(ptMarkers.get(1).getId()));
	}
}
