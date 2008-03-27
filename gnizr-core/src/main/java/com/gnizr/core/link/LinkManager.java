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
package com.gnizr.core.link;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.user.UserDao;

public class LinkManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -45314859222232988L;
	private static final Logger logger = Logger.getLogger(LinkManager.class);
	
	private LinkDao linkDao;
	private BookmarkDao bookmarkDao;
	private UserDao userDao;
	
	public LinkManager(GnizrDao gnizrDao){
		this.linkDao = gnizrDao.getLinkDao();
		this.bookmarkDao = gnizrDao.getBookmarkDao();
		this.userDao = gnizrDao.getUserDao();
	}
	
	public Link getInfo(String urlHash){
		logger.debug("getInfo: urlHash="+urlHash);
		Link aLink = GnizrDaoUtil.getLinkByUrlHash(linkDao, urlHash);
		if(aLink != null){			
			return aLink;
		}
		return null;
	}
	
	public Bookmark getFirstMatchedBookmark(String url){
		logger.debug("getFirstMatchedBookmark: url" + url);
		Link aLink = GnizrDaoUtil.getLink(linkDao, url);
		if(aLink != null){
			DaoResult<Bookmark> result = bookmarkDao.pageBookmarks(aLink, 0, 1);
			if(result.getSize() > 0){
				return result.getResult().get(0);
			}
		}
		return null;
	}
	
	public List<Bookmark> getHistory(Link link) {
		logger.debug("getHistory: link="+link);
		List<Bookmark> result = new ArrayList<Bookmark>();
		GnizrDaoUtil.checkNull(link);
		Link aLink = new Link(link);
		try{
			GnizrDaoUtil.fillId(linkDao, aLink);
			int totalBmCount = bookmarkDao.getBookmarkCount(aLink);
			if(totalBmCount > 0){
				List<Bookmark> bmarks = bookmarkDao.pageBookmarks(aLink, 0, totalBmCount).getResult();
				for(Iterator<Bookmark> it = bmarks.iterator(); it.hasNext();){
					Bookmark bm = it.next();
					GnizrDaoUtil.fillObject(bookmarkDao, userDao, linkDao, bm);
					result.add(bm);
				}
			}		
			return result;
		}catch(Exception e){
			logger.debug(e);
		}
		return result;
	}
}
