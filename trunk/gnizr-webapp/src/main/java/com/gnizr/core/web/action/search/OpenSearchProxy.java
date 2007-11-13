package com.gnizr.core.web.action.search;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.gnizr.core.web.action.AbstractAction;
import com.sun.syndication.feed.module.opensearch.OpenSearchModule;
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
		jsonResult = createJsonResult(feed);
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
	private JSONObject createJsonResult(SyndFeed feed){		
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
			entryMap.put(KEY_TITLE,e.getTitle());
			entryMap.put(KEY_LINK,e.getLink());
			entryMap.put(KEY_AUTHOR,e.getAuthor());
			entryMap.put(KEY_SUMMARY,e.getDescription().getValue());
			entries.add(entryMap);
		}
		map.put(KEY_ENTRIES, entries);
		return JSONObject.fromObject(map);				
	}
	

}
