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
package com.gnizr.web.action;


import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.gnizr.web.util.GnizrConfiguration;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionSupport;

public abstract class AbstractAction extends ActionSupport implements GnizrConfigurationAware{

	private static final Logger logger = Logger.getLogger(AbstractAction.class.getName());
	
	public static final String MAINTENANCE = "maintenance";
	public static final String REDIRECT = "redirect";
	
	/**
	 * A flag that sets whether this action should do its best
	 * to suppress warning and errors.
	 */
	private boolean quietMode;
	
	/**
	 * A flag that sets whether session data should be 
	 * used as part of this action
	 */
	private boolean sessionMode;

	private GnizrConfiguration gnizrConfiguration;

	/**
	 * Creates a new <code>AbstractionAction</code> object and sets
	 * the default values of various action flags. Default values:
	 * <code>isQuietMode()</code> returns <code>false</code> and 
	 * <code>isSessionMode()</code> returns <code>true</code>.
	 * 
	 */
	public AbstractAction(){
		quietMode = false;
		sessionMode = true;
	}
	
	/**
	 * @return the sessionMode
	 */
	public boolean isSessionMode() {
		return sessionMode;
	}
	
	/**
	 * @param sessionMode the sessionMode to set
	 */
	public void setSessionMode(boolean sessionMode) {
		this.sessionMode = sessionMode;
	}

	protected abstract String go() throws Exception;
	
	public String execute() throws Exception{		
		logger.debug("executing action");
		return go();
	}
	
	protected HttpServletResponse getServletResponse(){
		ActionContext ctx = ActionContext.getContext();
		return (HttpServletResponse)ctx.get(ServletActionContext.HTTP_RESPONSE);		
	}
	
	protected HttpServletRequest getServletRequest(){
		ActionContext ctx = ActionContext.getContext();
		return (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
	}
	
	public static final String SYSTEM_ERROR_MSG = "system error! please contact administrator.";
	
	public static final String NO_SAVED_BOOKMARK = "Found 0 bookmarks.";

	/**
	 * Returns the quiet mode flag of this action. Returns 
	 * <code>true</code> if this action is set to suppress
	 * warnings and errors. Otherwise, returns <code>false</code>
	 * 
	 * @return the current value of the quiet mode flag
	 */
	public boolean isQuietMode() {
		return quietMode;
	}

	/**
	 * Sets the quiet mode flag of this action. If <code>quietModel</code>
	 * is set to <code>true</code>, then the action will try its best
	 * to suppress warnings and errors. Otherwise, warnings and errors will 
	 * be reported (e.g., exception thrown) as soon as they are encountered.
	 *  
	 * @param quietMode the flag value to be assigned
	 */
	public void setQuietMode(boolean quietMode) {
		this.quietMode = quietMode;
	}
		
	public Date getNow(){
		return GregorianCalendar.getInstance().getTime();
	}

	public void setGnizrConfiguration(GnizrConfiguration config) {
		this.gnizrConfiguration = config;		
	}
	
	
	public GnizrConfiguration getGnizrConfiguration() {
		return gnizrConfiguration;
	}

	
	@SuppressWarnings("unchecked")
	public String getRedirectToPage() {
		Map session = ActionContext.getContext().getSession();		
		return (String)session.get(SessionConstants.REDIRECT_TO_PAGE);
	}
	
	@SuppressWarnings("unchecked")
	public void setRedirectToPage(String path){
		Map session = ActionContext.getContext().getSession();		
		session.put(SessionConstants.REDIRECT_TO_PAGE,path);
	}
	
	public void setCallback(String callback){
		// no code here
	}
	
}
