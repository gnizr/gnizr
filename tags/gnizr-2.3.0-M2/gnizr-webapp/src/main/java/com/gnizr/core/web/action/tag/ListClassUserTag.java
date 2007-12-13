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
package com.gnizr.core.web.action.tag;

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.tag.TagPager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;

public class ListClassUserTag extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4916111194865232462L;
	private static final Logger logger = Logger.getLogger(ListClassUserTag.class);
	
	private User user;
	private String username;
	private List<UserTag> classTags;
	
	private TagPager tagPager;
	
	public List<UserTag> getClassTags() {
		return classTags;
	}

	public void setClassTags(List<UserTag> classTags) {
		this.classTags = classTags;
	}

	public TagPager getTagPager() {
		return tagPager;
	}

	public void setTagPager(TagPager tagPager) {
		this.tagPager = tagPager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("listClassUserTag: username="+username);
		if(tagPager == null){
			logger.error("missing tagPager object.");
			return ERROR;
		}
		try{
			if(user == null){
				user = new User(username);
			}
			classTags = tagPager.findUserTagClass(user);
			return SUCCESS;
		}catch(Exception e){
			logger.error(e);
		}		
		return ERROR;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
