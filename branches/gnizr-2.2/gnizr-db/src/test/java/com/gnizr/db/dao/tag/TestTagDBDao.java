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
package com.gnizr.db.dao.tag;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDBTestBase;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class TestTagDBDao extends GnizrDBTestBase {

	private TagDBDao tagDao;
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTagDBDao.class.getResourceAsStream("/dbunit/tagdbdao/TestTagDBDao-input.xml"));
	}

	protected void setUp() throws Exception {
		super.setUp();
		tagDao = new TagDBDao(getDataSource());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreateLinkTag() throws Exception{
		Link link = new Link(1);
		Tag tag = new Tag("newtag");
		int tid = tagDao.createTag(tag);
		tag = new Tag(tid);
		int id = tagDao.createLinkTag(new LinkTag(link,tag));
		assertTrue((id >0));
		
		int id2 = tagDao.createLinkTag(new LinkTag(link,tag));
		assertEquals(id,id2);
	}
	
	public void testCreateTag() throws Exception{
		String t = "newtag";
		Tag tag1 = new Tag(t);		
		int t1Id = tagDao.createTag(tag1);
		assertNotNull(t1Id);
		assertTrue((t1Id > 0));
		
		int t1Id2 = tagDao.createTag(tag1);
		assertNotNull(t1Id2);
		assertEquals(t1Id,t1Id2);
		
		t1Id = tagDao.createTag(tag1);
		assertNotNull(t1Id);
		assertTrue((t1Id > 0));
	}
	
	public void testCreateTag2() throws Exception{
		String t1 = "tt";
		String t2 = "TT";
		
		int t1Id = tagDao.createTag(new Tag(t1));
		int t2Id = tagDao.createTag(new Tag(t2));
		assertFalse((t1Id == t2Id));
	}
	
	public void testCreateUserTag() throws Exception{
		User user = new User(1);
		Tag tag = new Tag("newtag");
		int tid = tagDao.createTag(tag);
		tag = tagDao.getTag(tid);
		UserTag userTag = new UserTag(user,tag);
		int id = tagDao.createUserTag(userTag);
		assertNotNull(id);
		assertTrue((id > 0));
		
		int id2 = tagDao.createUserTag(userTag);
		assertNotNull(id2);
		assertEquals(id,id2);
	}
	
	public void testDeleteLinkTag() throws Exception{
		int id = (1);
		assertTrue(tagDao.deleteLinkTag(id));
		assertFalse(tagDao.deleteLinkTag(id));
		assertNull(tagDao.getLinkTag(id));
	}
	
	public void testDeleteTag() throws Exception{
		int id = (1);
		assertTrue(tagDao.deleteTag(id));
		assertFalse(tagDao.deleteTag(id));
		assertNull(tagDao.getTag(id));
	}
	
	public void testDeleteUserTag() throws Exception{
		int id = (1);
		assertTrue(tagDao.deleteUserTag(id));
		assertFalse(tagDao.deleteUserTag(id));
		assertNull(tagDao.getUserTag(id));
	}
	
	
	public void testFindLinkTag() throws Exception{
		Link link = new Link(1);
		Tag tag = new Tag(1);
		List<LinkTag> linkTags = tagDao.findLinkTag(link, tag);
		assertEquals(1,linkTags.size());
		
		LinkTag linkTag = linkTags.get(0);
		assertEquals((1),linkTag.getLink().getId());
		assertEquals((1),linkTag.getTag().getId());
	
		assertNotNull(linkTag.getTag().getLabel());
		assertNotNull(linkTag.getLink().getUrlHash());		
	}
	
	public void testFindLinkTagMinFreq() throws Exception{
		Link link = new Link(1);
		List<LinkTag> tags = tagDao.findLinkTag(link,0);
		assertEquals(4,tags.size());
		
		tags = tagDao.findLinkTag(link, 10);
		assertEquals(2,tags.size());
		
		assertNotNull(tags.get(0).getLink().getUrl());
	}
	
	public void testPageLinkTagSortByFreq() throws Exception{
		Link link = new Link(1);
		DaoResult<LinkTag> result = tagDao.pageLinkTagSortByFreq(link, 0,100);
		assertEquals(3, result.getSize());
		List<LinkTag> tags = result.getResult();
		assertEquals(105,tags.get(0).getCount());
		assertEquals(100,tags.get(1).getCount());	
		assertNotNull(tags.get(0).getLink().getUrl());
		
		result = tagDao.pageLinkTagSortByFreq(link,2,100);
		assertEquals(3, result.getSize());
		assertEquals(1,result.getResult().size());
	}
	
	
	public void testFindTag() throws Exception{
		List<Tag> tags = tagDao.findTag("news");
		assertEquals(1,tags.size());
		Tag t = tags.get(0);
		assertEquals((2),t.getId());
		assertEquals("news", t.getLabel());
		assertEquals((100),t.getCount());
		
		// matching should be case-insensitive
		tags = tagDao.findTag("NEWS");
		assertEquals(0,tags.size());
	}
	
	public void testFindTagTopN() throws Exception{
		List<Tag> tags = tagDao.findTag(3);
		assertEquals(3,tags.size());
		assertEquals(2,tags.get(0).getId());
		assertEquals(3,tags.get(1).getId());
		assertEquals(5,tags.get(2).getId());
		
		tags = tagDao.findTag(1);
		assertEquals(1,tags.size());
		assertEquals(2,tags.get(0).getId());
		
		tags = tagDao.findTag(1000);
		assertEquals(3,tags.size());
	}
	
	public void testFindTagTopNSortByAlpha() throws Exception{
		List<Tag> results = tagDao.findTag(3,TagDao.SORT_ALPH);
		assertEquals(3,results.size());
		assertEquals("jojo",results.get(0).getLabel());
		assertEquals("news",results.get(1).getLabel());
		assertEquals("pop",results.get(2).getLabel());
	}
	
	public void testFindTagNSortByFreq() throws Exception{
		List<Tag> results = tagDao.findTag(3,TagDao.SORT_FREQ);
		assertEquals(3,results.size());
		assertEquals("news",results.get(0).getLabel());
		assertEquals("jojo",results.get(1).getLabel());
		assertEquals("pop",results.get(2).getLabel());
	}
	
	
	public void testFindUserTag() throws Exception{
		User user = new User(1);
		Tag tag = new Tag(1);
		List<UserTag> userTags = tagDao.findUserTag(user, tag);
		assertEquals(1,userTags.size());
		
		UserTag ut = userTags.get(0);
		assertEquals((1),ut.getUser().getId());
		assertEquals((1),ut.getTag().getId());
		
		userTags = tagDao.findUserTag(user);
		assertEquals(3,userTags.size());
		ut = userTags.get(0);
		assertEquals((1),ut.getUser().getId());
		ut = userTags.get(1);
		assertEquals((1),ut.getUser().getId());
	}
	
	public void testFindUserTagMinFreq() throws Exception{
		User user = new User(1);
		List<UserTag> tags = tagDao.findUserTag(user,0,TagDao.SORT_FREQ);
		assertEquals(3,tags.size());
		UserTag ut1 = tags.get(0);
		UserTag ut2 = tags.get(1);
		UserTag ut3 = tags.get(2);
		assertTrue(ut1.getCount()>ut2.getCount());
		
		tags = tagDao.findUserTag(user,0,TagDao.SORT_ALPH);
		assertEquals(3,tags.size());
		ut1 = tags.get(0);
		ut2 = tags.get(1);
		ut3 = tags.get(2);
		assertEquals("cnn",ut1.getTag().getLabel());
		assertEquals("iehf", ut2.getTag().getLabel());
		assertEquals("news",ut3.getTag().getLabel());
		
		tags = tagDao.findUserTag(user,1);
		assertEquals(1,tags.size());
		
		tags = tagDao.findUserTag(user,20);
		assertEquals(0,tags.size());
	}
	
	public void testGetLinkTag() throws Exception{
		int id = (1);
		LinkTag linkTag = tagDao.getLinkTag(id);
		assertNotNull(linkTag);
		assertEquals((105),linkTag.getCount());
		assertEquals((1),linkTag.getLink().getId());
		assertEquals((1),linkTag.getTag().getId());
	}

	public void testGetTag() throws Exception{
		Tag tag = tagDao.getTag((2));
		assertEquals("news",tag.getLabel());
		assertEquals((100),tag.getCount());
	}
	
	public void testGetUserTag() throws Exception{
		int id = (3);
		UserTag userTag = tagDao.getUserTag(id);
		assertNotNull(userTag);
		assertEquals((55),userTag.getCount());
		assertEquals((2),userTag.getUser().getId());
		assertEquals((1),userTag.getTag().getId());
	}
	
	public void testUpdateLinkTag() throws Exception{
		int id = (1);
		LinkTag tag = tagDao.getLinkTag(id);
		tag.setCount((938));		
		assertTrue(tagDao.updateLinkTag(tag));
		tag = tagDao.getLinkTag(id);
		assertEquals((938),tag.getCount());
	}
	
	public void testUpdateTag() throws Exception{
		int id = (1);
		Tag tag = tagDao.getTag(id);
		tag.setLabel("foo");
		tag.setCount((1000));
		assertTrue(tagDao.updateTag(tag));
		tag = tagDao.getTag(id);
		assertEquals("foo",tag.getLabel());
		assertEquals((1000),tag.getCount());
	}
	
	public void testUpdateUserTag() throws Exception{
		int id = (1);
		UserTag tag = tagDao.getUserTag(id);
		tag.setCount((938));		
		assertTrue(tagDao.updateUserTag(tag));
		tag = tagDao.getUserTag(id);
		assertEquals((938),tag.getCount());
	}
	
	
	public void testAddTagCountOne() throws Exception{
		Tag tag4 = tagDao.getTag(4);
		assertEquals(0,tag4.getCount());
		
		UserTag uTag4 = tagDao.getUserTag(4000);
		assertEquals(0,uTag4.getCount());
		
		LinkTag lTag4 = tagDao.getLinkTag(400);
		assertEquals(0,lTag4.getCount());
		
		BookmarkTag bTag4 = tagDao.getBookmarkTag(40);
		assertEquals(0,bTag4.getCount());
				
		User user1 = new User(1);
		Link link1 = new Link(1);
		Bookmark bmark1 = new Bookmark(1);
				
		boolean[] opOkay = tagDao.addTagCountOne(new Tag[]{tag4}, user1, link1, bmark1);
		assertTrue(opOkay[0]);
		
		tag4 = tagDao.getTag(4);
		assertEquals(1,tag4.getCount());
		
		uTag4 = tagDao.getUserTag(4000);
		assertEquals(1,uTag4.getCount());
		
		lTag4 = tagDao.getLinkTag(400);
		assertEquals(1,lTag4.getCount());
		
		bTag4 = tagDao.getBookmarkTag(40);
		assertEquals(1,bTag4.getCount());
	}
	
	public void testAddTagCountOne2() throws Exception{
		BookmarkTag btag1 = tagDao.getBookmarkTag(1);
		assertEquals(1,btag1.getCount());
		
		boolean[] opOkay = tagDao.addTagCountOne(new Tag[]{new Tag(1)}, new User(1), new Link(1), new Bookmark(1));
		assertTrue(opOkay[0]);
		
		btag1 = tagDao.getBookmarkTag(1);
		assertEquals(1,btag1.getCount());
	}
	
	public void testSubtractTagCountOne() throws Exception{
		Tag tag = tagDao.getTag(2);
		BookmarkTag btag = tagDao.getBookmarkTag(2);
		LinkTag ltag = tagDao.getLinkTag(2);
		UserTag utag = tagDao.getUserTag(2);
		
		assertEquals(100,tag.getCount());
		assertEquals(1,btag.getCount());
		assertEquals(1,ltag.getCount());
		assertEquals(1,utag.getCount());
		
		boolean[] opOkay = tagDao.subtractTagCountOne(new Tag[]{tag}, new User(1), new Link(1), new Bookmark(1));
		assertTrue(opOkay[0]);
		
		tag = tagDao.getTag(2);
		btag = tagDao.getBookmarkTag(2);
		ltag = tagDao.getLinkTag(2);
		utag = tagDao.getUserTag(2);
		
		assertEquals(99,tag.getCount());
		assertEquals(0,btag.getCount());
		assertEquals(0,ltag.getCount());
		assertEquals(0,utag.getCount());
	}
}
