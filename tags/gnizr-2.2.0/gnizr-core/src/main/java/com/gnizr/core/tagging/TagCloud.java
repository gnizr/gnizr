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
package com.gnizr.core.tagging;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchLinkTagException;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NoSuchUserTagException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.GnizrDao;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.user.UserDao;

public class TagCloud implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4688029740239496069L;

	private static final Logger logger = Logger.getLogger(TagCloud.class);

	private UserDao userDao;

	private TagDao tagDao;

	public static final int NUM_OF_FONT_SIZE = 6;

	public TagCloud(GnizrDao gnizrDao) {
		this.userDao = gnizrDao.getUserDao();
		this.tagDao = gnizrDao.getTagDao();		
	}

	/**
	 * @deprecated since 2.2
	 */
	public List<UserTag> getFreqCloudSortAlph(User user, int minFreq, String[] filterRegExPattern) throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException{
		return this.getFreqCloud(user, minFreq, filterRegExPattern, TagDao.SORT_ALPH);
	}
	/**
	 * @deprecated since 2.2
	 */
	public List<UserTag> getFreqCloudSortFreq(User user, int minFreq, String[] filterRegExPattern) throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException{
		return this.getFreqCloud(user, minFreq, filterRegExPattern, TagDao.SORT_FREQ);
	}
	
	/**
	 * @deprecated since 2.2
	 */
	private List<UserTag> getFreqCloud(User user, int minFreq,
			String[] filterRegExPattern, int sortBy) 
			throws NoSuchUserException, NoSuchTagException, NoSuchUserTagException, MissingIdException {
		logger.debug("getFreqCloud: user=" + user + ",minFreq=" + minFreq);
		List<UserTag> cloud = new ArrayList<UserTag>();
		GnizrDaoUtil.fillId(userDao, user);
		
		List<UserTag> ut = null;
		if(sortBy == TagDao.SORT_FREQ){
			ut = tagDao.findUserTag(user, minFreq, sortBy);
		}else{
			ut = tagDao.findUserTag(user, minFreq, TagDao.SORT_ALPH);
		}		
		logger.debug("found # of user tag: " + ut.size());
		
		if(filterRegExPattern  != null && filterRegExPattern.length > 0){
			for(UserTag  u : ut){
				if(filterOut(u.getTag().getLabel(), filterRegExPattern) == false){
					cloud.add(u);
				}
			}
		}else{
			cloud = ut;
		}		
		return cloud;
	}
	
	private boolean filterOut(String tag, String[] filterRegExPattern) {
		if (filterRegExPattern != null) {
			for (int i = 0; i < filterRegExPattern.length; i++) {
				if (tag.matches(filterRegExPattern[i]) == true) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @deprecated
	 * @param user
	 * @param minFreq
	 * @return
	 * @throws NoSuchUserException
	 * @throws NoSuchTagException
	 * @throws NoSuchUserTagException
	 * @throws MissingIdException
	 */
	public List<UserTag> getFreqCloud(User user, int minFreq)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, MissingIdException {
		return this.getFreqCloudSortAlph(user, minFreq, new String[0]);
	}

	public List<LinkTag> getCommonTagCloud(Link link, int maxCount)
			throws NoSuchLinkException, NoSuchTagException, MissingIdException,
			NoSuchLinkTagException, NoSuchUserException {
		logger.debug("getCommonTagCloud: link=" + link+",maxCount="+maxCount);
		DaoResult<LinkTag> result = tagDao.pageLinkTagSortByFreq(link,0,maxCount);
		return result.getResult();
	}

	private int[] getMaxMin(List<Tag> tags){
		int[] vMaxMin = new int[2];
		vMaxMin[0] = Integer.MIN_VALUE; // max: start with the smallest possible int
		vMaxMin[1] = Integer.MAX_VALUE; // min: start with the largest possible int
		for(Tag tag : tags){
			if(tag.getCount() > vMaxMin[0]){
				vMaxMin[0] = tag.getCount();
			}
			if(tag.getCount() < vMaxMin[1]){
				vMaxMin[1] = tag.getCount();		
			}
		}
		return vMaxMin;
	}
	
	public Map<Tag, Integer> getPopularTagCloudSortFreq() throws NoSuchTagException{
		return getPopularTagCloud(TagDao.SORT_FREQ);
	}
	
	public Map<Tag, Integer> getPopularTagCloudSortAlph() throws NoSuchTagException{
		return getPopularTagCloud(TagDao.SORT_ALPH);
	}
	
	private Map<Tag, Integer> getPopularTagCloud(int sortBy) throws NoSuchTagException {
		logger.debug("getPopularTagCloud");
		Map<Tag, Integer> cloud = new LinkedHashMap<Tag, Integer>();
		List<Tag> tags = tagDao.findTag(50, sortBy);		
		if (tags.isEmpty() == false) {
			int[] maxMin = getMaxMin(tags);
			int max = maxMin[0];
			int min = maxMin[1];
			double[] threshold = compThreshold(max, min, NUM_OF_FONT_SIZE);
			for (Iterator<Tag> it = tags.iterator(); it.hasNext();) {
				Tag aTag = it.next();
				boolean fontSetFlag = false;
				for (int i = 0; i < NUM_OF_FONT_SIZE; i++) {
					if (fontSetFlag == false) {						
						if (aTag.getCount() <= threshold[i]) {
							cloud.put(aTag, i + 1);
							fontSetFlag = true;
						}
					} else {
						break;
					}
				}
			}
		}
		return cloud;
	}

	private double[] compThreshold(int max, int min, int numOfFontSizes) {
		double delta = (double) (max - min) / numOfFontSizes;
		double[] th = new double[numOfFontSizes];
		for (int i = 0; i < numOfFontSizes; i++) {
			th[i] = min + (i + 1) * delta;
		}
		return th;
	}

}
