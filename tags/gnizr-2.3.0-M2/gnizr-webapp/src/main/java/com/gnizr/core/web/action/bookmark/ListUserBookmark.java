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
package com.gnizr.core.web.action.bookmark;

import java.util.ArrayList;
import java.util.List;

import com.gnizr.core.bookmark.BookmarkPager;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.tag.TagManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.web.action.AbstractPagingAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class ListUserBookmark extends AbstractPagingAction implements LoggedInUserAware{


	private static final long serialVersionUID = -1050609319896851144L;

	private UserManager userManager;
	private BookmarkPager bookmarkPager;
	private TagManager tagManager;
	private List<Bookmark> gnizrBookmark;
	private String username;
	private String tag;
	
	// maybe used to other chained actions or a view template pages
	private User user;	
	private UserTag userTag;
	
	// user who is currently logged in 
	private User loggedInUser;	
	
	/**
	 * @return the loggedInUser
	 */
	public User getLoggedInUser() {
		return loggedInUser;
	}

	public ListUserBookmark(){
		gnizrBookmark = new ArrayList<Bookmark>();
		page = 1;		
	}
	
	
	private String doListBookmark(){
		int ppc = getPerPageCount();
		try {
			int offset = computeOffset(page);		
			DaoResult<Bookmark> result =  bookmarkPager.pageBookmark(user,offset,ppc);
			gnizrBookmark =	result.getResult();			
			int maxNumOfPages = computeMaxPageNumber(ppc,result.getSize());
			setTotalNumOfPages(maxNumOfPages);
			setPreviousPageNum(getPage(),maxNumOfPages);
			setNextPageNum(getPage(),maxNumOfPages);		
			if(gnizrBookmark.size() == 0){				
				return SUCCESS;
			}
		} catch(NoSuchUserException e){
			addActionError(NO_SAVED_BOOKMARK);
			return INPUT;
		} catch (Exception e){
			logger.error(e);
			return ERROR;
		}		
		return SUCCESS;	
	}

	private String doListTaggedBookmark() {			
		int ppc = getPerPageCount();	
		try {		
			int offset = computeOffset(page);
			DaoResult<Bookmark> result = bookmarkPager.pageBookmark(userTag,offset,ppc);
			gnizrBookmark = result.getResult();
			int maxNumOfPages = computeMaxPageNumber(ppc,result.getSize());
			setTotalNumOfPages(maxNumOfPages);
			setPreviousPageNum(getPage(),maxNumOfPages);
			setNextPageNum(getPage(),maxNumOfPages);	
			if(gnizrBookmark.size() == 0){
				addActionError(NO_SAVED_BOOKMARK);
			}
		} catch(NoSuchUserException e){
			addActionError(NO_SAVED_BOOKMARK);
			return INPUT;
		}catch(NoSuchTagException e){
			addActionError(NO_SAVED_BOOKMARK);
			return INPUT;
		} catch(Exception e){
			logger.error(e);
			return ERROR;
		}
		return SUCCESS;		
	}

	public List<Bookmark> getBookmark() {
		return gnizrBookmark;
	}	
	public BookmarkPager getBookmarkPager() {
		return bookmarkPager;
	}
	
	public String getTag() {
		return tag;
	}
	
	public User getUser(){
		return user;
	}
	
	public UserTag getUserTag(){
		return userTag;
	}
	
	private User getUser1(){
		if(user == null || (user.getUsername().equals(username) == false)){
			if(username != null){
				user = new User(username);
			}else if(loggedInUser != null){
				user = new User(loggedInUser);
				username = user.getUsername();
			}
			if(user != null){
				try {
					user = userManager.getUser(user.getUsername());
				} catch (NoSuchUserException e) {
					logger.debug(e);
				}
			}
		}
		return user;
	}	

	public UserManager getUserManager() {
		return userManager;
	}
	
	public String getUsername() {
		return username;
	}	
	
	private UserTag getUserTag1(){	
		if(userTag != null && tag != null){
			if(userTag.getTag() != null){
				if(tag.equals(userTag.getTag().getLabel())){
					return userTag;
				}
			}
		}else if(tag != null){
			User u = getUser();
			if(u != null){			
				try {
					userTag = tagManager.getUserTag(u,new Tag(tag));
				} catch (Exception e) {
					logger.debug(e);
				}
			}
		}
		return this.userTag;
	}
	
	@SuppressWarnings("unchecked")
	protected String go() throws Exception {	
		// default result to return is LOGIN
		String resultCode = LOGIN;
		
		initPagingAction();
		User u = getUser1();
		UserTag ut = getUserTag1();
		if(u != null && ut != null){
			resultCode = doListTaggedBookmark();
		}else if(u != null && ut == null){
			resultCode = doListBookmark();
		}
		
		// if either one of the 'do' methods returns
		// non-SUCCESS result, we check "quiet mode" flag.
		// if the flag is true, then suppress the error
		// and return SUCCESS.		
		if(SUCCESS.equals(resultCode) == false){
			if(isQuietMode() == false){
				// if both 'do' methods are not executed
				// because of missing u and ut, then
				// LOGIN is returned.
				return resultCode;
			}else{
				return SUCCESS;
			}
		}
		return SUCCESS;		
	}
	
	public void setBookmark(List<Bookmark> gnizrBookmark) {
		this.gnizrBookmark = gnizrBookmark;
	}

	public void setBookmarkPager(BookmarkPager bookmarkPager) {
		this.bookmarkPager = bookmarkPager;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}
	
	public String doListAllBookmarks() throws Exception{
		gnizrBookmark = bookmarkPager.pageAllBookmark(getUser1()).getResult();
		return SUCCESS;
	}

}
