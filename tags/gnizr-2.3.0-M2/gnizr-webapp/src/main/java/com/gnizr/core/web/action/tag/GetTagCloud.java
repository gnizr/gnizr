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
package com.gnizr.core.web.action.tag;

import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gnizr.core.tag.TagCloud;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.db.dao.Tag;

public class GetTagCloud extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2498565322717510542L;
	private static final Logger logger = Logger.getLogger(GetTagCloud.class);
	private Map<Tag,Integer> tagCloudMap;
	private TagCloud tagCloud;
	private Tag[] sortedTags;
	private String sort;
	
	
	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("exec GetTagCloud.go()");
		if(sort != null && sort.equalsIgnoreCase("freq")){
			tagCloudMap = tagCloud.getPopularTagCloudSortFreq();
		}else{
			tagCloudMap = tagCloud.getPopularTagCloudSortAlph();
		}
		Set<Tag> tagSet = tagCloudMap.keySet();
		sortedTags = tagSet.toArray(new Tag[0]);
		return SUCCESS;
	}

	public TagCloud getTagCloud() {
		return tagCloud;
	}

	public void setTagCloud(TagCloud tagCloud) {
		this.tagCloud = tagCloud;
	}

	public Map<Tag, Integer> getTagCloudMap() {
		return tagCloudMap;
	}

	public void setTagCloudMap(Map<Tag, Integer> tagCloudMap) {
		this.tagCloudMap = tagCloudMap;
	}

	public Tag[] getSortedTags() {
		return sortedTags;
	}

}
