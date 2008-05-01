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
package com.gnizr.web.action.bookmark;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.tag.TagManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.web.action.SessionConstants;
import com.opensymphony.xwork.ActionSupport;

public class TestListBookmark extends GnizrWebappTestBase{

	private BookmarkPager bookmarkPager;
	private UserManager userManager;
	private TagManager tagManager;
	private ListUserBookmark list;
	private Map session;
	
	protected void setUp() throws Exception {
		super.setUp();
		bookmarkPager = new BookmarkPager(getGnizrDao());
		userManager = new UserManager(getGnizrDao());
		tagManager = new TagManager(getGnizrDao());
		list = new ListUserBookmark();
		list.setBookmarkPager(bookmarkPager);	
		list.setUserManager(userManager);
		list.setTagManager(tagManager);
		session = new HashMap();
		list.setSession(session);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		list = null;
		session.clear();
	}

	@SuppressWarnings("unchecked")
	public void testListUserBookmark() throws Exception{
		list.setUsername("hchen1");
				
		String code = list.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		assertEquals(8,list.getBookmark().size());
		assertEquals(10,session.get(SessionConstants.PAGE_COUNT));
		
		session.put(SessionConstants.PAGE_COUNT,2);
		list.setUsername("hchen1");
		
		code = list.execute();
		assertEquals(ActionSupport.SUCCESS,code);
		assertEquals(2,list.getBookmark().size());
		assertEquals(2,session.get(SessionConstants.PAGE_COUNT));
		
		Map<Integer,Integer> cntMap = new HashMap<Integer, Integer>();
		session.put(SessionConstants.PAGE_COUNT,4);
		for(int i = 1; i <= 10; i++){
			list.setPage(i);			
			code = list.execute();
			int cnt = 0;
			List<Bookmark> bm = list.getBookmark();
			for(Iterator<Bookmark> it = bm.iterator(); it.hasNext();){
				cnt++;
				it.next();
			}
			cntMap.put(i,cnt);
		}
		assertEquals(new Integer(4),cntMap.get(1));
		assertEquals(new Integer(4),cntMap.get(2));
		assertEquals(new Integer(0),cntMap.get(3));
		assertEquals(new Integer(0),cntMap.get(4));
		assertEquals(new Integer(0),cntMap.get(10));
	}

	public void testListUserTaggedBookmark() throws Exception{
		list.setUsername("hchen1");
		list.setTag("webwork");	
		assertEquals(ActionSupport.SUCCESS,list.execute());
		assertEquals(2,list.getBookmark().size());
	}
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestListBookmark.class.getResourceAsStream("/TestListBookmark1-input.xml"));
	}

	
}
