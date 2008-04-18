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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.LoggedInUserAware;
import com.gnizr.web.action.SessionConstants;
import com.gnizr.web.util.ServletUtilities;
import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * <p>An {@link com.opensymphony.xwork.interceptor.Interceptor} implementation for
 * passing on the information of a logged-user user to an Action. When this 
 * <code>Interceptor</code> is used on an Action that implements 
 * {@link com.gnizr.web.action.LoggedInUserAware}, if the session contains the information 
 * of a logged-in <code>User</code>, it will be passed onto the Action.</p>
 * <p>If the session doesn't contain any information of a logged-in user, this class
 * checks for a cookie named <code>rememberMe</code>. If this cookie exists, this 
 * <code>Interceptor</code> will attempt to use this information to construct a valid 
 * logged-in <code>User</code> and store it in the session. </p> 
 * 
 * @author Harry Chen
 * @since 2.2
 *
 */
public class LoggedInUserInterceptor implements Interceptor{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5325628528894720685L;
	private static final Logger logger = Logger.getLogger(LoggedInUserInterceptor.class);
	
	public void destroy() {
		// no code		
	}

	public void init() {
		// no code		
	}

	@SuppressWarnings("unchecked")
	public String intercept(ActionInvocation actionInvocation) throws Exception {
		Map session = actionInvocation.getInvocationContext().getSession();
		User user = (User)session.get(SessionConstants.LOGGED_IN_USER);
		if(user == null){
			user = getUserFromRememberMeCookie();		
			if(user != null){
				session.put(SessionConstants.LOGGED_IN_USER,user);
			}
		}
		Action action = (Action)actionInvocation.getAction();
		if(user != null && (action instanceof LoggedInUserAware)){
			((LoggedInUserAware)action).setLoggedInUser(user);			
		}				
		return actionInvocation.invoke();
	}
	
	private UserManager userManager;

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	private User getUserFromRememberMeCookie(){
		ActionContext ctx = ActionContext.getContext();
		HttpServletRequest req = (HttpServletRequest)ctx.get(ServletActionContext.HTTP_REQUEST);
		Cookie[] cookies = req.getCookies();
		Cookie passKey = ServletUtilities.getCookie(cookies,SessionConstants.REMEMBER_ME);
		if(passKey != null){
			String val = passKey.getValue();
			String[] s = val.split(":");
			if(s != null && s.length == 2){
				String username = s[0];
				String passwordMD5 = s[1];
				try {
					User matchingUser = userManager.getUser(username);
					if(matchingUser != null){
						if(username != null && username.equals(matchingUser.getUsername())){
							if(passwordMD5 != null && passwordMD5.equals(matchingUser.getPassword())){
								return matchingUser;
							}
						}
					}
				} catch (NoSuchUserException e) {
					logger.debug("user acct is deleted? username="+username,e);
				}
			}
		}
		return null;
	}

	
}
