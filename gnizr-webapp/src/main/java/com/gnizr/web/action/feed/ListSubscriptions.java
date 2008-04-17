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
package com.gnizr.web.action.feed;

import java.util.List;

import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.web.action.AbstractLoggedInUserAction;

public class ListSubscriptions extends AbstractLoggedInUserAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9044043394043967437L;

	// read-only data
	private List<FeedSubscription> subscriptions;
	
	// data access object
	private FeedSubscriptionManager feedSubscriptionManager;
	
	public FeedSubscriptionManager getFeedSubscriptionManager() {
		return feedSubscriptionManager;
	}

	public void setFeedSubscriptionManager(
			FeedSubscriptionManager feedSubscriptionManager) {
		this.feedSubscriptionManager = feedSubscriptionManager;
	}

	public List<FeedSubscription> getSubscriptions() {
		return subscriptions;
	}

	@Override
	protected String go() throws Exception {
		resolveUser();
		if(user != null){
			try{
				DaoResult<FeedSubscription> result = feedSubscriptionManager.listSubscription(user);
				this.subscriptions = result.getResult();
			}catch(Exception e){
				addActionError(e.toString());
				return ERROR;
			}			
		}
		return SUCCESS;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return false;
	}

}
