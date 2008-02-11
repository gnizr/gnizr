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
package com.gnizr.web.action.folder;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserStat;
import com.gnizr.web.action.AbstractAction;

public class UpdateOwnership extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7226020760163464345L;

	private static final Logger logger = Logger
			.getLogger(UpdateOwnership.class);

	private FolderManager folderManager;

	private BookmarkManager bookmarkManager;

	private UserManager userManager;

	private List<User> userInProcess = new ArrayList<User>();
	
	@Override
	protected String go() throws Exception {
		List<UserStat> stats = userManager.listUserStats();
		User gnizrUser = userManager.getUser("gnizr");
		for (UserStat s : stats) {
			try {
				String username = s.getUsername();
				User user = userManager.getUser(username);				
				userInProcess.add(user);				
				int fldrCnt = folderManager.getUserFolderCount(user);
				if (fldrCnt > 0) {
					DaoResult<Folder> folders = folderManager.pageUserFolders(
							user, 0, fldrCnt);
					for (Folder f : folders.getResult()) {
						int fldrSize = f.getSize();
						DaoResult<Bookmark> bookmarks = folderManager
								.pageFolderContent(user, f.getName(), 0,
										fldrSize);
						List<Bookmark> bmarks2add = bookmarks.getResult();
						List<Bookmark> bmarks2rmv = new ArrayList<Bookmark>();
						for (Bookmark bmrk : bmarks2add) {
							if (bmrk.getUser().getId() == gnizrUser.getId()) {
								bmarks2rmv.add(new Bookmark(bmrk.getId()));
								bmrk.setUser(user);
								bmrk.setId(-1);							
								int id = bookmarkManager.addBookmark(bmrk);
								if (id > 0) {
									bmrk.setId(id);
								}else{
									logger
									.error("failed claiming ownership on bookmark="
											+ bmrk + ", owner=" + user);
								}
							}
						}
						folderManager.addBookmarks(user,f.getName(),bmarks2add);
						folderManager.removeBookmarks(user,f.getName(),bmarks2rmv);
					}
				}
			} catch (Exception e) {
				logger.error(e);
			}
		}

		return SUCCESS;
	}

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public List<User> getUserInProcess() {
		return userInProcess;
	}

}
