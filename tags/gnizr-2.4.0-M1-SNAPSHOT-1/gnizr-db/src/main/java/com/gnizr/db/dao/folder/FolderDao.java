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
package com.gnizr.db.dao.folder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;

/**
 * @author harryc
 *
 */
public interface FolderDao extends Serializable{
	
	public static final int SORT_BY_BMRK_CREATED_ON = 1;
	public static final int SORT_BY_BMRK_LAST_UPDATED = 2;
	public static final int SORT_BY_BMRK_FLDR_LAST_UPDATED = 3;
	public static final int SORT_BY_USAGE_FREQ = 2;
	public static final int SORT_BY_ALPHA = 1;
	public static final int DESCENDING = 1;
	public static final int ASCENDING = 2;
	
	public int createFolder(Folder folder);
	
	public boolean updateFolder(Folder folder);
	
	public boolean deleteFolder(User owner, String folderName);
	
	public Folder getFolder(int id);

	public Folder getFolder(User owner, String folderName);
	
	public DaoResult<Folder> pageFolders(User owner, int offset, int count);
	
	public boolean[] addBookmarks(Folder folder, List<Bookmark> bookmarks, Date timestamp);
	
	public boolean[] removeBookmarks(Folder folder, List<Bookmark> bookmarks);
	
	public int removeAllBookmarks(Folder folder);
	
	public DaoResult<Bookmark> pageBookmarks(Folder folder, int offset, int count);
	
	public DaoResult<Bookmark> pageBookmarks(Folder folder, int offset, int count, int sortBy, int order);
	
	public DaoResult<Bookmark> pageBookmarks(Folder folder, Tag tag, int offset, int count);
	
	public DaoResult<Bookmark> pageBookmarks(Folder folder, Tag tag, int offset, int count, int sortBy, int order);
	
	public DaoResult<Folder> pageContainedInFolders(Bookmark bookmark, int offset, int count);
	
	public List<FolderTag> findTagsInFolder(Folder folder, int minFreq, int sortBy, int order);

	public boolean hasFolderTag(Folder folder, Tag tag);
	
	public Map<String,List<FolderTag>> listTagGroups(Folder folder, int minFreq, int sortBy, int order);
	
}
