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

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NoSuchUserTagException;
import com.gnizr.core.tag.TagManager;
import com.gnizr.core.tag.TagPager;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.TagUtil;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.tag.TagsParser;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.LoggedInUserAware;

public class EditUserTag extends AbstractAction implements LoggedInUserAware {

	private static final Logger logger = Logger.getLogger(EditUserTag.class);

	private static final long serialVersionUID = 7550363395870979713L;

	private String instanceTags;

	private String relatedTags;

	private String broaderTags;

	private String narrowerTags;

	private String tag;

	private UserTag editUserTag;

	private User loggedInUser;

	private TagManager tagManager;
	
	private UserManager userManager;

	private TagPager tagPager;
	
	private List<UserTag> userTags;
	
	private int classTagId;

	/**
	 * The name of the property to be edited (e.g., relatedTags,
	 * narrowerTags, broaderTags or classTags)
	 */
	private String editValueName;
	
	public String doDeleteClassTag() throws Exception{
		String op = SUCCESS;
		if(classTagId > 0 && loggedInUser != null){
			if(tagManager.deleteRDFTypeClassAssertions(loggedInUser, new UserTag(classTagId)) == false){
				op = ERROR;
			}
		}else{
			op = INPUT;
		}
		return op;		
	}
	
	
	/**
	 * Saves <code>tag</code> property changes that a user has made.
	 */
	protected String go() throws Exception {
		logger.debug("Entering EditUserTag.go()...");
		if (tag != null) {
			if(isInputTagValid(tag) == false){
				return INPUT;
			}			
			if(SUCCESS == doSave()){
				addActionMessage("saved successfully!");
				return fetchEditData();
			}else{
				logger.error("unable to save edit data: "+editUserTag);
				return ERROR;
			}
		}
		logger.debug("Leaving EditUserTag.go().");
		return INPUT;
	}

	
	private boolean isInputTagValid(String tag){
		boolean inputOkay = true;
		TagsParser parser = new TagsParser(tag);
		List<String> parsedTags = parser.getTags();
		if(parsedTags.size() == 0){
			inputOkay = false;
			addActionMessage("No tag is defined.");
		}else if(parsedTags.size() > 1){
			inputOkay = false;
			addActionMessage("No spaces are allowed in a new tag.");
		}else{
			String s = parsedTags.get(0);
			if(s.length() == 0){
				inputOkay = false;
				addActionMessage("No tag is defined.");
			}
		}
		return inputOkay;
	}
	
	private String doSave() {
		editUserTag = tagManager.createUserTag(loggedInUser, tag);
		try {			
			if(relatedTags != null){
				if(doSaveRelatedTags() == false){
					return ERROR;
				}
			}
			if(broaderTags != null){
				if(doSaveBroaderTags() == false){
					return ERROR;
				}
			}
			if(narrowerTags != null){
				if(doSaveNarrowerTags() == false){
					return ERROR;
				}
			}
			if(instanceTags != null){
				if(doSaveInstanceTags() == false){
					return ERROR;
				}
			}
		} catch (Exception e) {
			logger.error(e);
			return ERROR;
		}
		return SUCCESS;
	}

	private boolean doSaveRelatedTags() throws NoSuchUserException,
			NoSuchTagException, NoSuchUserTagException, MissingIdException {
		String oldTags = getRelatedTags(loggedInUser, editUserTag);
		String newTags = relatedTags;
		
		List<String> setOfNewTags = splitTags(newTags);
		List<String> setOfOldTags = splitTags(oldTags);
		
		// tags that exist in the set of new tags but don't
		// exist in the set of old tags will have their associated
		// tag assertations deleted
		List<String> tags2del = getStringNotInSetOne(setOfNewTags,setOfOldTags);
		
		// tags that exist in the set of old tags but don't 
		// exist in the set of new tags will have their associated
		// tag assertions added
		List<String> tags2add = getStringNotInSetOne(setOfOldTags,setOfNewTags);
		
		boolean isOkay = false;
		isOkay = tagManager.deleteSKOSRelatedTags(loggedInUser,editUserTag,tags2del);
		if(isOkay == true){
			isOkay = tagManager.addSKOSRelatedTags(loggedInUser, editUserTag, tags2add);
		}
		return isOkay;
	}

	private List<String> splitTags(String tags){
		TagsParser parser = new TagsParser(tags);
		return parser.getTags();
	}
	
	private boolean doSaveBroaderTags() throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException {
		String oldTags = getBroaderTags(loggedInUser, editUserTag);
		String newTags = broaderTags;
		
		List<String> setOfNewTags = splitTags(newTags);
		List<String> setOfOldTags = splitTags(oldTags);
		
		// tags that exist in the set of new tags but don't
		// exist in the set of old tags will have their associated
		// tag assertations deleted
		List<String> tags2del = getStringNotInSetOne(setOfNewTags,setOfOldTags);
		
		// tags that exist in the set of old tags but don't 
		// exist in the set of new tags will have their associated
		// tag assertions added
		List<String> tags2add = getStringNotInSetOne(setOfOldTags,setOfNewTags);
		
		boolean isOkay = false;
		isOkay = tagManager.deleteSKOSBroaderTags(loggedInUser,editUserTag,tags2del);
		if(isOkay == true){
			isOkay = tagManager.addSKOSBroaderTags(loggedInUser, editUserTag, tags2add);
		}
		return isOkay;
	}

	private boolean doSaveNarrowerTags() throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException {
		String oldTags = getNarrowerTags(loggedInUser, editUserTag);
		String newTags = narrowerTags;
		
		List<String> setOfNewTags = splitTags(newTags);
		List<String> setOfOldTags = splitTags(oldTags);
		
		// tags that exist in the set of new tags but don't
		// exist in the set of old tags will have their associated
		// tag assertations deleted
		List<String> tags2del = getStringNotInSetOne(setOfNewTags,setOfOldTags);
		
		// tags that exist in the set of old tags but don't 
		// exist in the set of new tags will have their associated
		// tag assertions added
		List<String> tags2add = getStringNotInSetOne(setOfOldTags,setOfNewTags);
		
		boolean isOkay = false;
		isOkay = tagManager.deleteSKOSNarrowerTags(loggedInUser,editUserTag,tags2del);
		if(isOkay == true){
			isOkay = tagManager.addSKOSNarrowerTags(loggedInUser, editUserTag, tags2add);
		}
		return isOkay;
	}

	private boolean doSaveInstanceTags() throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException {
		String oldTags = getInstanceTags(loggedInUser, editUserTag);
		String newTags = instanceTags;
		
		List<String> setOfNewTags = splitTags(newTags);
		List<String> setOfOldTags = splitTags(oldTags);
		
		// tags that exist in the set of new tags but don't
		// exist in the set of old tags will have their associated
		// tag assertations deleted
		List<String> tags2del = getStringNotInSetOne(setOfNewTags,setOfOldTags);
		
		// tags that exist in the set of old tags but don't 
		// exist in the set of new tags will have their associated
		// tag assertions added
		List<String> tags2add = getStringNotInSetOne(setOfOldTags,setOfNewTags);
		
		boolean isOkay = false;
		isOkay = tagManager.deleteRDFInstanceTags(loggedInUser,editUserTag,tags2del);
		if(isOkay == true){
			isOkay = tagManager.addRDFInstanceTags(loggedInUser, editUserTag, tags2add);
		}
		return isOkay;
	}

	private List<String> getStringNotInSetOne(List<String> setOne,
			List<String> setTwo) {
		List<String> notInSetOne = new ArrayList<String>();
		for (String s : setTwo) {
			if (setOne.contains(s) == false) {
				notInSetOne.add(s);
			}
		}
		return notInSetOne;
	}

	/**
	 * Fetches the tag data for editing properties of <code>tag</code>
	 * 
	 * @return
	 */
	public String fetchEditData() {		
		try {
			userTags = userManager.getTagsSortByAlpha(loggedInUser,0);
			if(tag != null){
				if(isInputTagValid(tag) == false){
					return INPUT;
				}
				editUserTag = tagManager.createUserTag(loggedInUser, tag);
				relatedTags = getRelatedTags(loggedInUser, editUserTag);
				broaderTags = getBroaderTags(loggedInUser, editUserTag);
				narrowerTags = getNarrowerTags(loggedInUser, editUserTag);
				instanceTags = getInstanceTags(loggedInUser,editUserTag);
			}else{
				return INPUT;
			}
			
		} catch (Exception e) {
			logger.error(e);
		}

		return SUCCESS;
	}

	private String getRelatedTags(User user, UserTag subjectTag)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, MissingIdException {
		StringBuilder sb = new StringBuilder();
		List<UserTag> result = tagPager.findSKOSRelated(user, subjectTag);
		for (UserTag ut : result) {
			sb.append(formatUserTag(user,ut));
			sb.append(" ");
		}
		return sb.toString().trim();
	}

	private String getBroaderTags(User user, UserTag subjectTag)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, MissingIdException {
		StringBuilder sb = new StringBuilder();
		List<UserTag> result = tagPager.findSKOSBroader(user, subjectTag);
		for (UserTag ut : result) {
			sb.append(formatUserTag(user,ut));
			sb.append(" ");
		}
		return sb.toString().trim();
	}

	private String getNarrowerTags(User user, UserTag subjectTag)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, MissingIdException {
		StringBuilder sb = new StringBuilder();
		List<UserTag> result = tagPager.findSKOSNarrower(user, subjectTag);
		for (UserTag ut : result) {
			sb.append(formatUserTag(user,ut));
			sb.append(" ");
		}
		return sb.toString().trim();
	}
	
	private String getInstanceTags(User user, UserTag subjectTag) throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException{
		StringBuilder sb = new StringBuilder();
		List<UserTag> result = tagPager.findUserTagInstance(user, subjectTag);
		for (UserTag ut : result) {
			sb.append(formatUserTag(user,ut));
			sb.append(" ");
		}
		return sb.toString().trim();
	}
	
	
	private String formatUserTag(User user, UserTag userTag){
		StringBuilder sb = new StringBuilder();
		if(userTag.getUser().getId() != user.getId()){
			sb.append("tag:");
			sb.append(userTag.getUser().getUsername());
			sb.append("/");
			sb.append(userTag.getTag().getLabel());
		}else{
			sb.append(userTag.getTag().getLabel());
		}
		return sb.toString();
	}

	public String getBroaderTags() {
		return broaderTags;
	}

	public void setBroaderTags(String broaderTags) {
		this.broaderTags = broaderTags;
	}

	public String getInstanceTags() {
		return instanceTags;
	}

	public void setInstanceTags(String classTags) {
		this.instanceTags = classTags;
	}

	public String getNarrowerTags() {
		return narrowerTags;
	}

	public void setNarrowerTags(String narrowerTags) {
		this.narrowerTags = narrowerTags;
	}

	public String getRelatedTags() {
		return relatedTags;
	}

	public void setRelatedTags(String relatedTags) {
		this.relatedTags = relatedTags;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = TagUtil.makeSafeTagString(tag);
	}

	public UserTag getEditUserTag() {
		return editUserTag;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;
	}

	public User getLoggedInUser() {
		return this.loggedInUser;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	public TagPager getTagPager() {
		return tagPager;
	}

	public void setTagPager(TagPager tagPager) {
		this.tagPager = tagPager;
	}

	public List<UserTag> getUserTags() {
		return userTags;
	}

	public String getEditValueName() {
		return editValueName;
	}

	public void setEditValueName(String editPropertyName) {
		this.editValueName = editPropertyName;
	}

	public int getClassTagId() {
		return classTagId;
	}


	public void setClassTagId(int classTagId) {
		this.classTagId = classTagId;
	}


	public UserManager getUserManager() {
		return userManager;
	}


	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

}
