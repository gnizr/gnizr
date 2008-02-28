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
package com.gnizr.web.interceptor;

import java.util.Map;

import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.SessionConstants;
import com.gnizr.web.util.GnizrConfiguration;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;


public class ServerMaintenanceInterceptor implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5931856583951592704L;

	private GnizrConfiguration gnizrConfiguration;
	
	public GnizrConfiguration getGnizrConfiguration() {
		return gnizrConfiguration;
	}

	public void setGnizrConfiguration(GnizrConfiguration gnizrConfiguration) {
		this.gnizrConfiguration = gnizrConfiguration;
	}

	public void destroy() {
		// no code;
	}

	public void init() {
		// no code;		
	}

	@SuppressWarnings({ "unchecked", "unchecked" })
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		if(gnizrConfiguration != null){
			boolean isEnabled = gnizrConfiguration.isServerMaintenanceModeEnabled();
			if(isEnabled == true){
				Map session = actionInvocation.getInvocationContext().getSession();
				User user = (User)session.get(SessionConstants.LOGGED_IN_USER);
				if(user == null){
					return AbstractAction.MAINTENANCE;					
				}else{
					if("gnizr".equals(user.getUsername()) == false){
						return AbstractAction.MAINTENANCE;
					}
				}				
			}
		}
		return actionInvocation.invoke();		
	}

}
