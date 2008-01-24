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
package com.gnizr.web.action.user;

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.UserStat;
import com.gnizr.web.action.AbstractAction;

public class ListUserStats extends AbstractAction{

	private static final long serialVersionUID = -7144236096714897575L;
	private static final Logger logger = Logger.getLogger(ListUserStats.class);
	
	private List<UserStat> userStats;
	private UserManager userManager;	
	
	@Override
	protected String go() throws Exception {
		logger.debug("ListUser.go()");
		userStats = userManager.listUserStats();
		if(userStats == null){
			return ERROR;
		}
		return SUCCESS;		
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public List<UserStat> getUserStats() {
		return userStats;
	}

	public void setUserStats(List<UserStat> userStats) {
		this.userStats = userStats;
	}


}
