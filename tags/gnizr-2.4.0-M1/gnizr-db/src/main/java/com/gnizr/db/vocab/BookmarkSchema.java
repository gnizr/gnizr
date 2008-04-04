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

public interface BookmarkSchema {
	public static final String TABLE_NAME = "bookmark";
	public static final String ID = TABLE_NAME + ".id";
	public static final String USER_ID = TABLE_NAME + ".user_id";
	public static final String LINK_ID = TABLE_NAME + ".link_id";
	public static final String TITLE = TABLE_NAME + ".title";
	public static final String NOTES = TABLE_NAME + ".notes";
	public static final String CREATED_ON = TABLE_NAME + ".created_on";
	public static final String LAST_UPDATED = TABLE_NAME + ".last_updated";	
	public static final String IS_ARCHIVED = TABLE_NAME + ".is_archived";
	public static final String TAGS = "tags";
	public static final String FOLDERS = "folders";
}
