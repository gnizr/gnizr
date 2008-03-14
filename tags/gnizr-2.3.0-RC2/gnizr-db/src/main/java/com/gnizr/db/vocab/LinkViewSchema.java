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

public interface LinkViewSchema {

	public static final String TABLE_NAME = "link_view";
	public static final String ID = LinkSchema.ID;
	public static final String MIME_TYPE_ID = LinkSchema.MIME_TYPE_ID;
	public static final String URL = LinkSchema.URL;
	public static final String URL_HASH = LinkSchema.URL_HASH;
	public static final String USER_ID = BookmarkSchema.USER_ID;
	public static final String TITLE = BookmarkSchema.TITLE;
	public static final String NOTES = BookmarkSchema.NOTES;
	public static final String CREATED_ON = BookmarkSchema.CREATED_ON;
	public static final String TAGS =  BookmarkSchema.TAGS;
	public static final String BOOKMARK_COUNT = TABLE_NAME+".bookmark_count";
}
