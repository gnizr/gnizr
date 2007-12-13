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
package com.gnizr.db.vocab;

/**
 * Defines the vocabulary for setting user account status. 
 * 
 * @author Harry Chen
 */
public interface AccountStatus {
	/**
	 * An user account has been created but user login is prohibited.
	 * Typically, this value is used when an account is created for
	 * the very first time, and it's waiting for the completion of 
	 * some user identity verificatoin process.  
	 */
	public static final int INACTIVE = 0;
	/**
	 * An user account has been created and user login is permitted.
	 * Typically, this value is used when the identity of a user has been
	 * verified. 
	 */
	public static final int ACTIVE = 1;
	/**
	 * An user account has been created, but it is disabled. User login 
	 * is prohibited. Typically, this value is used when the system wants 
	 * to preserve all information about an account (bookmarks, tags etc.) that
	 * is no longer associated with an active user. 
	 */
	public static final int DISABLED = 2;
}
