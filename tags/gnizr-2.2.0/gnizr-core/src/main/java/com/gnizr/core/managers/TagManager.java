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
package com.gnizr.core.managers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchTagAssertionException;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchTagPropertyException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NoSuchUserTagException;
import com.gnizr.core.exceptions.ParseTagException;
import com.gnizr.core.folder.NoSuchFolderException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.TagUtil;
import com.gnizr.db.GnizrDao;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.TagProperty;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.folder.FolderDao;
import com.gnizr.db.dao.tag.TagAssertionDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.tag.TagPropertyDao;
import com.gnizr.db.dao.user.UserDao;
import com.gnizr.db.vocab.UserSchema;

public class TagManager implements Serializable {

	private static final long serialVersionUID = 9096812556095636955L;

	private static final Logger logger = Logger.getLogger(TagManager.class);

	private TagPropertyDao tagPrptDao;

	private UserDao userDao;

	private TagAssertionDao tagAsrtDao;

	private TagDao tagDao;
	
	private FolderDao folderDao;

	private TagProperty rdfTypeProperty;

	private TagProperty skosRelatedProperty;

	private TagProperty skosBroaderProperty;

	private TagProperty skosNarrowerProperty;

	private UserTag gnizrPlaceTag;

	public static final String PLACE_TAG = "place";

	public static final String EVENT_TAG = "event";

	public static final String PERSON_TAG = "person";

	public TagProperty getRdfTypeProperty() {
		return rdfTypeProperty;
	}

	public TagManager(GnizrDao gnizrDao) {
		if (gnizrDao == null) {
			logger.fatal("tagManager(GnizrDao): gnizr object that is NULL");
		}
		this.tagPrptDao = gnizrDao.getTagPropertyDao();
		this.userDao = gnizrDao.getUserDao();
		this.tagAsrtDao = gnizrDao.getTagAssertionDao();
		this.tagDao = gnizrDao.getTagDao();
		this.folderDao = gnizrDao.getFolderDao();
		initDataValues();
	}

	private void initDataValues() {
		try {
			rdfTypeProperty = tagPrptDao.getTagProperty("rdf", "type");
			skosBroaderProperty = tagPrptDao.getTagProperty("skos", "broader");
			skosNarrowerProperty = tagPrptDao
					.getTagProperty("skos", "narrower");
			skosRelatedProperty = tagPrptDao.getTagProperty("skos", "related");

			User gnizrUser = new User(UserSchema.GNIZR_USER);
			Tag placeTag = new Tag(PLACE_TAG);
			GnizrDaoUtil.fillId(userDao, gnizrUser);
			GnizrDaoUtil.fillId(tagDao, placeTag);
			gnizrPlaceTag = GnizrDaoUtil
					.getUserTag(tagDao, gnizrUser, placeTag);
		} catch (Exception e) {
			logger.fatal("unable to initialize system data values.", e);
		}
	}

	public int addTag(Tag tag) {
		logger.debug("addTag: tag=" + tag);
		return tagDao.createTag(tag);
	}

	public Tag getTag(String tag) {
		logger.debug("getTag: " + tag);
		List<Tag> tags = tagDao.findTag(tag);
		if (tags != null && tags.size() > 0) {
			return tags.get(0);
		}
		return null;
	}

	public boolean addTagProperty(TagProperty prpt) {
		logger.debug("addTagProperty: prpt=" + prpt);
		GnizrDaoUtil.checkNull(prpt);
		int id = tagPrptDao.createTagProperty(prpt);
		return (id > 0);
	}

	public Boolean deleteTagProperty(TagProperty prpt)
			throws MissingIdException, NoSuchTagPropertyException {
		logger.debug("deleteTagProperty: tagPrpt=" + prpt);
		GnizrDaoUtil.checkNull(prpt);
		TagProperty obj = new TagProperty(prpt);
		if (GnizrDaoUtil.hasMissingId(obj)) {
			GnizrDaoUtil.fillId(tagPrptDao, obj);
			if (obj.getId() <= 0) {
				throw new MissingIdException("tagProperty missing id");
			}
		}
		return tagPrptDao.deleteTagProperty(obj.getId());
	}

	public List<TagProperty> listTagProperty() {
		logger.debug("listTagProperty");
		return tagPrptDao.findTagProperty();
	}

	private boolean addTagAssertions(User user, UserTag subjectTag, TagProperty prpt, List<String> tags2add){
		boolean isOkay = false;
		List<TagAssertion> tagAssertion = new ArrayList<TagAssertion>();
		try {
			UserTag[] objectTag = createUserTag(user, tags2add);
			for(int i = 0; i < objectTag.length; i++){
				TagAssertion ta = new TagAssertion();
				ta.setUser(user);
				ta.setSubject(subjectTag);
				ta.setProperty(prpt);
				ta.setObject(objectTag[i]);
				tagAssertion.add(ta);
			}
			int size = tagAssertion.size();
			isOkay = tagAsrtDao.createTagAssertion(tagAssertion.toArray(new TagAssertion[size]));
		} catch (Exception e) {
			logger.error(e);			
		}
		return isOkay;
	}
	
	public boolean addRDFTypeTags(User user, UserTag subjectTag, List<String> classTags){
		return addTagAssertions(user, subjectTag, rdfTypeProperty, classTags);
	}
	
	public boolean addRDFInstanceTags(User user, UserTag clsTag, List<String> instTags){
		boolean isOkay = false;
		List<TagAssertion> tagAssertion = new ArrayList<TagAssertion>();
		try {
			UserTag[] subjTag = createUserTag(user, instTags);
			for(int i = 0; i < subjTag.length; i++){
				TagAssertion ta = new TagAssertion();
				ta.setUser(user);
				ta.setSubject(subjTag[i]);
				ta.setProperty(rdfTypeProperty);
				ta.setObject(clsTag);
				tagAssertion.add(ta);
			}
			int size = tagAssertion.size();
			isOkay = tagAsrtDao.createTagAssertion(tagAssertion.toArray(new TagAssertion[size]));
		} catch (Exception e) {
			logger.error(e);			
		}
		return isOkay;
	}
	
	public boolean addSKOSRelatedTags(User user, UserTag subjectTag,
			List<String> relatedTags) {
		return addTagAssertions(user, subjectTag, skosRelatedProperty, relatedTags);
	}

	public boolean addSKOSBroaderTags(User user, UserTag subjectTag,
			List<String> broaderTags) {
		return addTagAssertions(user, subjectTag, skosBroaderProperty, broaderTags);
	}
	
	public boolean addSKOSNarrowerTags(User user, UserTag subjectTag,
			List<String> narrowerTags) {
		return addTagAssertions(user, subjectTag, skosNarrowerProperty, narrowerTags);
	}
	
	/**
	 * Creates an array of <code>UserTag</code> from a list of <code>tags</code>.
	 * If an object of <code>tags</code> is a machine tag of <code>gn:tag</code> ,
	 * then the <code>UserTag</code> is created based on this machine tag data. Otherwise,
	 * the <code>UserTag</code> is created using <code>user</code> as the user of the
	 * the object. 
	 * 
	 * @param user the user to be associated with the <code>UserTag</code>
	 * @param tags a list of tags
	 * 
	 * @return an array of <code>UserTag</code> with instantiated <code>UserTag.getId</code>
	 * @throws NoSuchUserException 
	 */
	private UserTag[] createUserTag(User user, List<String> tags) throws NoSuchUserException{
		List<UserTag> objectTag = new ArrayList<UserTag>();
		for(String ctag : tags){
			User userX = null;
			String tagX = null;
			UserTag ut = null;
			if(TagUtil.isPrefixedUserTag(ctag)){
				UserTag tmpObj = TagUtil.parsePrefixedUserTag(ctag);
				if((tmpObj.getUser().getId() <= 0) && tmpObj.getUser().getUsername() != null){
					userX = GnizrDaoUtil.getUser(userDao,tmpObj.getUser().getUsername());				
					if(userX == null){
						throw new NoSuchUserException(tmpObj.getUser().getUsername());
					}
				}else{
					userX = user;
				}
				tagX = tmpObj.getTag().getLabel();
			}else{
				userX = user;
				tagX = ctag;
			}
			ut = createUserTag(userX, tagX);			
			objectTag.add(ut);
		}
		return objectTag.toArray(new UserTag[objectTag.size()]);
	}
	
	public boolean deleteRDFTypeTags(User user, UserTag subjectTag,
			List<String> classTags) throws NoSuchUserException {
		UserTag[] objectTags = createUserTag(user,classTags);
		return tagAsrtDao.deleteRDFTypeAssertion(user, subjectTag,objectTags);
	}
	
	public boolean deleteRDFInstanceTags(User user, UserTag classTag, List<String> instTags) throws NoSuchUserException{
		UserTag[] subjTags = createUserTag(user,instTags);
		return tagAsrtDao.deleteRDFTypeAssertion(user, subjTags, classTag);
	}
		
	public boolean deleteSKOSRelatedTags(User user, UserTag subjectTag,
			List<String> relatedTags) throws NoSuchUserException {		
		UserTag[] objectTags = createUserTag(user, relatedTags);
		return tagAsrtDao.deleteSKOSRelatedAssertion(user, subjectTag, objectTags);	
	}
	
	public boolean deleteSKOSBroaderTags(User user, UserTag subjectTag,
			List<String> broaderTags) throws NoSuchUserException {
		UserTag[] objectTags = createUserTag(user, broaderTags);
		return tagAsrtDao.deleteSKOSBroaderAssertion(user, subjectTag, objectTags);
	}
	
	public boolean deleteSKOSNarrowerTags(User user, UserTag subjectTag,
			List<String> narrowerTags) throws NoSuchUserException {
		UserTag[] objectTags = createUserTag(user, narrowerTags);
		return tagAsrtDao.deleteSKOSNarrowerAssertion(user, subjectTag, objectTags);
	}

	public List<TagAssertion> listTagAssertion(User user)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, NoSuchTagPropertyException,
			NoSuchTagAssertionException, MissingIdException {
		logger.debug("listTagAssertion: user=" + user);
		GnizrDaoUtil.checkNull(user);
		User aUser = new User(user);
		if (GnizrDaoUtil.hasMissingId(aUser)) {
			GnizrDaoUtil.fillId(userDao, aUser);
		}
		List<TagAssertion> result = tagAsrtDao.findTagAssertion(aUser);
		for (Iterator<TagAssertion> it = result.iterator(); it.hasNext();) {
			TagAssertion ta = it.next();
			GnizrDaoUtil
					.fillObject(tagAsrtDao, tagPrptDao, tagDao, userDao, ta);
		}
		return result;
	}

	public Boolean addTagAssertion(TagAssertion assertion, boolean createTags)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, NoSuchTagPropertyException,
			MissingIdException, ParseTagException {
		logger.debug("addTagAssertion: assertion=" + assertion + ",createTags="
				+ createTags);
		GnizrDaoUtil.checkNull(assertion);
		GnizrDaoUtil.checkNull(assertion.getUser());
		GnizrDaoUtil.checkNull(assertion.getSubject());
		GnizrDaoUtil.checkNull(assertion.getObject());
		GnizrDaoUtil.checkNull(assertion.getProperty());

		User defaultUser = assertion.getUser();
		if (createTags == true) {
			UserTag subject = assertion.getSubject();
			UserTag object = assertion.getObject();
			if (GnizrDaoUtil.hasMissingId(subject)) {
				String subjTag = subject.getTag().getLabel();
				int id = GnizrDaoUtil.createUserTagIfNotExist(tagDao, userDao,
						subjTag, defaultUser);
				if (id > 0) {
					subject.setId(id);
				}
			}
			if (GnizrDaoUtil.hasMissingId(object)) {
				String objTag = object.getTag().getLabel();
				int id = GnizrDaoUtil.createUserTagIfNotExist(tagDao, userDao,
						objTag, defaultUser);
				if (id > 0) {
					object.setId(id);
				}
			}
		}
		return addTagAssertion(assertion);
	}

	public boolean addTagAssertion(TagAssertion assertion)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, NoSuchTagPropertyException,
			MissingIdException {
		logger.debug("addTagAssertion: assertion=" + assertion);
		GnizrDaoUtil.checkNull(assertion);
		User user = assertion.getUser();
		if (GnizrDaoUtil.hasMissingId(user)) {
			GnizrDaoUtil.fillId(userDao, user);
		}
		UserTag subjTag = assertion.getSubject();
		if (GnizrDaoUtil.hasMissingId(subjTag)) {
			GnizrDaoUtil.fillId(tagDao, userDao, subjTag);
		}
		UserTag objTag = assertion.getObject();
		if (GnizrDaoUtil.hasMissingId(objTag)) {
			GnizrDaoUtil.fillId(tagDao, userDao, objTag);
		}
		TagProperty prpt = assertion.getProperty();
		if (GnizrDaoUtil.hasMissingId(prpt)) {
			GnizrDaoUtil.fillId(tagPrptDao, prpt);
		}

		int id = tagAsrtDao.createTagAssertion(assertion);
		if (id > 0) {
			return true;
		}
		logger
				.debug("create tag assertion DAO returns none-positive id. DB exception?");
		return false;
	}

	public Boolean deleteTagAssertion(TagAssertion assertion)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, NoSuchTagPropertyException,
			NoSuchTagAssertionException, MissingIdException {
		logger.debug("deleteTagAssertion: assertion=" + assertion);
		GnizrDaoUtil.checkNull(assertion);
		TagAssertion obj = new TagAssertion(assertion);
		if (GnizrDaoUtil.hasMissingId(obj)) {
			GnizrDaoUtil.fillId(tagAsrtDao, tagPrptDao, tagDao, userDao, obj);
			if (obj.getId() <= 0) {
				throw new MissingIdException("tag assertion missing id");
			}
		}
		return tagAsrtDao.deleteTagAssertion(obj.getId());
	}

	public boolean deleteRDFTypeClassAssertions(User user, UserTag classTag){
		logger.debug("deleteRDFTypeClassAssertions: user="+user+",classTag="+classTag);
		boolean isOkay = false;
		try {
			GnizrDaoUtil.fillId(userDao, user);
			GnizrDaoUtil.fillId(tagDao, userDao, classTag);
			isOkay = tagAsrtDao.deleteRDFTypeClassAssertion(user, classTag);
		} catch (Exception e) {
			logger.debug(e);
		}
		return isOkay;
	}
	
	public boolean isGeoTagTypeAssertion(TagAssertion tagAssertion)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, NoSuchTagPropertyException,
			NoSuchTagAssertionException, MissingIdException {
		GnizrDaoUtil.checkNull(tagAssertion);
		TagAssertion obj = new TagAssertion(tagAssertion);
		GnizrDaoUtil.fillId(tagAsrtDao, tagPrptDao, tagDao, userDao, obj);
		TagProperty property = obj.getProperty();
		UserTag objectTag = obj.getObject();
		if (property.getId() == rdfTypeProperty.getId()
				&& objectTag.getId() == gnizrPlaceTag.getId()) {
			return true;
		}
		return false;
	}

	public Boolean updateTagAssertion(TagAssertion assertion) {
		throw new UnsupportedOperationException();
	}

	public TagAssertion getTagAssertion(int id) throws NoSuchUserException,
			NoSuchTagException, NoSuchUserTagException,
			NoSuchTagPropertyException, NoSuchTagAssertionException,
			MissingIdException {
		TagAssertion asrt = tagAsrtDao.getTagAssertion(id);
		GnizrDaoUtil.fillObject(tagAsrtDao, tagPrptDao, tagDao, userDao, asrt);
		return asrt;
	}

	public UserTag getUserTag(User user, Tag tag) throws NoSuchUserException,
			NoSuchTagException, NoSuchUserTagException, MissingIdException {
		GnizrDaoUtil.fillId(userDao, user);
		if(tag.getId() <= 0){
			int id = tagDao.createTag(tag);
			tag.setId(id);
		}
		List<UserTag> r = tagDao.findUserTag(user, tag);
		if(r != null && r.size() > 0){
			return r.get(0);
		}else{
			int utid = tagDao.createUserTag(new UserTag(user,tag));
			return tagDao.getUserTag(utid);
		}
	}

	public boolean hasTagAssertion(TagAssertion assertion) {
		boolean result = false;
		GnizrDaoUtil.checkNull(assertion);
		if (GnizrDaoUtil.hasMissingId(assertion)) {
			try {
				GnizrDaoUtil.fillObject(tagAsrtDao, tagPrptDao, tagDao,
						userDao, assertion);
				if (assertion.getId() > 0) {
					result = true;
				}
			} catch (Exception e) {
				logger.debug("has Tag Assertion == false, asrt=" + assertion);
			}
		}
		return result;
	}

	

	public UserTag getUserTag(int id) throws NoSuchUserException,
			NoSuchTagException, NoSuchUserTagException, MissingIdException {
		logger.debug("getUserTag: id=" + id);
		UserTag ut = tagDao.getUserTag(id);
		GnizrDaoUtil.fillObject(tagDao, userDao, ut);
		return ut;
	}

	
	public UserTag createUserTag(User user, String tag) {
		UserTag ut = null;
		try {
			int id = tagDao.createTag(new Tag(tag));
			Tag tagObj = tagDao.getTag(id);
			id = tagDao.createUserTag(new UserTag(user, tagObj));
			ut = tagDao.getUserTag(id);
		} catch (Exception e) {
			logger.debug(e);
		}
		return ut;
	}

	
	public TagProperty getSkosBroaderProperty() {
		return skosBroaderProperty;
	}

	public TagProperty getSkosNarrowerProperty() {
		return skosNarrowerProperty;
	}

	public TagProperty getSkosRelatedProperty() {
		return skosRelatedProperty;
	}

	public List<BookmarkTag> listBookmarkTag(User user){
		List<BookmarkTag> bTags = new ArrayList<BookmarkTag>();
		try{
			GnizrDaoUtil.fillId(userDao,user);
			bTags = tagDao.findBookmarkTag(user);
		}catch(Exception e){
			logger.debug(e);
		}
		return bTags;
	}
	
	public List<BookmarkTag> listBookmarkTagUserFolder(User user, String folderName) throws NoSuchUserException, NoSuchFolderException{
		GnizrDaoUtil.fillId(userDao, user);
		Folder f = folderDao.getFolder(user, folderName);
		List<BookmarkTag> result = null;
		if(f != null){
			result = tagDao.findBookmarkTag(f);
		}else{
			throw new NoSuchFolderException("no such folder: " + folderName);
		}
		return result;
	}
}
