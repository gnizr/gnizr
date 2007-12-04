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
package com.gnizr.core.util;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.User;

public class SecurityManager {

	public boolean isEditPermitted(User user,Bookmark bookmark){
		GnizrDaoUtil.checkNull(user);
		GnizrDaoUtil.checkNull(bookmark);
		GnizrDaoUtil.checkNull(bookmark.getUser());
		return (user.getId() == bookmark.getUser().getId());
	}
	
	public boolean isEditPermitted(User user,TagAssertion tagAssertion){
		GnizrDaoUtil.checkNull(user);
		GnizrDaoUtil.checkNull(tagAssertion);
		GnizrDaoUtil.checkNull(tagAssertion.getUser());
		return (user.getId() == tagAssertion.getUser().getId());
	}
	
	
}
