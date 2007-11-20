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
package com.gnizr.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.vocab.TimeRange;
import com.gnizr.db.MIMEType;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.TagProperty;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.tag.TagAssertionDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.tag.TagPropertyDao;
import com.gnizr.db.dao.user.UserDao;

public class TestGnizrDaoUtil extends GnizrCoreTestBase {

	private TagAssertionDao tagAssertionDao;
	private TagDao tagDao;
	private TagPropertyDao tagPrptDao;
	private UserDao userDao;
	private BookmarkDao bookmarkDao;
	private LinkDao linkDao;
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestGnizrDaoUtil.class.getResourceAsStream("/TestGnizrDaoUtil-input.xml"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		userDao = getGnizrDao().getUserDao();
		tagDao = getGnizrDao().getTagDao();
		tagPrptDao = getGnizrDao().getTagPropertyDao();
		tagAssertionDao = getGnizrDao().getTagAssertionDao();
		linkDao = getGnizrDao().getLinkDao();
		bookmarkDao = getGnizrDao().getBookmarkDao();
	}

	public void testFillIdBookmark() throws Exception{
		User user = new User(1);
		Link link = new Link("http://zirr.us/");
		Bookmark bmark = new Bookmark(user,link);
		GnizrDaoUtil.fillId(bookmarkDao, userDao, linkDao, bmark);
		assertEquals((300),bmark.getId());
		assertEquals((1),bmark.getUser().getId());
		assertEquals((202),bmark.getLink().getId());
	}
	
	public void testFillIdLink() throws Exception{
		Link link = new Link("http://zirr.us/");
		GnizrDaoUtil.fillId(linkDao, link);
		assertEquals((202),link.getId());
	}
	
	public void testFillIdLinkTag() throws Exception{
		Tag tag = new Tag("web");
		Link link = new Link(202);
		LinkTag linkTag = new LinkTag(link,tag);
		GnizrDaoUtil.fillId(tagDao,linkDao,linkTag);
		assertEquals((1),linkTag.getId());
		assertEquals((3),linkTag.getTag().getId());
		assertEquals((202),linkTag.getLink().getId());		
	}
	
	
	public void testFillIdTag() throws Exception{
		Tag tag = new Tag("wii",(0));		
		GnizrDaoUtil.fillId(tagDao,tag);
		assertEquals((2),tag.getId());
		assertEquals("wii",tag.getLabel());
		assertEquals((0),tag.getCount());
		
		boolean caughtException = false;
		try{
			GnizrDaoUtil.fillId(tagDao, new Tag("fooabre",(30)));
		}catch(Exception e){
			caughtException = true;
		}
		assertTrue(caughtException);
	}
	
	public void testFillIdTagAssertion() throws Exception{
		User user = new User("hchen1");
		UserTag subject = new UserTag("hchen1","webwork");
		TagProperty property = new TagProperty(3);
		UserTag object = new UserTag(2);
		TagAssertion asrt = new TagAssertion(subject,property,object,user);
		GnizrDaoUtil.fillId(tagAssertionDao, tagPrptDao, tagDao, userDao,asrt);
		assertEquals((1),asrt.getUser().getId());
		assertEquals((1),asrt.getSubject().getId());
		assertEquals((2),asrt.getObject().getId());
		assertEquals((3),asrt.getProperty().getId());
	}
	
	public void testFillIdTagProperty() throws Exception{
		TagProperty tp = new TagProperty();
		tp.setNamespacePrefix("skos");
		tp.setName("broader");
		GnizrDaoUtil.fillId(tagPrptDao, tp);
		assertEquals((2),tp.getId());
	}
	
	public void testFillIdUser() throws Exception{
		User user = new User("hchen1");
		GnizrDaoUtil.fillId(userDao, user);
		assertEquals((1),user.getId());
		assertEquals("hchen1",user.getUsername());
		assertNull(user.getEmail());
	}
	
	public void testFillIdUserTag() throws Exception{
		UserTag userTag = new UserTag();
		userTag.setTag(new Tag("webwork",(0)));
		userTag.setUser(new User("hchen1"));
		GnizrDaoUtil.fillId(tagDao,userDao,userTag);
		assertEquals((1),userTag.getId());
		assertEquals((1),userTag.getUser().getId());
		assertEquals((1),userTag.getTag().getId());
		
		boolean caughtException = false;
		try{
			userTag.setId(-1);
			userTag.setUser(new User("jfdkslf"));
			GnizrDaoUtil.fillId(tagDao,userDao,userTag);
		}catch(Exception e){
			caughtException = true;
		}
		assertTrue(caughtException);
	}
	
	public void testFillObjectBookmark() throws Exception{
		Bookmark bm = new Bookmark(300);
		GnizrDaoUtil.fillObject(bookmarkDao, userDao, linkDao, bm);
		assertEquals("zirrus",bm.getTitle());
		assertEquals("cnn news",bm.getTags());
		assertEquals("hchen1",bm.getUser().getUsername());
		assertEquals("http://zirr.us/",bm.getLink().getUrl());
	}
	
	public void testFillObjectLink() throws Exception{
		Link link = new Link(202);
		GnizrDaoUtil.fillObject(linkDao,link);
		assertEquals(1003,link.getMimeTypeId());
		assertEquals("http://zirr.us/",link.getUrl());
		assertEquals(1,link.getCount());
	}
	
	public void testFillObjectLinkTag() throws Exception{
		LinkTag linkTag = new LinkTag(1);
		GnizrDaoUtil.fillObject(tagDao, linkDao, linkTag);
		assertEquals("web",linkTag.getTag().getLabel());
		assertEquals((3),linkTag.getTag().getCount());
	}
	
	public void testFillObjectTag() throws Exception{
		Tag tag = new Tag("wii");
		GnizrDaoUtil.fillObject(tagDao, tag);
		assertEquals((2),tag.getId());
		assertEquals("wii",tag.getLabel());
		
		tag = new Tag((1));
		GnizrDaoUtil.fillObject(tagDao, tag);
		assertEquals("webwork",tag.getLabel());		
	}
	
	public void testFillObjectTagAssertion() throws Exception{
		User u = new User((1));
		UserTag s = new UserTag((1));
		UserTag o = new UserTag((2));
		TagProperty p = new TagProperty((3));
		TagAssertion ta = new TagAssertion(s,p,o,u);
		GnizrDaoUtil.fillObject(tagAssertionDao, tagPrptDao, tagDao, userDao,ta);
		assertEquals("hchen1",ta.getUser().getUsername());
		assertEquals("webwork",ta.getSubject().getTag().getLabel());
		assertEquals("wii",ta.getObject().getTag().getLabel());
		assertEquals("narrower",ta.getProperty().getName());
	}
	
	public void testFillObjectTagProperty() throws Exception{
		TagProperty related = new TagProperty();
		related.setNamespacePrefix("skos");
		related.setName("related");
		GnizrDaoUtil.fillObject(tagPrptDao,related);
		assertEquals((1),related.getId());
		assertEquals(TagProperty.CARDINALITY_ONE_OR_MORE,related.getCardinality());
		assertEquals(TagProperty.TYPE_DEFAULT,related.getPropertyType());
	}
	
	public void testFillObjectUser() throws Exception{
		User user = new User((1));
		GnizrDaoUtil.fillObject(userDao, user);
		assertEquals("hchen1",user.getUsername());
		assertEquals("Harry Chen",user.getFullname());
		
		user = new User("hchen1");
		GnizrDaoUtil.fillObject(userDao,user);
		assertEquals((1),user.getId());
		assertEquals("Harry Chen",user.getFullname());
	}
	
	public void testFillObjectUserTag() throws Exception{
		UserTag userTag = new UserTag("hchen1","wii");
		GnizrDaoUtil.fillObject(tagDao, userDao, userTag);
		assertEquals((2),userTag.getId());
		assertEquals((1),userTag.getCount());
		
		User user = userTag.getUser();
		assertEquals((1),user.getId());
		assertEquals("hchen1",user.getUsername());
		assertEquals("Harry Chen",user.getFullname());
		
		Tag tag = userTag.getTag();
		assertEquals((2),tag.getId());
		assertEquals("wii",tag.getLabel());
		assertEquals((1),tag.getCount());
	}
	
	public void testCreateTagIfNotExist() throws Exception{
		String tag = "usa";
		List<Tag> tags = tagDao.findTag(tag);
		assertTrue(tags.isEmpty());

		int id = GnizrDaoUtil.createTagIfNotExist(tagDao,tag);
		Tag t = tagDao.getTag(id);
		assertEquals(tag,t.getLabel());
		
		int id2 = GnizrDaoUtil.createTagIfNotExist(tagDao, tag);
		t = tagDao.getTag(id2);
		assertNotNull(t);
		assertEquals(id,id2);
		
		tag = "foobar:usa";
		int id3 = GnizrDaoUtil.createTagIfNotExist(tagDao, tag);
		t = tagDao.getTag(id3);
		assertNotNull(t);
		assertEquals("foobar:usa",t.getLabel());
	}
	
	public void testCreateUserTagIfNotExist() throws Exception{
		String tag1 = "hchen1:usa";
		String tag3 = "java";
		
		User defaultUser = new User("hchen1");
		
		UserTag userTag = null;
		
		int id1 = GnizrDaoUtil.createUserTagIfNotExist(tagDao, userDao,tag1, defaultUser);
		assertTrue((id1 > 0));
		userTag = tagDao.getUserTag(id1);
		GnizrDaoUtil.fillObject(tagDao, userDao, userTag);
		assertNotNull(userTag);
		assertEquals(id1,userTag.getId());
		assertEquals("hchen1:usa",userTag.getTag().getLabel());
		assertEquals("hchen1",userTag.getUser().getUsername());
		
		int id3 = GnizrDaoUtil.createUserTagIfNotExist(tagDao, userDao, tag3, defaultUser);
		assertTrue((id3 > 0));
		userTag = tagDao.getUserTag(id3);
		GnizrDaoUtil.fillObject(tagDao, userDao, userTag);
		assertNotNull(userTag);
		assertEquals(id3,userTag.getId());
		assertEquals("java",userTag.getTag().getLabel());
		assertEquals("hchen1",userTag.getUser().getUsername());
		
		int id4 = GnizrDaoUtil.createUserTagIfNotExist(tagDao, userDao, tag3, defaultUser);
		assertEquals(id3,id4);
	}
	
	public void testToDayStarts() throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = df.parse("2007-01-11T12:13:93");
		Date dateStarts = GnizrDaoUtil.toDayBegins(date);
		Calendar c = Calendar.getInstance();
		c.setTime(dateStarts);
		assertEquals(2007,c.get(Calendar.YEAR));
		assertEquals(0,c.get(Calendar.MONTH));
		assertEquals(11,c.get(Calendar.DAY_OF_MONTH));
		assertEquals(0,c.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,c.get(Calendar.MINUTE));
		assertEquals(0,c.get(Calendar.SECOND));
	}
	
	public void testToDayEnds() throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = df.parse("2007-01-11T12:13:93");
		Date dateEnds = GnizrDaoUtil.toDayEnds(date);
		Calendar c = Calendar.getInstance();
		c.setTime(dateEnds);
		assertEquals(2007,c.get(Calendar.YEAR));
		assertEquals(0,c.get(Calendar.MONTH));
		assertEquals(11,c.get(Calendar.DAY_OF_MONTH));
		assertEquals(23,c.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,c.get(Calendar.MINUTE));
		assertEquals(59,c.get(Calendar.SECOND));
	}
	
	public void testAddNHours() throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = df.parse("2007-01-11T00:00:00");
		date = GnizrDaoUtil.addNHours(date,24);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		assertEquals(2007,c.get(Calendar.YEAR));
		assertEquals(0,c.get(Calendar.MONTH));
		assertEquals(12,c.get(Calendar.DAY_OF_MONTH));
		assertEquals(0,c.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,c.get(Calendar.MINUTE));
		assertEquals(0,c.get(Calendar.SECOND));
	}
	
	public void testSubNHours() throws Exception{
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date = df.parse("2007-01-11T00:00:00");
		date = GnizrDaoUtil.subNHours(date,24);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		assertEquals(2007,c.get(Calendar.YEAR));
		assertEquals(0,c.get(Calendar.MONTH));
		assertEquals(10,c.get(Calendar.DAY_OF_MONTH));
		assertEquals(0,c.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,c.get(Calendar.MINUTE));
		assertEquals(0,c.get(Calendar.SECOND));
	}
	
	public void testGetTimeRangeDateToday() throws Exception{
		Date[] timeRange = GnizrDaoUtil.getTimeRangeDate(TimeRange.TODAY);
		Date now = GnizrDaoUtil.getNow();
	
		Calendar nc = Calendar.getInstance();
		nc.setTime(now);
		
		Calendar sc = Calendar.getInstance();
		sc.setTime(timeRange[0]);
		
		Calendar ec = Calendar.getInstance();
		ec.setTime(timeRange[1]);
		
		assertEquals(nc.get(Calendar.YEAR),sc.get(Calendar.YEAR));
		assertEquals(nc.get(Calendar.MONTH),sc.get(Calendar.MONTH));
		assertEquals(nc.get(Calendar.DAY_OF_MONTH),sc.get(Calendar.DAY_OF_MONTH));
		
		assertEquals(nc.get(Calendar.YEAR),ec.get(Calendar.YEAR));
		assertEquals(nc.get(Calendar.MONTH),ec.get(Calendar.MONTH));
		assertEquals(nc.get(Calendar.DAY_OF_MONTH),ec.get(Calendar.DAY_OF_MONTH));
		
		assertEquals(0,sc.get(Calendar.HOUR_OF_DAY));
		assertEquals(0,sc.get(Calendar.MINUTE));
		assertEquals(0,sc.get(Calendar.SECOND));
		
		assertEquals(23,ec.get(Calendar.HOUR_OF_DAY));
		assertEquals(59,ec.get(Calendar.MINUTE));
		assertEquals(59,ec.get(Calendar.SECOND));
		
	}
	
	public void testGetTimeRangeDateYesterday() throws Exception{
		Date[] timeRange = GnizrDaoUtil.getTimeRangeDate(TimeRange.YESTERDAY);
		Date now = GnizrDaoUtil.getNow();
	
		Calendar nc = Calendar.getInstance();
		nc.setTime(now);
		nc.add(Calendar.HOUR_OF_DAY,-24);
		
		Calendar sc = Calendar.getInstance();
		sc.setTime(timeRange[0]);
		
		Calendar ec = Calendar.getInstance();
		ec.setTime(timeRange[1]);
		
		assertTrue(sc.before(nc));
		assertTrue(ec.after(nc));
		assertTrue(sc.before(ec));		
		assertEquals(sc.get(Calendar.HOUR_OF_DAY),0);
		assertEquals(ec.get(Calendar.HOUR_OF_DAY),23);
		assertEquals(sc.get(Calendar.DAY_OF_MONTH),ec.get(Calendar.DAY_OF_MONTH));
	}
	
	public void testGetTimeRangeDateLast7Days() throws Exception{
		Date[] timeRange = GnizrDaoUtil.getTimeRangeDate(TimeRange.LAST_7_DAYS);
		Date now = GnizrDaoUtil.getNow();

		Calendar nc = Calendar.getInstance();
		nc.setTime(now);
		
		Calendar sc = Calendar.getInstance();
		sc.setTime(timeRange[0]);
		
		Calendar ec = Calendar.getInstance();
		ec.setTime(timeRange[1]);
		
		assertTrue(sc.before(nc));
		assertTrue(ec.after(nc));
		assertTrue(sc.before(ec));
	}
	
	public void testGetTimeRangeDateLastMonth() throws Exception{
		Date[] timeRange = GnizrDaoUtil.getTimeRangeDate(TimeRange.LAST_MONTH);
		Date now = GnizrDaoUtil.getNow();

		Calendar nc = Calendar.getInstance();
		nc.setTime(now);
		
		Calendar sc = Calendar.getInstance();
		sc.setTime(timeRange[0]);
		
		Calendar ec = Calendar.getInstance();
		ec.setTime(timeRange[1]);
		
		assertTrue(sc.before(nc));
		assertTrue(ec.before(nc));
		assertTrue(sc.before(ec));
	}
	
	public void testGetTimeRangeDateThisMonth() throws Exception{
		Date[] timeRange = GnizrDaoUtil.getTimeRangeDate(TimeRange.THIS_MONTH);
		Date now = GnizrDaoUtil.getNow();

		Calendar nc = Calendar.getInstance();
		nc.setTime(now);
		
		Calendar sc = Calendar.getInstance();
		sc.setTime(timeRange[0]);
		
		Calendar ec = Calendar.getInstance();
		ec.setTime(timeRange[1]);
		
		assertTrue(sc.before(nc));
		assertTrue(ec.after(nc));
		assertTrue(sc.before(ec));
	}
	
	public void testDetectMIMEType() throws Exception{
		int mimeTypeId = GnizrDaoUtil.detectMIMEType("http://www.cnn.com");
		assertEquals(MIMEType.TEXT_HTML,mimeTypeId);
		
		mimeTypeId = GnizrDaoUtil.detectMIMEType("http://www.google.com/intl/en_ALL/images/logo.gif");
		assertEquals(MIMEType.IMG_GIF,mimeTypeId);
		
		mimeTypeId = GnizrDaoUtil.detectMIMEType("http://google.com");
		assertEquals(MIMEType.TEXT_HTML,mimeTypeId);
	}
	
	public void testIsUrlSafeString() throws Exception{
		boolean f = GnizrDaoUtil.isUrlSafeString("abc?");
		assertFalse(f);
		f = GnizrDaoUtil.isUrlSafeString("abc/adc");
		assertFalse(f);
		f = GnizrDaoUtil.isUrlSafeString("\\ab c\\");
		assertFalse(f);
		f = GnizrDaoUtil.isUrlSafeString(";2 3");
		assertFalse(f);
		f = GnizrDaoUtil.isUrlSafeString("'fa'");
		assertFalse(f);
		f = GnizrDaoUtil.isUrlSafeString("\" fds");
		assertFalse(f);
		f = GnizrDaoUtil.isUrlSafeString("ja & ff");
		assertFalse(f);
		f = GnizrDaoUtil.isUrlSafeString("ja + ff");
		assertFalse(f);
		
		f = GnizrDaoUtil.isUrlSafeString("ja * ff");
		assertFalse(f);
		
		f = GnizrDaoUtil.isUrlSafeString("ja ^ ff");
		assertFalse(f);
		
		f = GnizrDaoUtil.isUrlSafeString("ja % ff");
		assertFalse(f);
	}
	
	public void testEncodeURI() throws Exception{
		String s = GnizrDaoUtil.encodeURI("http://www.gnizr.com:3000/ksb/public/workspaces/ngaworkspace/datasets/country/instances?lensId=CountryWithPopulation&maxpop=100000000&format=atom");
		assertNotNull(s);
		
	}
	
	public void testDiffList() throws Exception{
		List<String> aList = new ArrayList<String>();
		List<String> bList = new ArrayList<String>();
		
		aList.add("abc");
		aList.add("123");
		aList.add("93939");
		
		bList.add("abc");
		
		List<String> result = GnizrDaoUtil.diffList(aList, bList);
		assertEquals(2,result.size());
		assertTrue(result.contains("123"));
		assertTrue(result.contains("93939"));
	}
	
	public void testStripNonPrintableChar() throws Exception{
		String s = "contribute \u0007 \fto \nother\tsustainable\rdevelopment goals";
		int length = s.length();
		String s1 = GnizrDaoUtil.stripNonPrintableChar(s);
		System.out.println(s1);
		assertEquals(length,s1.length());
		assertFalse(s1.contains("\r"));
		assertFalse(s1.contains("\n"));
		assertFalse(s1.contains("\t"));
		assertFalse(s1.contains("\f"));
		assertFalse(s1.contains("\u0007"));
	}
	
	public void testGetRandomURI() throws Exception{
		String s1 = GnizrDaoUtil.getRandomURI();
		assertNotNull(s1);
		String s2 = GnizrDaoUtil.getRandomURI();
		assertNotNull(s2);
		assertFalse(s1.equals(s2));
	}
}
