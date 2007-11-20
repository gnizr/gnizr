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
package com.gnizr.core.web.action.link;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.managers.LinkManager;
import com.gnizr.core.tagging.TagCloud;
import com.gnizr.core.web.action.AbstractAction;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;

public class GetCommonTags extends AbstractAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6308565373936323861L;

	private static final Logger logger = Logger.getLogger(GetCommonTags.class);

	private TagCloud tagCloud;
	
	private LinkManager linkManager;

	private List<Bookmark> bookmarks;

	private List<LinkTag> commonTags;

	private String url;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<LinkTag> getCommonTags() {
		return commonTags;
	}

	public void setCommonTags(List<LinkTag> commonTags) {
		this.commonTags = commonTags;
	}

	@Override
	protected String go() throws Exception {
		if (bookmarks != null) {
			if (bookmarks.size() == 1) {
				commonTags = tagCloud.getCommonTagCloud(bookmarks.get(0).getLink(),20);
			} else {
				commonTags = unionCommonTagClouds(tagCloud, bookmarks);
			}
		}
		return SUCCESS;
	}

	private static List<LinkTag> unionCommonTagClouds(TagCloud tagCloud,
			List<Bookmark> bmarks) {
		List<LinkTag> unionCommonTags = new ArrayList<LinkTag>();
		List<Integer> seenTag = new ArrayList<Integer>();
		List<LinkTag> linkTags = null;
		for (Iterator<Bookmark> it = bmarks.iterator(); it.hasNext();) {
			Link aLink = it.next().getLink();
			try {
				LinkTag tmpLinkTag = null;
				// each link in the list is allowed to contribute
				// no more than 5 tags to the common tag cloud
				linkTags = tagCloud.getCommonTagCloud(aLink,5);
				for (Iterator<LinkTag> it2 = linkTags.iterator(); it2.hasNext();) {
					tmpLinkTag = it2.next();
					if (seenTag.contains(tmpLinkTag.getTag().getId()) == false) {
						unionCommonTags.add(tmpLinkTag);
						seenTag.add(tmpLinkTag.getTag().getId());						
					}
				}
			} catch (Exception e) {
				logger.debug(
						"failed to get common tag cloud for link=" + aLink, e);
			}
		}	
		return unionCommonTags;
	}

	public String doPageLinkTags() throws Exception{
		if(url != null){
			Link link = linkManager.getInfo(Link.computeUrlHash(url));
			if(link != null){
				List<LinkTag> tags = tagCloud.getCommonTagCloud(link, 6);
				if(tags != null){
					commonTags = tags;
				}
			}
		}
		if(commonTags == null){
			commonTags = new ArrayList<LinkTag>();
		}
		return SUCCESS;
	}
	
	public List<Bookmark> getBookmarks() {
		if (bookmarks == null) {
			bookmarks = new ArrayList<Bookmark>();
		}
		return bookmarks;
	}

	public void setBookmarks(List<Bookmark> bookmarks) {
		if (bookmarks != null) {
			getBookmarks().clear();
			getBookmarks().addAll(bookmarks);
		}
	}

	public void setLink(Link link) {
		if (link != null) {
			getBookmarks().clear();
			getBookmarks().add(new Bookmark(null,link));
		}
	}

	public TagCloud getTagCloud() {
		return tagCloud;
	}

	public void setTagCloud(TagCloud tagCloud) {
		this.tagCloud = tagCloud;
	}

	public LinkManager getLinkManager() {
		return linkManager;
	}

	public void setLinkManager(LinkManager linkManager) {
		this.linkManager = linkManager;
	}

}
