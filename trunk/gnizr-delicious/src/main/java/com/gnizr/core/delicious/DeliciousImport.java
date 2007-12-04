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
package com.gnizr.core.delicious;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.UsernameTakenException;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.User;

import del.icio.us.Delicious;
import del.icio.us.beans.Post;

public class DeliciousImport {

	private static final Logger logger = Logger.getLogger(DeliciousImport.class);

	private String dUsername;

	private String dPassword;

	private User gUser;

	private UserManager userManager;

	private BookmarkManager bmarkManager;
	
	private FolderManager folderManager;

	private boolean createUser;
	
	public DeliciousImport(String dUsername, String dPassword, User gUser,
			UserManager userManager, BookmarkManager bookmarkManager, FolderManager folderManager, boolean createUser) {
		this.dUsername = dUsername;
		this.dPassword = dPassword;
		this.gUser = gUser;
		this.bmarkManager = bookmarkManager;
		this.userManager = userManager;
		this.folderManager = folderManager;
		this.createUser = createUser;		
	}
	
	public DeliciousImport(String dUsername, String dPassword, User gUser, UserManager userManager, BookmarkManager bookmarkManager, FolderManager folderManager){
		this(dUsername,dPassword,gUser,userManager,bookmarkManager,folderManager,false);
	}

	public ImportStatus doImport() throws NoSuchUserException, UsernameTakenException{
		ImportStatus status = new ImportStatus();
		
		if (createUser == true) {
			if(userManager.hasUser(gUser) == false){
				createUser();
			}
		}
		
		int numAdded = 0;
		int numUpdated = 0;
		List<Bookmark> addToMyBookmarks = new ArrayList<Bookmark>();
		Delicious dConn = new Delicious(dUsername, dPassword);
		logger.debug("created an access object to del.icio.us");
		List posts = dConn.getAllPosts();
		if (posts != null && posts.size() > 0) {
			for(Iterator it = posts.iterator(); it.hasNext();) {
				Post aPost = (Post) it.next();
				Bookmark bmark = createBookmark(aPost);
				int bmid = bmarkManager.getBookmarkId(gUser,bmark.getLink().getUrl());
				if(bmid <= 0){
					try {
						bmid = bmarkManager.addBookmark(bmark);
						if(bmid > 0){
							addToMyBookmarks.add(new Bookmark(bmid));
							numAdded++;							
						}					
					} catch(Exception e){
						logger.debug(e);
					}					
				}else{
					try {
						bmark.setId(bmid);
						boolean isOkay = bmarkManager.updateBookmark(bmark);
						if(isOkay == true){
							numUpdated++;
						}
					} catch (Exception e) {
						logger.debug(e);
					} 
				}
			}
			if(addToMyBookmarks.isEmpty() == false){
				folderManager.addToMyBookmarks(gUser, addToMyBookmarks);
			}
		}
		
		status.setTotalNumber(posts.size());
		status.setNumberAdded(numAdded);
		status.setNumberUpdated(numUpdated);
		int numError = status.getTotalNumber() - numAdded - numUpdated;
		status.setNumberError(numError);
		
		return status;
	}

	private void createUser() throws UsernameTakenException {
		if (userManager.createUser(gUser) == false) {
			System.out.println("Error: unable to create user. DB error.");
			System.exit(1);
		} else {
			gUser = userManager.getUser(gUser.getUsername(), gUser
					.getPassword());
		}
	}

	private Bookmark createBookmark(Post post) {
		String href = post.getHref();
		String title = post.getDescription();
		String cmt = post.getExtended();
		String tag = post.getTag();
		Date createdOn = post.getTimeAsDate();

		Bookmark gBookmark = new Bookmark();
		gBookmark.setTitle(title);
		gBookmark.setNotes(cmt);
		gBookmark.setTags(tag);
		gBookmark.setLink(new Link(href));
		gBookmark.setUser(gUser);
		gBookmark.setCreatedOn(createdOn);
		return gBookmark;
	}
}
