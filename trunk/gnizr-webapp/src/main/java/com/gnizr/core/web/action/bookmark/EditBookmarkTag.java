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

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.managers.UserManager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.tag.TagsParser;

public class EditBookmarkTag extends AbstractAction implements LoggedInUserAware{

	private static final Logger logger = Logger.getLogger(EditBookmarkTag.class);
	private static final long serialVersionUID = -8665695143631497741L;
	private User loggedInUser;	
	private String tag;
	private String newTag;
	private List<UserTag> userTags;
	private BookmarkManager bookmarkManager;
	private UserManager userManager;

	public static final String DELETE_TAG_FAILED = "DELETE_TAG_FAILED";
	public static final String RENAME_TAG_FAILED = "RENAME_TAG_FAILED";
	
	public List<UserTag> getUserTags() {
		return userTags;
	}

	public void setUserTags(List<UserTag> userTags) {
		this.userTags = userTags;
	}

	public String getNewTag() {
		return newTag;
	}

	public void setNewTag(String newTag) {
		this.newTag = newTag;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	protected String go() throws Exception {
		if(tag == null){
			return fetchEditData();
		}else{
			String opOkay = ERROR;
			if(newTag !=  null){
				opOkay = doRename();
			}else{
				opOkay = doDelete();
			}
			if(SUCCESS == opOkay){
				return fetchEditData();
			}else{
				return opOkay;
			}			
		}		
	}
	
	private String doRename(){
		String[] ntags = parseTags(newTag);
		if(ntags.length > 0){
			try{
				boolean okay = bookmarkManager.renameTag(loggedInUser,tag,ntags);
				if(okay == false){
					addActionMessage(RENAME_TAG_FAILED);
					return INPUT;
				}
			}catch(Exception e){
				addActionError("operation error. check log.");
				logger.error(e);
				return ERROR;
			}
		}else{
			addActionMessage("No new tag is defined. The tag has not been renamed.");		
		}
		return SUCCESS;
	}
	
	private String doDelete(){
		if(isValidTag(tag)){
			try{
				boolean okay = bookmarkManager.deleteTag(loggedInUser,tag);
				if(okay == false){
					addActionMessage(DELETE_TAG_FAILED);
					return INPUT;
				}
			}catch(Exception e){
				addActionError("operation error. check log.");
				logger.error(e);
				return ERROR;
			}
		}
		return SUCCESS;
	}

	private String[] parseTags(String tags){
		TagsParser parser = new TagsParser(tags);
		return parser.getTags().toArray(new String[0]);
	}
	
	private boolean isValidTag(String tag){
		if(tag != null && tag.length()>0){
			return true;
		}
		return false;
	}
	
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

	private String fetchEditData() {
		try{
			userTags = userManager.getTagsSortByAlpha(loggedInUser,0);
		}catch(Exception e){
			logger.error(e);
			return ERROR;
		}
		return SUCCESS;		
	}

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}


}
