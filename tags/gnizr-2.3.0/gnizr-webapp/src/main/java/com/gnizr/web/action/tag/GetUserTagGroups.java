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

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.UserTag;
import com.gnizr.web.action.AbstractTagCloudAction;
import com.gnizr.web.action.SessionConstants;

public class GetUserTagGroups extends AbstractTagCloudAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5640145374429147726L;

	private static final Logger logger = Logger.getLogger(GetUserTagGroups.class);
	
	// read-only data
	private Map<String, List<UserTag>> userTagGroups;
	
	@Override
	protected String go() throws Exception {
		super.resolveUser();
		logger.debug("GetUserTagGroups.go() ");
		int frq = getMinTagFreq();
		if(getSortBy().equalsIgnoreCase(SessionConstants.SORT_ALPH)){
			userTagGroups = userManager.getTagGroupsSortByAlpha(user,frq);
		}else{
			userTagGroups = userManager.getTagGroupsSortByFreq(user, frq);
		}
		return SUCCESS;
	}


	public Map<String, List<UserTag>> getUserTagGroups() {
		return userTagGroups;
	}

	public String doGetAll() throws Exception{
		super.resolveUser();
		logger.debug("GetUserTagGroups.doGetAll() ");
		if(getSortBy().equalsIgnoreCase(SessionConstants.SORT_ALPH)){
			userTagGroups = userManager.getTagGroupsSortByAlpha(user,1);
		}else{
			userTagGroups = userManager.getTagGroupsSortByFreq(user,1);
		}
		return SUCCESS;
	}


	@Override
	protected boolean isStrictLoggedInUserMode() {
		return false;
	}
}
