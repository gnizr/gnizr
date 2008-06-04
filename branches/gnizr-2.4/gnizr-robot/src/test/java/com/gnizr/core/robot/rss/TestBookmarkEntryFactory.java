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
package com.gnizr.core.robot.rss;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;
import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndCategoryImpl;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;

public class TestBookmarkEntryFactory extends TestCase {

	private BookmarkEntryFactory factory;
	private String title = "this is a title";
	private List<SyndCategory> categories = new ArrayList<SyndCategory>();
	private Date pubDate = GnizrDaoUtil.getNow();
	private Date updDate = GnizrDaoUtil.getNow();
	private String link = null;
	private String rssUrl = "http://somesite.com/rss";
	private SyndContent content = new SyndContentImpl();
	
	private FeedSubscription fromSub;
	private SyndEntry entry;
	
	protected void setUp() throws Exception {
		super.setUp();
		factory = new BookmarkEntryFactory();
		
		entry = new SyndEntryImpl();
		entry.setTitle("this is a title");
		SyndCategory c = new SyndCategoryImpl();
		c.setName("abc,123,0990");
		categories.add(c);
		c = new SyndCategoryImpl();
		c.setName("cat dog food");
		categories.add(c);
		c = new SyndCategoryImpl();
		c.setName("red");
		categories.add(c);
		c = new SyndCategoryImpl();
		c.setName("blue");
		categories.add(c);
		entry.setCategories(categories);
		link = "http://something.example.com/page1";
		entry.setLink(link);
		entry.setPublishedDate(pubDate);
		entry.setUpdatedDate(updDate);	
		content.setValue("notes");
		entry.setDescription(content);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testCreateEntry1() throws Exception{
		fromSub = new FeedSubscription();
		Bookmark rssBm = new Bookmark();
		rssBm.setUser(new User("hchen1"));
		rssBm.setLink(new Link(rssUrl));
		fromSub.setBookmark(rssBm);
		fromSub.setImportFolders(new ArrayList<String>());
			
		BookmarkEntry bmEntry = factory.createEntry(entry,null, fromSub);
		assertNotNull(bmEntry);
		Bookmark b = bmEntry.getBookmark();
		assertEquals(title,b.getTitle());
		assertEquals(link,b.getLink().getUrl());
		assertEquals(content.getValue(),b.getNotes().trim());
		List<String> tags = b.getTagList();
		assertTrue(tags.contains("123"));
		assertTrue(tags.contains("abc"));
		assertTrue(tags.contains("0990"));
		assertTrue(tags.contains("catdogfood"));
		assertTrue(tags.contains("red"));
		assertTrue(tags.contains("blue"));
		assertFalse(tags.contains("null"));
		assertTrue(tags.contains("gn:folder=_import_"));
		assertTrue(pubDate.equals(b.getCreatedOn()));
		assertTrue(updDate.equals(b.getLastUpdated()));
		assertEquals("hchen1",b.getUser().getUsername());		
	}
	
	
	public void testCreateEntry2() throws Exception{
		fromSub = new FeedSubscription();
		Bookmark rssBm = new Bookmark();
		rssBm.setUser(new User("hchen1"));
		rssBm.setLink(new Link(rssUrl));
		rssBm.setTags("tag:mycoolfeed gn:tag=geonames:21024,md tag:    ");
		fromSub.setBookmark(rssBm);
		
		List<String> imFldrs = new ArrayList<String>();
		imFldrs.add("folder1");		
		fromSub.setImportFolders(imFldrs);
			
		BookmarkEntry bmEntry = factory.createEntry(entry,null, fromSub);
		assertNotNull(bmEntry);
		Bookmark b = bmEntry.getBookmark();
		assertEquals(title,b.getTitle());
		assertEquals(link,b.getLink().getUrl());
		assertEquals(content.getValue(),b.getNotes().trim());
		List<String> tags = b.getTagList();
		assertTrue(tags.contains("123"));
		assertTrue(tags.contains("abc"));
		assertTrue(tags.contains("0990"));
		assertTrue(tags.contains("catdogfood"));
		assertTrue(tags.contains("red"));
		assertTrue(tags.contains("blue"));
		assertTrue(tags.contains("gn:folder=folder1"));
		assertTrue(tags.contains("mycoolfeed"));
		assertTrue(tags.contains("geonames:21024,md"));
		assertFalse(tags.contains("null"));
		assertTrue(pubDate.equals(b.getCreatedOn()));
		assertTrue(updDate.equals(b.getLastUpdated()));
		assertEquals("hchen1",b.getUser().getUsername());		
	}

}
