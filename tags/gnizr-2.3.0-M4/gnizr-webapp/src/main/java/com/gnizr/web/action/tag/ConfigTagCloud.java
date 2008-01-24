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

import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.SessionConstants;
import com.opensymphony.webwork.interceptor.SessionAware;

public class ConfigTagCloud extends AbstractAction implements SessionAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2630093457677725889L;
	private static final Logger logger = Logger.getLogger(ConfigTagCloud.class);
	
	private Map session;
	
	@Override
	protected String go() throws Exception {
		return SUCCESS;
	}

	@SuppressWarnings("unchecked")
	public void setSortBy(String sortBy){
		if(sortBy != null){
			session.put(SessionConstants.TAG_SORT_BY, sortBy);
		}else{
			session.put(SessionConstants.TAG_SORT_BY, SessionConstants.SORT_ALPH);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setTagView(String viewOpt){
		if(viewOpt != null){
			session.put(SessionConstants.TAG_VIEW_OPT,viewOpt);
		}else{
			session.put(SessionConstants.TAG_VIEW_OPT,SessionConstants.VIEW_TAG_CLOUD);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setMinTagFreq(int min){
		if(min > 0){
			logger.debug("set minTagFreq = " + min);
			session.put(SessionConstants.MIN_TAG_FREQ,min);
		}else{
			logger.debug("set minTagFreq = 1");
			session.put(SessionConstants.MIN_TAG_FREQ,1);
		}
	}

	@SuppressWarnings("unchecked")
	public void setHideTagGroups(boolean hide){
		logger.debug("set hideTagGroups = " + hide);
		session.put(SessionConstants.HIDE_TAG_GROUPS,hide);
	}
	
	public void setSession(Map session) {
		this.session = session;		
	}
	
}
