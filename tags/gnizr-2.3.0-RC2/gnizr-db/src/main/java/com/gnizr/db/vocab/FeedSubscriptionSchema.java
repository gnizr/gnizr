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

public interface FeedSubscriptionSchema {
	public static final String TABLE_NAME = "feed_subscription";
	public static final String BOOKMARK_ID = TABLE_NAME + ".bookmark_id";
	public static final String LAST_SYNC = TABLE_NAME + ".last_sync";
	public static final String AUTO_IMPORT = TABLE_NAME + ".auto_import";
	public static final String MATCH_TEXT = TABLE_NAME + ".match_text";
	public static final String ID = TABLE_NAME + ".id";
	public static final String IMPORT_FOLDERS = "importFolders";
	public static final String PUB_DATE = TABLE_NAME + ".pub_date";
}
