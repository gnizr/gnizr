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
package com.gnizr.web.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Provides convenient methods for searching cookie vaules, setting cookies and deleting cookies.  
 * @author Harry Chen
 * @since 2.2
 *
 */
public class ServletUtilities {

	/**
	 * Searches for the value of a given cookie and returns a default value if 
	 * the given cookie name doesn't exist in the input.  
	 * @param cookies The list of cookies objects to search
	 * @param cookieName the name of cookie to search for
	 * @param defaultValue if the value of <code>cookieName</code> can't be found,
	 * return <code>defaultValue</code>
	 * 
	 * @return the value of cookie <code>cookieName</code>. If the value can't be found
	 * by searching through <code>cookies</code>, <code>defaultValue</code> is returned.
	 */
	public static String getCookieValue(Cookie[] cookies, String cookieName,
			String defaultValue) {
		if(cookies == null){
			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equals(cookie.getName()))
				return (cookie.getValue());
		}
		return (defaultValue);
	}

	/**
	 * Returns the cookie object that matches a given cookie name. 
	 * @param cookies the list of cookie object to search
	 * @param cookieName the name of the cookie to find
	 * @return <code>null</code> if the cookie object can't be found. Otherwise,
	 * returns the <code>Cookie</code> object in <code>cookies</code> that matches
	 * the input <code>cookieName</code>
	 */
	public static Cookie getCookie(Cookie[] cookies, String cookieName) {
		if(cookies == null){
			return null;
		}
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equals(cookie.getName()))
				return (cookie);
		}
		return (null);
	}
	
	/**
	 * Deletes a cookie object from the input <code>HttpServletResponse</code>. 
	 * <b>Side effect</b>: If a cookie of name <code>cookieName</code> doesn't already
	 * exist, a new cookie object will be created and its maximum age will be set to <code>0</code>
	 * and path will be set to <code>/</code>. 
	 * @param res the source where the cookie will be deleted.
	 * @param cookieName the name of the cookie to delete. 
	 */
	public static void deleteCookie(HttpServletResponse res, String cookieName){
		Cookie cookie = new Cookie(cookieName,null);
		cookie.setMaxAge(0);
		cookie.setPath("/");
		res.addCookie(cookie);
	}
	
	/** Number of seconds in a year (i.e., 365 days).*/
	public static final int SECONDS_PER_YEAR = 60 * 60 * 24 * 365;
}
