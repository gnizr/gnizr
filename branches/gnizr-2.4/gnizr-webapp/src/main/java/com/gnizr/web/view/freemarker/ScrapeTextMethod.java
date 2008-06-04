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
package com.gnizr.web.view.freemarker;

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.util.FormatUtil;

import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * A FreeMarker function for scraping plain text from HTML code. 
 *  
 * @author Harry Chen
 * @since 2.4.0
 *
 */
public class ScrapeTextMethod implements TemplateMethodModelEx{

	private static final Logger logger = Logger.getLogger(ScrapeTextMethod.class);
	
	/**
	 * Returns a plain text string that is scraped from the input HTML code. This 
	 * function takes a single argument, and which must be <code>StringModel</code>
	 * or <code>SimpleScalar</code>. 
	 */
	@SuppressWarnings("unchecked")
	public Object exec(List args) throws TemplateModelException {
		if(args.size() != 1){
			logger.error("Received wrong number of arguments.");
			throw new TemplateModelException("Wrong number of arguments. Expected args size of 1, but received " + args.size());
		}
		String htmlCode = null;
		if(args.get(0) instanceof StringModel){	
			htmlCode = ((StringModel)args.get(0)).getAsString();						
		}else if(args.get(0) instanceof SimpleScalar){
			htmlCode = ((SimpleScalar)args.get(0)).getAsString();
		}else{
			logger.error("Not a StringModel or SimpleScalar");
			throw new TemplateModelException("Argument is not an instance of StringModel or SimpleScalar");
		}
		if(htmlCode == null){
			logger.error("Can't scrape text from html code because the input is null");
			throw new NullPointerException("htmlCode is null");
		}
		return FormatUtil.extractTextFromHtml(htmlCode);
	}

}
