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

public interface TagAssertionSchema {
	public static final String TABLE = "tag_assertion";
	public static final String ID = TABLE + ".id";
	public static final String SUBJECT_ID = TABLE + ".subject_id";
	public static final String PROPERTY_ID = TABLE + ".prpt_id";
	public static final String OBJECT_ID = TABLE + ".object_id";
	public static final String USER_ID = TABLE + ".user_id";
	
}
