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

import com.gnizr.core.web.action.AbstractTagCloudAction;
import com.gnizr.core.web.action.SessionConstants;
import com.gnizr.db.dao.UserTag;
import com.opensymphony.webwork.interceptor.SessionAware;

public class GetUserTagCloud extends AbstractTagCloudAction implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4575473819745171036L;
	private static final Logger logger = Logger.getLogger(GetUserTagCloud.class);
		
	// read-only data
	private List<UserTag> userTags;

	@Override
	protected String go() throws Exception {
		logger.debug("GetUserTagCloud.go()");
		super.resolveUser();
		int frq = getMinTagFreq();
		if(getSortBy().equalsIgnoreCase(SessionConstants.SORT_ALPH)){			
			userTags = getUserManager().getTagsSortByAlpha(user,frq);
		}else{
			userTags = getUserManager().getTagsSortByFreq(user,frq);
		}
		return SUCCESS;
	}

	public List<UserTag> getUserTags() {
		return userTags;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return false;
	}
	
}
