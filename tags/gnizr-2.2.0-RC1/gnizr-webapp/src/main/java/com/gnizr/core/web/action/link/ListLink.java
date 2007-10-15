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
package com.gnizr.core.web.action.link;

import java.util.ArrayList;
import java.util.List;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.web.action.AbstractPagingAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;

public class ListLink extends AbstractPagingAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7225786966121892724L;
	
	private List<Bookmark> bookmarks;
	private User loggedInUser;
	private String tag;
	private BookmarkPager bookmarkPager;
	
	public BookmarkPager getBookmarkPager() {
		return bookmarkPager;
	}

	public void setBookmarkPager(BookmarkPager bookmarkPager) {
		this.bookmarkPager = bookmarkPager;
	}

	@Override
	protected String go() throws Exception {
		initPagingAction();

		int ppc = getPerPageCount();
		int max = 1;
		try {
			Tag aTag = new Tag(tag);
			max = bookmarkPager.getMaxPageNumber(aTag,ppc);
			setTotalNumOfPages(max);		
			int offset = computeOffset(page);		
			bookmarks = bookmarkPager.pageBookmark(aTag,offset,ppc);
			setPreviousPageNum(getPage(),max);
			setNextPageNum(getPage(),max);			
		}catch(NoSuchTagException e){
			logger.debug("no such tag: " + tag);
		}catch(Exception e){
			logger.debug(e);
			return ERROR;
		}
		return SUCCESS;	
	}
	
	public String doListAllBookmarks() throws Exception{
		bookmarks = new ArrayList<Bookmark>();
		try{
			bookmarks = bookmarkPager.pageAllBookmark(new Tag(tag)).getResult();
		}catch(Exception e){
			logger.debug("error occured when paging bookmarks of tag: "+tag);
		}
		return SUCCESS;
	}
	
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

	public List<Bookmark> getBookmarks() {
		return bookmarks;
	}

	public void setBookmarks(List<Bookmark> bookmarks) {
		this.bookmarks = bookmarks;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

}
