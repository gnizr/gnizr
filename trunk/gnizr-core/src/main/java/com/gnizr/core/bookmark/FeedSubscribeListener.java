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
package com.gnizr.core.bookmark;

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.User;

public class FeedSubscribeListener implements BookmarkListener{

	private static final long serialVersionUID = 5808065554881663484L;
	private FeedSubscriptionManager feedManager;
	
	private static final Logger logger = Logger.getLogger(FeedSubscribeListener.class);
	
	public FeedSubscribeListener(FeedSubscriptionManager feedManager) {	
		this.feedManager = feedManager;	
	}

	private void doSubscribe(Bookmark bookmark)  {
		String feedUrl = bookmark.getLink().getUrl();
		String feedTitle = bookmark.getTitle();		
		User feedOwner = bookmark.getUser();
		try{
			FeedSubscription feedSub = feedManager.createSubscription(feedOwner, feedUrl,feedTitle);
			if(feedSub != null){
				logger.debug("added feed subscription: " + feedSub);
			}else{
				logger.error("unable to add feed subscription: " + feedSub);
			}
		}catch(Exception e){
			logger.error(e);
		}
	}
	
	
	
	private boolean hasSubscribeTag(List<MachineTag> machineTags){
		for(MachineTag mt : machineTags){
			String tag = mt.toString();
			if(tag.equals("gn:subscribe=this") || tag.equals("subscribe:this")){
				return true;
			}
		}
		return false;
	}

	public void notifyAdded(BookmarkManager manager, Bookmark bookmark) throws Exception {
		List<MachineTag> machineTags = bookmark.getMachineTagList();
		boolean hasSubTag = hasSubscribeTag(machineTags);
		if(hasSubTag == true){
			doSubscribe(bookmark);
		}		
	}

	public void notifyDeleted(BookmarkManager manager,Bookmark bookmark) throws Exception {
		// no code
		// taken care by the use of DB's foreign key construct		
	}

	public void notifyUpdated(BookmarkManager manager,Bookmark oldBookmark, Bookmark newBookmark) throws Exception {		
		List<MachineTag> newTags = newBookmark.getMachineTagList();		
		boolean foundInNewTags = hasSubscribeTag(newTags);
		if(foundInNewTags == true){
			doSubscribe(newBookmark);
		}
	}

}
