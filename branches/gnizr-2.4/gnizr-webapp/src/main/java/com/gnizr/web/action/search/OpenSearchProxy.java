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
package com.gnizr.web.action.search;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.gnizr.core.util.FormatUtil;
import com.gnizr.web.action.AbstractAction;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class OpenSearchProxy extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2427461792008794188L;
	private static final Logger logger = Logger.getLogger(OpenSearchProxy.class);
	private static final String KEY_TITLE = "title";
	private static final String KEY_LINK = "link";
	private static final String KEY_ITEMS_PER_PAGE = "itemsPerPage";
	private static final String KEY_TOTAL_RESULTS = "totalResults";
	private static final String KEY_START_INDEX = "startIndex";
	private static final String KEY_ENTRIES = "entries";
	private static final String KEY_AUTHOR = "author";
	private static final String KEY_ID = "id";
	private static final String KEY_SUMMARY = "summary";
	
	private String searchUrl;
	
	private JSONObject jsonResult;
	
	public String getSearchUrl() {
		return searchUrl;
	}

	public void setSearchUrl(String searchUrl) {
		this.searchUrl = searchUrl;
	}

	public JSONObject getJsonResult() {
		return jsonResult;
	}

	@Override
	protected String go() throws Exception {
		SyndFeed feed = readAtomData(getSearchUrl()); 
		if(feed != null){
			jsonResult = createJsonResult(feed);
		}else{
			jsonResult = null;
		}
		return SUCCESS;
	}
	
	private SyndFeed readAtomData(String url){
		SyndFeed feed = null;		
		SyndFeedInput input = new SyndFeedInput();
		try {
			feed = input.build(new XmlReader(new URL(url)));
		} catch(Exception e){
			logger.debug("error reading feed: " + url,e);
		}
		return feed;
	}
	
	@SuppressWarnings("unchecked")
	public static JSONObject createJsonResult(SyndFeed feed){		
		Map<String,Object> map = new HashMap<String, Object>();
		map.put(KEY_TITLE, feed.getTitle());
		map.put(KEY_LINK,feed.getLink());
		map.put(KEY_ID,feed.getUri());
		OpenSearchModule osMod = (OpenSearchModule) feed.getModule(OpenSearchModule.URI);
		if(osMod != null){
			map.put(KEY_ITEMS_PER_PAGE,osMod.getItemsPerPage());
			map.put(KEY_TOTAL_RESULTS,osMod.getTotalResults());
			map.put(KEY_START_INDEX,osMod.getStartIndex());
		}
		List<Map<String,Object>> entries = new ArrayList<Map<String,Object>>();
		List<SyndEntry> syndEntries = feed.getEntries();
		for(SyndEntry e : syndEntries){
			Map<String, Object> entryMap = new HashMap<String, Object>();
			entryMap.put(KEY_ID,e.getUri());
			entryMap.put(KEY_TITLE,getTidyText(e.getTitle()));
			entryMap.put(KEY_LINK,e.getLink());
			entryMap.put(KEY_AUTHOR,e.getAuthor());
			SyndContent content = e.getDescription();
			if(content != null && content.getValue() != null){
				String tt = getTidyText(e.getDescription().getValue());
				tt = FormatUtil.highlightStarEnclosedText(tt,"<span class=\"matched_text\">","</span>");
				entryMap.put(KEY_SUMMARY,tt);
			}else{
				entryMap.put(KEY_SUMMARY,"");
			}
			entries.add(entryMap);
		}
		map.put(KEY_ENTRIES, entries);
		return JSONObject.fromObject(map);				
	}
	
	private static String getTidyText(String text){
		if(text != null){
			String s = FormatUtil.extractTextFromHtml(text);
			s = FormatUtil.removeLongWord(s,20);
			return s;
		}
		return null;
	}
	
}
