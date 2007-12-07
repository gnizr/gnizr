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

import com.gnizr.core.web.util.GnizrConfiguration;

public interface SessionConstants {
	/**
	 * Defines the number of items to be displayed on a singple page
	 */
	public static final String PAGE_COUNT = "pageCount";
	
	/**
	 * Defines the total of pages
	 */
	public static final String PAGE_TOTAL_COUNT = "pageTotalCount";
	
	public static final String PREVIOUS_PAGE_NUM = "pagePrevious";
	
	public static final String NEXT_PAGE_NUM = "pageNext";
	
	public static final String LOGGED_IN_USER = "loggedInUser";
	
	public static final String MIN_TAG_FREQ = "minTagFreq";
	
	/** 
	 * @deprecated since 2.2
	 */
	public static final String HIGHLIGHT_USER_TAG = "emUserTag";
	
	public static final String OP_INPUT = "input";
	public static final String OP_DELETE = "delete";
	public static final String OP_UPDATE = "update";
	public static final String OP_SAVE = "save";

	public static final String HIGHLIGHT_USER_TAG_NARROWER = "emNarrowerUserTag";
	public static final String HIGHLIGHT_USER_TAG_BROADER = "emBroaderUserTag";
	
	public static final String REMEMBER_ME = "rememberMe";

	public static final String SEARCH_STRING_HASH = "searchStringHash";
	public static final String SEARCH_LINK_RESULT = "searchLinkResult";
	public static final String SEARCH_STRING = "searchString";
	public static final String SEARCH_TYPE = "searchType";
	
	public static final String TAG_SORT_BY = "userTagSortBy";
	public static final String SORT_ALPH = "alpha";
	public static final String SORT_FREQ = "freq";

	
	public static final String TAG_VIEW_OPT = "userTagViewOpt";
	public static final String VIEW_TAG_CLOUD = "cloud";
	public static final String VIEW_TAG_LIST = "list";
	
	public static final String WEB_APP_URL = GnizrConfiguration.WEB_APPLICATION_URL;
	public static final String GMAPS_KEY = GnizrConfiguration.GOOGLE_MAPS_KEY;
	public static final String SNAP_SHOTS_KEY = GnizrConfiguration.SNAPSHOTS_KEY;
	public static final String REGISTRATION_POLICY = GnizrConfiguration.REGISTRATION_POLICY;

	public static final String REDIRECT_TO_PAGE = "redirectToPage";
	
	public static final String HIDE_TAG_GROUPS = "hideTagGroups";
}
