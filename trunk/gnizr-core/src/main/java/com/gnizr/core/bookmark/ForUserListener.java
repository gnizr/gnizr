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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.foruser.ForUserManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.User;

public class ForUserListener implements BookmarkListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1612635182362330056L;

	private static final Logger logger = Logger.getLogger(ForUserListener.class);
	
	private ForUserManager forUserManager;
	private UserManager userManager;
	private static final Pattern fullSyntaxPattern = Pattern.compile("gn:for=([a-zA-Z0-9]+)");
	private static final Pattern altSyntaxPattern = Pattern.compile("for:([a-zA-Z0-9]+)");
	
	public ForUserListener(UserManager userManager, ForUserManager forUserManager){
		this.userManager = userManager;
		this.forUserManager = forUserManager;
	}
	
	private void process(Bookmark bookmark) throws Exception {
		List<MachineTag> machineTags = bookmark.getMachineTagList();
		List<String> forUsers = getForUsers(machineTags);
		List<String> troubleUser = new ArrayList<String>();
		for(String username : forUsers){			
			try{
				User u = userManager.getUser(username);
				addForUserEntry(bookmark,u);
			}catch(Exception e){
				troubleUser.add(username);
			}			
		}		
		if(troubleUser.size() > 0){
			logger.debug("failed to ForUser records, no such users: "+troubleUser.toString());
		}
	}
	
	private void addForUserEntry(Bookmark bm, User u) throws NoSuchUserException, NoSuchLinkException, MissingIdException, NoSuchBookmarkException {
		if(forUserManager.hasForUser(bm, u) == false){
			ForUser forUserEntry = new ForUser(u,bm,"");
			forUserEntry.setCreatedOn(GnizrDaoUtil.getNow());
			int id = forUserManager.addForUser(forUserEntry);
			if(id <= 0){
				logger.error("db error. unable to create forUser: forUser="+forUserEntry);
			}
		}
	}
	
	private List<String> getForUsers(List<MachineTag> machineTags){
		List<String> users = new ArrayList<String>();
		for(MachineTag mt : machineTags){
			String mtStr = mt.toString();
			Matcher matcher = altSyntaxPattern.matcher(mtStr);
			if(matcher.matches()){
				users.add(matcher.group(1));
			}else{
				matcher = fullSyntaxPattern.matcher(mtStr);
				if(matcher.matches()){
					users.add(matcher.group(1));
				}
			}
		}
		return users;
	}

	public void notifyAdded(BookmarkManager manager,Bookmark bookmark) throws Exception {
		process(bookmark);		
	}

	public void notifyDeleted(BookmarkManager manager,Bookmark bookmark) throws Exception {
		// no code
		// taken care by DB's foreign key construct
	}

	public void notifyUpdated(BookmarkManager manager,Bookmark oldBookmark, Bookmark newBookmark) throws Exception {
		process(newBookmark);		
	}

}
