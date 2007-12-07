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
package com.gnizr.core.folder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.GnizrDao;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.bookmark.GeometryMarkerDao;
import com.gnizr.db.dao.folder.FolderDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.user.UserDao;

public class FolderManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1477166455078075280L;
	private static final Logger logger = Logger.getLogger(FolderManager.class);
	
	public static final String MY_BOOKMARKS_LABEL = "_my_";
	public static final String IMPORTED_BOOKMARKS_LABEL = "_import_";	
	
	public static final int DESCENDING_ORDER = FolderDao.DESCENDING;
	public static final int ASCENDING_ORDER = FolderDao.ASCENDING;
	
	private FolderDao folderDao;
	private UserDao userDao;
	private BookmarkDao bookmarkDao;
	private LinkDao linkDao;
	private TagDao tagDao;
	private GeometryMarkerDao geomMarkerDao;
	
	public FolderManager(GnizrDao gnizrDao){
		this.folderDao = gnizrDao.getFolderDao();
		this.userDao = gnizrDao.getUserDao();
		this.bookmarkDao = gnizrDao.getBookmarkDao();
		this.linkDao = gnizrDao.getLinkDao();
		this.tagDao = gnizrDao.getTagDao();
		this.geomMarkerDao = gnizrDao.getGeometryMarkerDao();
	}
	
	public DaoResult<Folder> pageUserFolders(User user, int offset, int count) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		return folderDao.pageFolders(user, offset, count);
	}
	
	public int getUserFolderCount(User user) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		DaoResult<Folder> result = folderDao.pageFolders(user, 0, 0);
		return result.getSize();
	}
	
	public Folder createFolder(User user, String folderName, String description) throws NoSuchUserException{
		Folder folder = new Folder();		
		GnizrDaoUtil.fillId(userDao, user);		
		folder.setUser(user);
		if(folderName == null || folderName.length()==0){
			throw new IllegalArgumentException("Defined folder name string is NULL or its length() is 0");
		}else{
			folder.setName(folderName);
		}
		if(description == null){
			folder.setDescription("");
		}else{
			folder.setDescription(description);
		}
		folder.setLastUpdated(GnizrDaoUtil.getNow());
		
		int fid = folderDao.createFolder(folder);
		folder = folderDao.getFolder(fid);
		return folder;
	}
	
	public DaoResult<Bookmark> pageFolderContent(User user, String folderName, int offset, int count) throws NoSuchUserException{
		DaoResult<Bookmark> result = null;		
		GnizrDaoUtil.fillId(userDao, user);
		if(folderName == null){
			throw new NullPointerException("folder name is NULL");
		}
		Folder folder = folderDao.getFolder(user, folderName);
		if(folder != null){
			result = folderDao.pageBookmarks(folder, offset, count);
		}
		return result;		
	}
	
	public DaoResult<Bookmark> pageFolderContent(User user, String folderName, String tag, int offset, int count) throws NoSuchUserException, NoSuchTagException{
		DaoResult<Bookmark> result = null;		
		GnizrDaoUtil.fillId(userDao, user);
		if(folderName == null){
			throw new NullPointerException("folder name is NULL");
		}
		Folder folder = folderDao.getFolder(user, folderName);
		Tag tagObj = new Tag(tag);
		GnizrDaoUtil.fillId(tagDao, tagObj);
		if(folder != null){			
			result = folderDao.pageBookmarks(folder, tagObj, offset, count);
		}
		return result;		
	}
	
	public boolean[] addToMyBookmarks(User user, List<Bookmark> bookmarks2add) throws NoSuchUserException {
		return  this.addBookmarks(user,MY_BOOKMARKS_LABEL, bookmarks2add);
	}
	
	public boolean addToMyBookmarks(User user, Bookmark bookmark) throws NoSuchUserException {
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		if(bookmark != null){
			bmarks.add(bookmark);
			boolean[] opOkay = this.addBookmarks(user,MY_BOOKMARKS_LABEL, bmarks);
			return opOkay[0];
		}
		throw new NullPointerException("bookmark is null");
	}
	
	public boolean[] addBookmarks(User user, String folderName, List<Bookmark> bookmarks2add) throws NoSuchUserException{			
		GnizrDaoUtil.fillId(userDao, user);
		if(folderName == null){
			throw new NullPointerException("folder name is NULL");
		}
		Folder folder = folderDao.getFolder(user, folderName);
		if(folder == null){
			folder = createFolder(user, folderName, "");
		}
		if(bookmarks2add == null){
			logger.warn("request to add bookmarks, but input bookmark list is NULL");
			return null;
		}else{
			return folderDao.addBookmarks(folder, bookmarks2add, GnizrDaoUtil.getNow());
		}		
	}
	
	public boolean[] removeBookmarks(User user, String folderName, List<Bookmark> bookmarks2remove) throws NoSuchFolderException, NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		if(folderName == null){
			throw new NullPointerException("folder name is NULL");
		}
		Folder folder = folderDao.getFolder(user, folderName);
		if(folder == null){
			throw new NoSuchFolderException("no such folder: "+ folderName);
		}
		if(bookmarks2remove == null){
			logger.warn("request to add bookmarks, but input bookmark list is NULL");
			return null;
		}else{
			return folderDao.removeBookmarks(folder, bookmarks2remove);
		}	
	}
	
	public Folder getUserFolder(User owner, String folderName) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, owner);
		if(folderName == null){
			throw new NullPointerException("folder name is NULL");
		}
		return folderDao.getFolder(owner, folderName);
	}
	
	public boolean deleteUserFolder(User owner, String folderName) throws Exception{
		GnizrDaoUtil.fillId(userDao, owner);
		if(folderName == null){
			throw new NullPointerException("folder name is NULL");
		}
		return folderDao.deleteFolder(owner, folderName);
	}

	
	public boolean updateFolder(User owner, Folder folder) throws Exception{
		GnizrDaoUtil.fillId(userDao, owner);
		if(folder == null){
			throw new NullPointerException("folder  is NULL");
		}
		return folderDao.updateFolder(folder);
	}
	
	public int purgeFolder(User owner, String folderName) throws Exception{
		GnizrDaoUtil.fillId(userDao, owner);
		if(folderName == null){
			throw new NullPointerException("folder name  is NULL");
		}
		Folder f = getUserFolder(owner, folderName);
		if(f.getSize() > 0){			
			return folderDao.removeAllBookmarks(f);
		}else{
			return 0;
		}
	}
	
	public DaoResult<Folder> pageContainedInFolder(User user, String url, int offset, int count) throws NoSuchUserException, NoSuchLinkException, MissingIdException, NoSuchBookmarkException{
		GnizrDaoUtil.fillId(userDao, user);
		if(url == null){
			throw new NullPointerException("url is null");
		}	
		Bookmark bmark = new Bookmark();
		bmark.setLink(new Link(url));
		bmark.setUser(user);
		GnizrDaoUtil.fillId(bookmarkDao, userDao, linkDao, bmark);
		return folderDao.pageContainedInFolders(bmark, offset, count);
	}
	
	public List<FolderTag> getTagsSortByAlpha(Folder folder, int minFreq, int order){		
		return folderDao.findTagsInFolder(folder, minFreq, FolderDao.SORT_BY_ALPHA, order);
	}
	
	public List<FolderTag> getTagsSortByFreq(Folder folder, int minFreq, int order){
		return folderDao.findTagsInFolder(folder, minFreq,FolderDao.SORT_BY_USAGE_FREQ,order);
	}
	
	public Map<String, List<FolderTag>> getTagGroupsSortByAlpha(Folder folder, int minFreq, int order){
		return folderDao.listTagGroups(folder, minFreq, FolderDao.SORT_BY_ALPHA,order);
	}
	
	public Map<String, List<FolderTag>> getTagGroupsSortByFreq(Folder folder, int minFreq, int order){
		return folderDao.listTagGroups(folder, minFreq, FolderDao.SORT_BY_USAGE_FREQ,order);
	}
	
	public DaoResult<Bookmark> pageBookmarkHasGeomMarker(Folder folder, int offset, int count){
		if(folder == null){
			throw new NullPointerException("folder is null");
		}
		return geomMarkerDao.pageBookmarksInFolder(folder, offset, count);		
	}
}
