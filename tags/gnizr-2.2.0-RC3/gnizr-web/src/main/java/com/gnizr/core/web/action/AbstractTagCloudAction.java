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
package com.gnizr.core.web.action;

import java.util.Map;

import com.opensymphony.webwork.interceptor.SessionAware;

public abstract class AbstractTagCloudAction extends AbstractLoggedInUserAction implements SessionAware{

	protected Map session;
	
	public void setSession(Map session) {
		this.session = session;		
	}
	
	public String getSortBy(){
		String sortBy = (String)session.get(SessionConstants.TAG_SORT_BY);
		if(sortBy != null){
			if(SessionConstants.SORT_ALPH.equalsIgnoreCase(sortBy)){
				return SessionConstants.SORT_ALPH; 
			}else if(SessionConstants.SORT_FREQ.equalsIgnoreCase(sortBy)){
				return SessionConstants.SORT_FREQ;
			}
		}
		return SessionConstants.SORT_ALPH;
	}
	
	public String getTagView(){
		String viewOpt = (String)session.get(SessionConstants.TAG_VIEW_OPT);
		if(viewOpt != null){
			if(SessionConstants.VIEW_TAG_CLOUD.equalsIgnoreCase(viewOpt)){
				return SessionConstants.VIEW_TAG_CLOUD; 
			}else if(SessionConstants.VIEW_TAG_LIST.equalsIgnoreCase(viewOpt)){
				return SessionConstants.VIEW_TAG_LIST;
			}
		}
		return SessionConstants.VIEW_TAG_CLOUD;
	}
	
	@SuppressWarnings("unchecked")
	public int getMinTagFreq() {
		Integer freq = (Integer) session.get(SessionConstants.MIN_TAG_FREQ);
		if (freq == null) {
			freq = 1;
			session.put(SessionConstants.MIN_TAG_FREQ, freq);
		}
		return freq;
	}
	
	@SuppressWarnings("unchecked")
	public boolean isHideTagGroups() {
		Boolean hideTagGrp = (Boolean)session.get(SessionConstants.HIDE_TAG_GROUPS);
		if(hideTagGrp == null){
			hideTagGrp = true;
			session.put(SessionConstants.HIDE_TAG_GROUPS, hideTagGrp);
		}
		return hideTagGrp;
	}
	
}
