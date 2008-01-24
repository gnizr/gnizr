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

import com.gnizr.web.action.GnizrConfigurationAware;
import com.gnizr.web.util.GnizrConfiguration;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

public class ConfigurationInterceptor implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4853144559009823867L;

	private GnizrConfiguration gnizrConfiguration;
	
	public GnizrConfiguration getGnizrConfiguration() {
		return gnizrConfiguration;
	}

	public void setGnizrConfiguration(GnizrConfiguration gnizrConfiguration) {
		this.gnizrConfiguration = gnizrConfiguration;
	}

	public void destroy() {
		// no code		
	}

	public void init() {
		// no code		
	}

	public String intercept(ActionInvocation actionInvocation) throws Exception {
		Action action = (Action)actionInvocation.getAction();
		if(action instanceof GnizrConfigurationAware){
			((GnizrConfigurationAware)action).setGnizrConfiguration(gnizrConfiguration);
		}
		return actionInvocation.invoke();
	}
}
