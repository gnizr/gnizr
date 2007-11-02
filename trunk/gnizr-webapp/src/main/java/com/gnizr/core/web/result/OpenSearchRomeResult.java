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
package com.gnizr.core.web.result;

import com.gnizr.core.web.action.OpenSearchResultAware;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;

/**
 * A {@link Result} to output OpenSearch Response in ATOM. 
 * 
 * @author Harry Chen
 *
 */
public class OpenSearchRomeResult extends RomeResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6548652321191974405L;
	
	/**
	 * Executes the action to create Open Search Response. The <code>SyndFeed</code> 
	 * object is already created with MIME-TYPE set to <code>text/xml</code>, feed type set to 
	 * <code>atom_1.0</code>. The class of <code>actionInvocation</code> 
	 * must implement {@link OpenSearchResultAware}.
	 */
	@Override
	public void execute(ActionInvocation actionInvocation) throws Exception {
		super.setMimeType("text/xml");
		super.setFeedType("atom_1.0");
		super.setFeedName("openSearchResult");
		super.execute(actionInvocation);
	}

}
