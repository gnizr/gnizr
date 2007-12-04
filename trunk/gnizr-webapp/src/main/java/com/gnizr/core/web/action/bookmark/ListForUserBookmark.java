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
package com.gnizr.core.web.action.bookmark;

import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.InvalidTimeRangeValueException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.foruser.ForUserManager;
import com.gnizr.core.foruser.ForUserPager;
import com.gnizr.core.vocab.TimeRange;
import com.gnizr.core.web.action.AbstractPagingAction;
import com.gnizr.core.web.action.LoggedInUserAware;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.User;

public class ListForUserBookmark extends AbstractPagingAction implements LoggedInUserAware{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8564795588598466593L;
	private static final Logger logger = Logger.getLogger(ListForUserBookmark.class);
	
	private User loggedInUser;
	private ForUserPager forUserPager;
	private List<ForUser> gnizrForUsers;
	private String timeRange;
	private ForUserManager forUserManager;
	private List<User> senders;
	private String sender;
	private int totalCount;
	
	public int getTotalCount() {
		return totalCount;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public List<User> getSenders() {
		return senders;
	}

	public ForUserManager getForUserManager() {
		return forUserManager;
	}

	public void setForUserManager(ForUserManager forUserManager) {
		this.forUserManager = forUserManager;
	}

	public String getTimeRange() {
		return timeRange;
	}

	public void setTimeRange(String timeRange) {
		this.timeRange = timeRange;
	}
	
	private static int getTimeRangeCode(String timeRange){
		if(timeRange == null){
			return -1;
		}else if("today".equalsIgnoreCase(timeRange)){
			return TimeRange.TODAY;
		}else if("yesterday".equalsIgnoreCase(timeRange)){
			return TimeRange.YESTERDAY;
		}else if("last7days".equalsIgnoreCase(timeRange)){
			return TimeRange.LAST_7_DAYS;
		}else if("lastmonth".equalsIgnoreCase(timeRange)){
			return TimeRange.LAST_MONTH;
		}else if("thismonth".equalsIgnoreCase(timeRange)){
			return TimeRange.THIS_MONTH;
		}
		return -1;
	}

	protected String go() throws Exception{
		logger.debug("listForUserBookmark.go()");
		initPagingAction();

		int max = 1;
		int ppc = getPerPageCount();
		int tmRngCode= getTimeRangeCode(timeRange);
		if(tmRngCode == -1){
			setTimeRange("all");
		}
		if(loggedInUser != null){
			try{
				//max = forUserPager.getMaxPageNumber(loggedInUser,ppc);
				max = getMaxPageNumber(loggedInUser,ppc,tmRngCode);
				setTotalNumOfPages(max);
				int offset = computeOffset(page);
				//gnizrForUsers = forUserPager.pageBookmarksForUser(loggedInUser,offset,ppc);
				gnizrForUsers = pageForUser(loggedInUser,offset,ppc,tmRngCode);
				setPreviousPageNum(getPage(),max);
				setNextPageNum(getPage(),max);
			}catch(Exception e){
				logger.error(e);
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	private int getMaxPageNumber(User u, int ppc,int tmRngCode) 
	  throws InvalidTimeRangeValueException, NoSuchUserException{
		int max = 1;
		if(tmRngCode != -1){			
			max = forUserPager.getMaxPageNumber(u,tmRngCode,ppc);
		}else{
			max = forUserPager.getMaxPageNumber(u,ppc);
		}
		return max;
	}
	
	
	private List<ForUser> pageForUser(User u, int offset,int ppc, int tmRngCode) throws InvalidTimeRangeValueException{
		if(tmRngCode != -1){
			return forUserPager.pageForUser(u,tmRngCode, offset, ppc); 
		}else{
			return forUserPager.pageForUser(u, offset, ppc);
		}
	}
	
	public User getUser(){
		return this.loggedInUser;
	}
	
	public void setLoggedInUser(User user) {
		this.loggedInUser = user;		
	}

	public User getLoggedInUser() {
		return loggedInUser;
	}

	public ForUserPager getForUserPager() {
		return forUserPager;
	}

	public void setForUserPager(ForUserPager forUserPager) {
		this.forUserPager = forUserPager;
	}

	public List<ForUser> getGnizrForUsers() {
		return gnizrForUsers;
	}

	public void setGnizrForUsers(List<ForUser> forUserList) {
		this.gnizrForUsers = forUserList;
	}

	public String doListSenderUsers() throws Exception{
		logger.debug("doListSenderUsers: loggedInUser=" + getLoggedInUser());
		if(loggedInUser == null){
			return INPUT;
		}
		senders = forUserManager.listForUserSenders(getLoggedInUser());
		return SUCCESS;
	}
	
	
	public String doGetTotalCount() throws Exception{
		logger.debug("doGetTotalCount");
		totalCount = forUserManager.getForUserCount(getLoggedInUser());
		return SUCCESS;
	}
	
	
	public String doListFromSender() throws Exception{
		logger.debug("doListFromSender.go()");
		initPagingAction();

		int max = 1;
		int ppc = getPerPageCount();
		if(loggedInUser != null){
			try{		
				int offset = computeOffset(getPage());
				DaoResult<ForUser> result = forUserManager.pageForUser(loggedInUser,new User(sender),offset,ppc);
				gnizrForUsers = result.getResult();
				max = calcMaxPageNum(result.getSize(), ppc);
				setTotalNumOfPages(max);
				setPreviousPageNum(getPage(),max);
				setNextPageNum(getPage(),max);
			}catch(Exception e){
				logger.error(e);
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	private int calcMaxPageNum(int numOfItems, int perPageCount) {
		int max = 1;
		if (numOfItems > 0 && perPageCount > 0) {
			int tnp = numOfItems / perPageCount;
			if ((numOfItems % perPageCount) > 0) {
				tnp++;
			}
			if (tnp > 1) {
				max = tnp;
			}
		}
		return max;
	}
	
}
