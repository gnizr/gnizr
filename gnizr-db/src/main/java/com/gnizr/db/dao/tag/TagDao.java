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

import java.io.Serializable;
import java.util.List;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public interface TagDao extends Serializable{
	
	public static final int SORT_ALPH = 1;
	public static final int SORT_FREQ = 2;
	
	public int createTag(Tag tag);
	public Tag getTag(int tid);
	public boolean deleteTag(int id);
	public boolean updateTag(Tag tag);
	public List<Tag> findTag(String tag);
	public List<Tag> findTag(int topN);
	public List<Tag> findTag(int topN, int sort_by);
		
	public int createUserTag(UserTag tag);
	public UserTag getUserTag(int id);
	public boolean deleteUserTag(int id);
	public boolean updateUserTag(UserTag tag);
	public List<UserTag> findUserTag(User user);
	public List<UserTag> findUserTag(User user, Tag tag);
	public List<UserTag> findUserTag(User user, int minFreq);
	public List<UserTag> findUserTag(User user, int minFreq, int sortBy);	
	
	public int createLinkTag(LinkTag tag);
	public LinkTag getLinkTag(int id);
	public boolean deleteLinkTag(int id);
	public boolean updateLinkTag(LinkTag tag);
	public List<LinkTag> findLinkTag(Link link, Tag tag);
	public List<LinkTag> findLinkTag(Link link, int minFreq);

	public DaoResult<LinkTag> pageLinkTagSortByFreq(Link link, int offset, int count);

	public int createBookmarkTag(BookmarkTag tag);
	public BookmarkTag getBookmarkTag(int id);
	public boolean deleteBookmarkTag(int id);
	public boolean updateBookmarkTag(BookmarkTag tag);
	public int getBookmarkTagId(Bookmark bookmark, Tag tag);	
	public List<BookmarkTag> findBookmarkTag(User user);	
	public List<BookmarkTag> findBookmarkTag(Folder folder);	
	public List<BookmarkTag> findBookmarkTagCommunitySearch(String searchQuery);
   
	public boolean[] addTagCountOne(Tag[] tags, User user, Link link, Bookmark bookmark);

	public boolean[] subtractTagCountOne(Tag[] tags, User user, Link link, Bookmark bookmark); 
	
	public boolean[] expandTag(User user, Tag fromTag, Tag[] toTags);
	
	public boolean[] reduceTag(User user, Tag[] tag);
	
}
