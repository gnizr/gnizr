package com.gnizr.core.web.action.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSON;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;

import com.gnizr.core.tag.TagManager;
import com.gnizr.core.tag.TagPager;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.db.dao.Tag;

public class SuggestSearchTags extends AbstractAction{

	private static final long serialVersionUID = -3987905051542991693L;
	private static final Logger logger = Logger.getLogger(SuggestSearchTags.class);
	
	private TagManager tagManager;
	private TagPager tagPager;
	private String q;
	private List<String> skosRelatedTags = new ArrayList<String>();
	private List<String> skosNarrowerTags = new ArrayList<String>();
	private List<String> skosBroaderTags = new ArrayList<String>();
	
	private JSON jsonResult;
	
	public List<String> getSkosRelatedTags() {
		return skosRelatedTags;
	}

	public List<String> getSkosBroaderTags() {
		return skosBroaderTags;
	}

	public List<String> getSkosNarrowerTags() {
		return skosNarrowerTags;
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
				if(skosNarrowerTags != null && skosNarrowerTags.isEmpty() == false){
					map.put("narrower",skosNarrowerTags);
				}
				if(skosBroaderTags != null && skosBroaderTags.isEmpty() == false){
					map.put("broader",skosBroaderTags);
				}
				jsonResult = JSONSerializer.toJSON(map);
			}catch(Exception e){
				final String err = "can't transform SKOS tag objects to JSON: related=" + skosRelatedTags 
				+ ",narrower=" + skosNarrowerTags + ",broader="+skosBroaderTags;
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
				List<Tag> nrwTags = tagPager.getPopularNarrowerTagsByGnizrUser(t,1);
				if(nrwTags.isEmpty() == false){
					for(Tag nt: nrwTags){
						if(skosNarrowerTags.contains(nt.getLabel()) == false){
							skosNarrowerTags.add(nt.getLabel());
						}
					}
				}
				List<Tag> brdTags = tagPager.getPopularBroaderTagsByGnizrUser(t,1);
				if(brdTags.isEmpty() == false){
					for(Tag bt : brdTags){
						if(skosBroaderTags.contains(bt.getLabel()) == false){
							skosBroaderTags.add(bt.getLabel());
						}
					}
				}	
				List<Tag> relTags = tagPager.getPopularRelatedTagsByGnizrUser(t,1);
				if(relTags.isEmpty() == false){
					for(Tag rt : relTags){
						if(skosRelatedTags.contains(rt.getLabel()) == false &&
						   skosBroaderTags.contains(rt.getLabel()) == false &&
						   skosNarrowerTags.contains(rt.getLabel()) == false){
							skosRelatedTags.add(rt.getLabel());
						}
					}
				}
							
			}catch(Exception e){
				final String err = "error getting Popular tags for: tag=" + t;
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
