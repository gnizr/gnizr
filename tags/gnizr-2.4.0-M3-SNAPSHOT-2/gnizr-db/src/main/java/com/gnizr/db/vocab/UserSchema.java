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

public interface UserSchema {
	public static final String TABLE_NAME = "user";
	public static final String ID = TABLE_NAME + ".id";
	public static final String USERNAME = TABLE_NAME + ".username";
	public static final String PASSWORD = TABLE_NAME + ".password";
	public static final String FULLNAME = TABLE_NAME + ".fullname";
	public static final String CREATED_ON = TABLE_NAME + ".created_on";
	public static final String ACCT_STATUS = TABLE_NAME + ".acct_status";
	public static final String EMAIL = TABLE_NAME + ".email";
	public static final String NUM_OF_BOOKMARKS = "total_number_bookmark";
	public static final String NUM_OF_TAGS = "total_number_tag";
	
	public static final String GNIZR_USER = "gnizr";
	
	public static final String ID_COL =  ".id";
	public static final String USERNAME_COL =  ".username";
	public static final String PASSWORD_COL =  ".password";
	public static final String FULLNAME_COL = ".fullname";
	public static final String CREATED_ON_COL =  ".created_on";
	public static final String ACCT_STATUS_COL =  ".acct_status";
	public static final String EMAIL_COL = ".email";
	
}
