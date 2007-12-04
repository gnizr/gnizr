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
package com.gnizr.core.foruser;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.InvalidTimeRangeValueException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.foruser.ForUserDao;

public class ForUserPager implements Serializable {

	private static final Logger logger = Logger.getLogger(ForUserPager.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 6432931495661221338L;

	private ForUserDao forUserDao;
	
	public ForUserPager(GnizrDao gnizrDao) {
		this.forUserDao = gnizrDao.getForUserDao();		
	}
	
	public int getMaxPageNumber(User user, int perPageCount)
			throws NoSuchUserException {
		logger.debug("getMaxPageNumber: gUser=" + user + ",perPageCount="
				+ perPageCount);
		int numOfBmarks = forUserDao.getForUserCount(user);
		return calcMaxPageNum(numOfBmarks, perPageCount);
	}

	public int getMaxPageNumber(User user, int timeRange, int perPageCount) throws InvalidTimeRangeValueException {
		logger.debug("getMaxPageNumber: user=" + user+ ",timeRange="+timeRange+",perPageCount=" + perPageCount);
		Date[] dates = GnizrDaoUtil.getTimeRangeDate(timeRange);
		if(dates != null){
			int numOfForUser = forUserDao.getForUserCount(user, dates[0],dates[1]);
			return calcMaxPageNum(numOfForUser, perPageCount);
		}
		throw new InvalidTimeRangeValueException();
	}

	public List<ForUser> pageForUser(User user, int offset, int count) {
		logger.debug("pageGnizrBookmark: user=" + user + ",offset=" + offset
				+ ",count=" + count);
		List<ForUser> linksForUser = forUserDao
				.pageForUser(user, offset, count);
		logger.debug("return # of GnizrForUser: " + linksForUser.size());		
		return linksForUser;
	}

	public List<ForUser> pageForUser(User user, int timeRange, int offset,int count) throws InvalidTimeRangeValueException {
		logger.debug("pageForUserCreatedOnToday: user=" + user + ",timeRange="+timeRange+",offset="+offset+",count="+count);
		Date[] dates = GnizrDaoUtil.getTimeRangeDate(timeRange);
		if(dates != null){
			List<ForUser> linksForUser = 
				forUserDao.pageForUser(user, dates[0], dates[1], offset, count);
			logger.debug("return # of GnizrForUser: " + linksForUser.size());	
			return linksForUser;
		}
		throw new InvalidTimeRangeValueException();
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
