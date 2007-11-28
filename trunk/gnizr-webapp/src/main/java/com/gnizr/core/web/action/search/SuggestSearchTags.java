package com.gnizr.core.web.action.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

import com.gnizr.core.managers.TagManager;
import com.gnizr.core.pagers.TagPager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.db.dao.Tag;

public class SuggestSearchTags extends AbstractAction{

	private static final long serialVersionUID = -3987905051542991693L;
	private static final Logger logger = Logger.getLogger(SuggestSearchTags.class);
	
	private TagManager tagManager;
	private TagPager tagPager;
	private String q;
	private List<String> skosRelatedTags = new ArrayList<String>();
	private JSON jsonResult;
	
	public List<String> getSkosRelatedTags() {
		return skosRelatedTags;
	}

	public TagManager getTagManager() {
		return tagManager;
	}

	public void setTagManager(TagManager tagManager) {
		this.tagManager = tagManager;
	}

	public JSON getJsonResult() {
		if(jsonResult == null){
			try{
				Map<String,List<String>> map = new HashMap<String, List<String>>();
				if(skosRelatedTags != null && skosRelatedTags.isEmpty() == false){
					map.put("related",skosRelatedTags);
				}
				jsonResult = JSONSerializer.toJSON(map);
			}catch(Exception e){
				final String err = "can't transform skosRelatedTags to JSON: " + skosRelatedTags;
				logger.error(err,e);
			}
		}
		return jsonResult;
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public TagPager getTagPager() {
		return tagPager;
	}

	public void setTagPager(TagPager tagPager) {
		this.tagPager = tagPager;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("SuggestSearchTags go(): q=" + getQ());
		List<String> tags = extractTags(getQ());
		for(String t : tags){
			try{	
				List<Tag> relTags = tagPager.getPopularRelatedTagsByGnizrUser(t,1);
				if(relTags.isEmpty() == false){
					for(Tag rt : relTags){
						if(skosRelatedTags.contains(rt.getLabel()) == false){
							skosRelatedTags.add(rt.getLabel());
						}
					}
				}
			}catch(Exception e){
				final String err = "error getting Popular Related Tags: tag=" + t;
				logger.error(err,e);
			}
		}
		return SUCCESS;
	}
	
	private List<String> extractTags(String searchTerm){
		List<String> tags = new ArrayList<String>();
		if(searchTerm != null){
			String[] st = searchTerm.split("\\s+");
			for(String s : st){
				Tag t = tagManager.getTag(s);
				if(t != null){
					tags.add(s);
				}
			}
		}
		return tags;
	}

	
	
}
