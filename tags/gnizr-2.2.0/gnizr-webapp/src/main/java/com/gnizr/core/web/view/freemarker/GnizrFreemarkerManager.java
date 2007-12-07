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
package com.gnizr.core.web.view.freemarker;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.opensymphony.webwork.views.freemarker.FreemarkerManager;
import com.opensymphony.webwork.views.freemarker.ScopesHashModel;
import com.opensymphony.xwork.util.OgnlValueStack;

public class GnizrFreemarkerManager extends FreemarkerManager{
	private static final String HIGHLIGHT_TEXT_KEY = "highlightText";
	private static final String COMPUTE_TAG_CLOUD = "computeTagCloud";

	@Override
	public void populateContext(ScopesHashModel model, OgnlValueStack stack, Object object, HttpServletRequest request, HttpServletResponse response) {
		super.populateContext(model, stack, object, request, response);
		model.put(HIGHLIGHT_TEXT_KEY, new HighlightTextMethod());
		model.put(COMPUTE_TAG_CLOUD, new ComputeTagCloudMethod());
	}
	
	
	
}
