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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NoSuchUserTagException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.user.UserDao;

public class BookmarkPager implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8089078507222123887L;

	private static final Logger logger = Logger.getLogger(BookmarkPager.class);

	private BookmarkDao bookmarkDao;

	private UserDao userDao;

	private TagDao tagDao;
	
	private LinkDao linkDao;
	
	public BookmarkPager(GnizrDao gnizrDao) {
		this.bookmarkDao = gnizrDao.getBookmarkDao();
		this.userDao = gnizrDao.getUserDao();
		this.tagDao = gnizrDao.getTagDao();
		this.linkDao = gnizrDao.getLinkDao();
	}

	
	public int getMaxPageNumber(Tag tag, int perPageCount) throws NoSuchTagException{
		logger.debug("getMaxPageNumber: tag="+tag+",perPageCount="+perPageCount);
		int max = 1;
		Tag aTag = new Tag(tag);
		GnizrDaoUtil.fillId(tagDao, aTag);
		int numOfbmarks = bookmarkDao.getBookmarkCount(aTag);
		if (numOfbmarks > 0) {
			if (perPageCount > 0) {
				int tnp = numOfbmarks / perPageCount;
				if ((numOfbmarks % perPageCount) > 0) {
					tnp++;
				}
				if (tnp > 1) {
					max = tnp;
				}
			}
		}
		return max;
	}
	
	public int getMaxPageNumber(User user, Integer perPageCount) throws NoSuchUserException {
		logger.debug("getMaxPageNumber: gUser=" + user + ",perPageCount="
				+ perPageCount);
		int max = 1;
		GnizrDaoUtil.checkNull(user);
		User aUser = new User(user);
		GnizrDaoUtil.fillId(userDao,aUser);		
		int numOfBmarks = bookmarkDao.getBookmarkCount(aUser);
		if (numOfBmarks > 0) {
			if (perPageCount != null && perPageCount > 0) {
				int tnp = numOfBmarks / perPageCount;
				if ((numOfBmarks % perPageCount) > 0) {
					tnp++;
				}
				if (tnp > 1) {
					max = tnp;
				}
			}
		}
		return max;
	}

	public int getMaxPageNumber(UserTag tag, Integer perPageCount) throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException{
		logger.debug("getMaxPageNumber: userTag=" + tag
				+ ",perPageCount=" + perPageCount);
		int max = 1;
		GnizrDaoUtil.checkNull(tag);
		UserTag userTag = new UserTag(tag);
		GnizrDaoUtil.fillId(tagDao,userDao,userTag);
		GnizrDaoUtil.fillObject(tagDao,userDao,userTag);
		if (userTag != null) {
			int numOfBmarks = userTag.getCount();
			if (numOfBmarks > 0) {
				if (perPageCount != null && perPageCount > 0) {
					int tnp = numOfBmarks / perPageCount;
					if ((numOfBmarks % perPageCount) > 0) {
						tnp++;
					}
					if (tnp > 1) {
						max = tnp;
					}
				}
			}
		}
		return max;
	}

	public DaoResult<Bookmark> pageBookmark(User user, int offset,
			int count) throws NoSuchUserException, NoSuchLinkException, MissingIdException, NoSuchBookmarkException {
		logger.debug("pageGnizrBookmark: user=" + user + ",offset=" + offset
				+ ",count=" + count);
		GnizrDaoUtil.fillId(userDao,user);
		return bookmarkDao.pageBookmarks(user, offset, count);		
	}

	public DaoResult<Bookmark> pageBookmark(UserTag tag, int offset,
			int count) throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException, NoSuchLinkException, NoSuchBookmarkException {
		logger.debug("pageGnizrBookmark: userTag=" + tag
				+ ",offset=" + offset + ",count=" + count);
		GnizrDaoUtil.checkNull(tag);
		GnizrDaoUtil.checkNull(tag.getTag());
		GnizrDaoUtil.checkNull(tag.getUser());
		User aUser = tag.getUser();
		Tag aTag = tag.getTag();
		GnizrDaoUtil.fillId(userDao,aUser);
		GnizrDaoUtil.fillId(tagDao,aTag);		
		if (tag != null) {
			return bookmarkDao.pageBookmarks(aUser,aTag,offset,count);			
		}
		return new DaoResult<Bookmark>(new ArrayList<Bookmark>(),0);
	}
	
	public List<Bookmark> pageBookmark(Tag tag, int offset, int count) throws NoSuchTagException{
		logger.debug("pageLink: tag="+tag+",offset="+offset+",count="+count);		
		GnizrDaoUtil.checkNull(tag);
		//make a local copy
		Tag aTag = new Tag(tag);
		GnizrDaoUtil.fillId(tagDao,aTag);
		DaoResult<Bookmark> result = bookmarkDao.pageBookmarks(aTag, offset, count);
		return result.getResult();
	}
	
	public DaoResult<Bookmark> pageAllBookmark(User user) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		DaoResult<Bookmark> result = bookmarkDao.pageBookmarks(user,0,0);
		return bookmarkDao.pageBookmarks(user,0, result.getSize());
	}
	
	public DaoResult<Bookmark> pageAllBookmark(User user, Tag tag) throws NoSuchUserException, NoSuchTagException{
		GnizrDaoUtil.fillId(userDao, user);
		GnizrDaoUtil.fillId(tagDao, tag);
		DaoResult<Bookmark> result = bookmarkDao.pageBookmarks(user, tag,0,0);
		return bookmarkDao.pageBookmarks(user, tag,0, result.getSize());
	}
	
	public DaoResult<Bookmark> pageAllBookmark(Tag tag) throws NoSuchTagException{
		GnizrDaoUtil.fillId(tagDao, tag);
		DaoResult<Bookmark> result = bookmarkDao.pageBookmarks(tag,0, 0);
		return bookmarkDao.pageBookmarks(tag,0, result.getSize());
	}
	
	public DaoResult<Bookmark> pageAllBookmark(Link link) throws NoSuchLinkException{
		GnizrDaoUtil.fillId(linkDao, link);
		DaoResult<Bookmark> result = bookmarkDao.pageBookmarks(link, 0,0);
		return bookmarkDao.pageBookmarks(link,0,result.getSize());
	}
	
}
