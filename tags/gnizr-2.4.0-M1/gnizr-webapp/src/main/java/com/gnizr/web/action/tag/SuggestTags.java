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
package com.gnizr.web.action.tag;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NoSuchUserTagException;
import com.gnizr.core.tag.TagManager;
import com.gnizr.core.tag.TagPager;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.web.action.AbstractAction;

public class SuggestTags extends AbstractAction{


	private static final long serialVersionUID = -54833670240626415L;

	private static final Logger logger = Logger.getLogger(SuggestTags.class.getName());
	
	private TagPager tagPager;
	private TagManager tagManager;
	private List<UserTag> skosRelatedTags;
	private List<UserTag> skosNarrowerTags;
	private List<UserTag> skosBroaderTags;
	private User user;
	private UserTag userTag;
	private String tag;

	public SuggestTags(){
		super();
		skosRelatedTags = new ArrayList<UserTag>();
		skosNarrowerTags = new ArrayList<UserTag>();
		skosBroaderTags = new ArrayList<UserTag>();
	}
		
	public TagPager getTagPager() {
		return tagPager;
	}

	public void setTagPager(TagPager tagPager) {
		this.tagPager = tagPager;
	}
	
	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	@Override
	protected String go() throws Exception {
		if(tagPager == null){
			logger.error("tagPager object is NULL");
			return ERROR;
		}
		if(user == null){
			logger.error("unable to suggest: no user is defined");
			return ERROR;
		}
		try{
			if(user != null && tag != null){
				userTag = tagManager.getUserTag(user, new Tag(tag));
			}
			if(userTag != null){
				List<UserTag> ut = null;
				
				ut = tagPager.findSKOSRelated(user, userTag);
				skosRelatedTags.addAll(ut);
				logger.debug("suggestTags: user="+user+",userTag="+userTag+",suggest_skos_related="+skosRelatedTags);

				ut = tagPager.findSKOSNarrower(user,userTag);
				skosNarrowerTags.addAll(ut);
				
				ut = tagPager.findSKOSBroader(user,userTag);
				skosBroaderTags.addAll(ut);
			}			
		}catch(NoSuchUserException e){
			logger.debug("unable to suggest: no such user. " + e.getMessage());
		}catch(NoSuchTagException e){
			logger.debug("unable to suggest: no such tag. " + e.getMessage());
		}catch(NoSuchUserTagException e){
			logger.debug("unable to suggest: no such user tag. " + e.getMessage());
		}catch(Exception e){
			logger.error("error: " + e.getMessage());
		}
		return SUCCESS;
	}
	
	public List<UserTag> getSkosBroaderTags() {
		return skosBroaderTags;
	}

	public List<UserTag> getSkosNarrowerTags() {
		return skosNarrowerTags;
	}

	public List<UserTag> getSkosRelatedTags() {
		return skosRelatedTags;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserTag getUserTag() {
		return userTag;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

}
