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
package com.gnizr.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.HeadMethod;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchLinkTagException;
import com.gnizr.core.exceptions.NoSuchTagAssertionException;
import com.gnizr.core.exceptions.NoSuchTagException;
import com.gnizr.core.exceptions.NoSuchTagPropertyException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NoSuchUserTagException;
import com.gnizr.core.exceptions.ParseTagException;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.vocab.TimeRange;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.ForUser;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.TagProperty;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.foruser.ForUserDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.tag.TagAssertionDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.tag.TagPropertyDao;
import com.gnizr.db.dao.user.UserDao;
import com.gnizr.db.vocab.MIMEType;

/**
 * This class defines convenience methods for working gnizr data transport
 * objects.
 * 
 * @author Harry Chen
 * 
 */
public class GnizrDaoUtil {

	/**
	 * Initializes <code>user.id</code> of the input <code>User</code>
	 * object. Based on the value of <code>user.getUsername()</code>, the
	 * method tries to find the <code>id</code> of the user. If
	 * <code>UserDao</code> can't find user of the given username, then an
	 * exception is thrown.
	 * 
	 * @param userDao
	 *            the DAO object of accessing user data records
	 * @param user
	 *            a user object whose <code>id</code> value needs to be
	 *            filled. The value of <code>user.getUsername()</code> must
	 *            not be <code>null</code>.
	 * @throws NoSuchUserException
	 */
	public static void fillId(UserDao userDao, User user)
			throws NoSuchUserException {
		if (hasMissingId(user) == true) {
			User obj = getUser(userDao, user.getUsername());
			if (obj == null) {
				throw new NoSuchUserException("no such user="
						+ user.getUsername());
			}
			user.setId(obj.getId());
		}
	}

	public static void fillId(LinkDao linkDao, Link link)
			throws NoSuchLinkException {
		if (hasMissingId(link) == true) {
			Link obj = null;
			if (link.getUrl() != null) {
				obj = getLink(linkDao, link.getUrl());
			} else if (link.getUrlHash() != null) {
				obj = getLinkByUrlHash(linkDao, link.getUrlHash());
			}
			if (obj == null) {
				throw new NoSuchLinkException("no such link=" + link.getUrl());
			}
			link.setId(obj.getId());
		}
	}

	public static Link getLinkByUrlHash(LinkDao linkDao, String urlHash) {
		if (urlHash == null) {
			throw new NullPointerException("urlHash is NULL");
		}
		Link link = null;
		List<Link> links = linkDao.findLinkByUrlHash(urlHash);
		if (links.isEmpty() == false) {
			link = links.get(0);
		}
		return link;
	}

	public static void fillId(BookmarkDao bmarkDao, UserDao userDao,
			LinkDao linkDao, Bookmark bmark) throws NoSuchUserException,
			NoSuchLinkException, MissingIdException, NoSuchBookmarkException {
		if (hasMissingId(bmark) == true) {
			User user = bmark.getUser();
			if (hasMissingId(user) == true) {
				fillId(userDao, user);
			}
			Link link = bmark.getLink();
			if (hasMissingId(link) == true) {
				fillId(linkDao, link);
			}
			Bookmark obj = getBookmark(bmarkDao, user, link);
			if (obj == null) {
				throw new NoSuchBookmarkException("no such bookmark: user="
						+ user + ",link=" + link);
			}
			bmark.setId(obj.getId());
		}
	}

	private static boolean hasMissingId(ForUser forUser) {
		checkNull(forUser);
		if (forUser.getId() > 0) {
			return false;
		}
		return true;
	}

	public static void fillId(TagDao tagDao, UserDao userDao, UserTag userTag)
			throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, MissingIdException {
		if (hasMissingId(userTag) == true) {
			User user = userTag.getUser();
			Tag tag = userTag.getTag();
			fillId(userDao, user);
			fillId(tagDao, tag);
			UserTag obj = getUserTag(tagDao, user, tag);
			if (obj == null) {
				throw new NoSuchUserTagException("no such userTag: user="
						+ user.getUsername() + ",tag=" + tag.getLabel());
			}
			userTag.setId(obj.getId());
		}
	}

	public static void fillId(TagDao tagDao, LinkDao linkDao, LinkTag linkTag)
			throws NoSuchLinkException, NoSuchTagException, MissingIdException,
			NoSuchLinkTagException {
		if (hasMissingId(linkTag) == true) {
			Link link = linkTag.getLink();
			Tag tag = linkTag.getTag();
			fillId(linkDao, link);
			fillId(tagDao, tag);
			LinkTag obj = getLinkTag(tagDao, link, tag);
			if (obj == null) {
				throw new NoSuchLinkTagException("no such linkTag=" + linkTag);
			}
			linkTag.setId(obj.getId());
		}
	}

	public static void fillId(TagDao tagDao, Tag tag) throws NoSuchTagException {
		if (hasMissingId(tag) == true) {
			Tag obj = getTag(tagDao, tag.getLabel());
			if (obj == null) {
				throw new NoSuchTagException("no such tag=" + tag);
			}
			tag.setId(obj.getId());
		}
	}

	public static void fillId(TagPropertyDao tagPrptDao, TagProperty tagPrpt)
			throws NoSuchTagPropertyException {
		if (hasMissingId(tagPrpt) == true) {
			TagProperty obj = null;
			String name = tagPrpt.getName();
			String ns = tagPrpt.getNamespacePrefix();
			if (name != null && ns != null) {
				obj = tagPrptDao.getTagProperty(ns, name);
			}
			if (obj == null) {
				throw new NoSuchTagPropertyException("no such tagPrpt: ns="
						+ ns + ",name=" + name);
			}
			tagPrpt.setId(obj.getId());
		}
	}

	public static void fillId(TagAssertionDao tagAssertionDao,
			TagPropertyDao tagPrptDao, TagDao tagDao, UserDao userDao,
			TagAssertion assertion) throws NoSuchUserException,
			NoSuchTagException, NoSuchUserTagException,
			NoSuchTagPropertyException, NoSuchTagAssertionException,
			MissingIdException {
		if (hasMissingId(assertion)) {
			User user = assertion.getUser();
			UserTag subjTag = assertion.getSubject();
			TagProperty prpt = assertion.getProperty();
			UserTag objTag = assertion.getObject();
			fillId(userDao, user);
			fillId(tagDao, userDao, subjTag);
			fillId(tagPrptDao, prpt);
			fillId(tagDao, userDao, objTag);
			TagAssertion obj = getTagAssertion(tagAssertionDao, user, subjTag,
					prpt, objTag);
			if (obj == null) {
				throw new NoSuchTagAssertionException(
						"no such tagAssertion: user=" + user + ",subjTag="
								+ subjTag + ",prpt=" + prpt + ",objTag="
								+ objTag);
			}
			assertion.setId(obj.getId());
		}
	}

	public static boolean hasMissingId(User user) {
		checkNull(user);
		return (user.getId() <= 0);
	}

	public static boolean hasMissingId(Link link) {
		checkNull(link);
		return (link.getId() <= 0);
	}

	public static boolean hasMissingId(Bookmark bm) {
		checkNull(bm);
		return (bm.getId() <= 0);
	}

	public static boolean hasMissingId(Tag tag) {
		checkNull(tag);
		return (tag.getId() <= 0);
	}

	public static boolean hasMissingId(UserTag userTag) {
		checkNull(userTag);
		return (userTag.getId() <= 0);
	}

	
	public static boolean hasMissingId(LinkTag linkTag) {
		checkNull(linkTag);
		return (linkTag.getId() <= 0);
	}

	public static boolean hasMissingId(TagProperty tagPrpt) {
		checkNull(tagPrpt);
		return (tagPrpt.getId() <= 0);
	}

	public static boolean hasMissingId(TagAssertion tagAssertion) {
		checkNull(tagAssertion);
		return (tagAssertion.getId() <= 0);
	}

	public static User getUser(UserDao userDao, String username) {
		if (username == null) {
			throw new NullPointerException("username is NULL");
		}
		User user = null;
		List<User> users = userDao.findUser(username);
		if (users.isEmpty() == false) {
			user = users.get(0);
		}
		return user;
	}

	public static Link getLink(LinkDao linkDao, String url) {
		if (url == null) {
			throw new NullPointerException("url is NULL");
		}
		Link link = null;
		List<Link> links = linkDao.findLink(url);
		if (links.isEmpty() == false) {
			link = links.get(0);
		}
		return link;
	}

	public static Bookmark getBookmark(BookmarkDao bmarkDao, User user,
			Link link) throws MissingIdException {
		if (hasMissingId(user) == false && hasMissingId(link) == false) {
			Bookmark bmark = null;
			List<Bookmark> bmarks = bmarkDao.findBookmark(user, link);
			if (bmarks.isEmpty() == false) {
				bmark = bmarks.get(0);
			}
			return bmark;
		}
		throw new MissingIdException(
				"either user or link object is missing a valid id");
	}

	public static Tag getTag(TagDao tagDao, String tag) {
		if (tag == null) {
			throw new NullPointerException("tag is NULL");
		}
		Tag aTag = null;
		List<Tag> tags = tagDao.findTag(tag);
		if (tags.isEmpty() == false) {
			aTag = tags.get(0);
		}
		return aTag;
	}

	public static UserTag getUserTag(TagDao tagDao, User user, Tag tag)
			throws MissingIdException {
		if (hasMissingId(user) == false && hasMissingId(tag) == false) {
			UserTag aTag = null;
			List<UserTag> aTags = tagDao.findUserTag(user, tag);
			if (aTags.isEmpty() == false) {
				aTag = aTags.get(0);
			}
			return aTag;
		}
		throw new MissingIdException(
				"either user or tag object is missing a valid id");
	}

	public static LinkTag getLinkTag(TagDao tagDao, Link link, Tag tag)
			throws MissingIdException {
		if (hasMissingId(link) == false && hasMissingId(tag) == false) {
			LinkTag aTag = null;
			List<LinkTag> aTags = tagDao.findLinkTag(link, tag);
			if (aTags.isEmpty() == false) {
				aTag = aTags.get(0);
			}
			return aTag;
		}
		throw new MissingIdException(
				"either link or tag object is missing a valid id");
	}

	public static TagAssertion getTagAssertion(TagAssertionDao tagAssertionDao,
			User user, UserTag subjTag, TagProperty tagPrpt, UserTag objTag)
			throws MissingIdException {
		if (hasMissingId(user) == true) {
			throw new MissingIdException("user object is missing a valid id");
		}
		if (hasMissingId(subjTag) == true) {
			throw new MissingIdException("subjectTag is missing a valid id");
		}
		if (hasMissingId(objTag) == true) {
			throw new MissingIdException("objectTag is missing a valid id");
		}
		if (hasMissingId(tagPrpt) == true) {
			throw new MissingIdException("tagProperty is missing a valid id");
		}
		TagAssertion asrt = null;
		List<TagAssertion> asrts = tagAssertionDao.findTagAssertion(user,
				subjTag, tagPrpt, objTag);
		if (asrts.isEmpty() == false) {
			asrt = asrts.get(0);
		}
		return asrt;
	}

	public static Integer detectMIMEType(String url) {
		try {
			HttpClient httpClient = new HttpClient();
			HeadMethod method = new HeadMethod(url);
			method.getParams().setIntParameter("http.socket.timeout",5000);
			int code = httpClient.executeMethod(method);
			if (code == 200) {
				Header h = method.getResponseHeader("Content-Type");
				if (h != null) {
					HeaderElement[] headElm = h.getElements();
					if(headElm != null & headElm.length > 0){
						String mimeType = headElm[0].getValue();
						if(mimeType == null){
							mimeType = headElm[0].getName();
						}
						if (mimeType != null) {
							return getMimeTypeIdCode(mimeType);
						}
					}
				}
			}
		} catch (Exception e) {
			// no code;
		}
		return MIMEType.UNKNOWN;
	}

	private static int getMimeTypeIdCode(String mimeType) {
		if (mimeType.equals("text/xml")) {
			return MIMEType.TEXT_XML;
		} else if (mimeType.equals("text/plain")) {
			return MIMEType.TEXT_PLAIN;
		} else if (mimeType.equals("text/html")) {
			return MIMEType.TEXT_HTML;
		} else if (mimeType.equals("image/jpeg")) {
			return MIMEType.IMG_JPEG;
		} else if (mimeType.equals("image/png")) {
			return MIMEType.IMG_PNG;
		} else if (mimeType.equals("image/tiff")) {
			return MIMEType.IMG_TIFF;
		} else if (mimeType.equals("image/gif")) {
			return MIMEType.IMG_GIF;
		}else if(mimeType.equals("application/rss+xml")){
			return MIMEType.APP_RSS_XML;
		}else if(mimeType.equals("application/rdf+xml")){
			return MIMEType.APP_RDF_XML;
		}else if(mimeType.equals("application/owl+xml")){
			return MIMEType.APP_OWL_XML;
		}
		return MIMEType.UNKNOWN;
	}

	public static Date getNow() {
		return GregorianCalendar.getInstance().getTime();
	}

	public static void checkNull(User user) {
		if (user == null) {
			throw new NullPointerException("User is NULL");
		}
	}

	public static void checkNull(Tag tag) {
		if (tag == null) {
			throw new NullPointerException("Tag is NULL");
		}
	}

	public static void checkNull(Link link) {
		if (link == null) {
			throw new NullPointerException("Link is NULL");
		}
	}

	public static void checkNull(Bookmark bmark) {
		if (bmark == null) {
			throw new NullPointerException("Bookmark is NULL");
		}
	}

	public static void checkNull(UserTag userTag) {
		if (userTag == null) {
			throw new NullPointerException("UserTag is NULL");
		}
	}

	public static void checkNull(LinkTag linkTag) {
		if (linkTag == null) {
			throw new NullPointerException("LinkTag is NULL");
		}
	}

	public static void checkNull(TagProperty tagPrpt) {
		if (tagPrpt == null) {
			throw new NullPointerException("TagProperty is NULL");
		}
	}

	public static void checkNull(TagAssertion assertion) {
		if (assertion == null) {
			throw new NullPointerException("TagAssertion is NULL");
		}
	}



	public static void fillObject(TagDao tagDao, UserDao userDao,
			UserTag userTag) throws NoSuchUserException, NoSuchTagException,
			NoSuchUserTagException, MissingIdException {
		if (hasMissingId(userTag) == true) {
			fillId(tagDao, userDao, userTag);
		}
		UserTag ut = tagDao.getUserTag(userTag.getId());
		Tag t = ut.getTag();
		User u = ut.getUser();
		fillObject(tagDao, t);
		fillObject(userDao, u);
		userTag.setTag(t);
		userTag.setUser(u);
		userTag.setCount(ut.getCount());
	}

	public static void fillObject(TagDao tagDao, LinkDao linkDao,
			LinkTag linkTag) throws NoSuchLinkException, NoSuchTagException,
			MissingIdException, NoSuchLinkTagException, NoSuchUserException {
		if (hasMissingId(linkTag) == true) {
			fillId(tagDao, linkDao, linkTag);
		}
		LinkTag obj = tagDao.getLinkTag(linkTag.getId());
		Link l = obj.getLink();
		Tag t = obj.getTag();
		fillObject(linkDao, l);
		fillObject(tagDao, t);
		linkTag.setLink(l);
		linkTag.setTag(t);
		linkTag.setCount(obj.getCount());
	}

	public static void fillObject(TagDao tagDao, Tag tag)
			throws NoSuchTagException {
		fillId(tagDao, tag);
		Tag t = tagDao.getTag(tag.getId());
		tag.setLabel(t.getLabel());
		tag.setCount(t.getCount());
	}

	/**
	 * Initializes all parameters of the input <code>user</code> object. Based
	 * on the value of <code>user.getId()</code>, this method finds a
	 * matching user data record and then sets the parameters of
	 * <code>user</code> with the parameter values of the user data record.
	 * 
	 * @param userDao
	 *            the DAO object used to find a matching user data record
	 * @param user
	 *            a <code>User</code> with partially initialized parameter
	 *            values. The value of <code>user.getId()</code> must return a
	 *            valid ID number and must not be <code>null</code>.
	 * @throws NoSuchUserException
	 *             thrown if no user data record exists for the input user ID.
	 */
	public static void fillObject(UserDao userDao, User user)
			throws NoSuchUserException {
		fillId(userDao, user);
		User u = userDao.getUser(user.getId());
		user.setFullname(u.getFullname());
		user.setPassword(u.getPassword());
		user.setUsername(u.getUsername());
		user.setEmail(u.getEmail());
		user.setCreatedOn(u.getCreatedOn());
		user.setAccountStatus(u.getAccountStatus());
	}

	public static void fillObject(BookmarkDao bmarkDao, UserDao userDao,
			LinkDao linkDao, Bookmark bmark) throws NoSuchUserException,
			NoSuchLinkException, MissingIdException, NoSuchBookmarkException {
		if (hasMissingId(bmark) == true) {
			fillId(bmarkDao, userDao, linkDao, bmark);
		}
		Bookmark bm = bmarkDao.getBookmark(bmark.getId());
		User u = bm.getUser();
		Link l = bm.getLink();
		fillObject(userDao, u);
		fillObject(linkDao, l);
		bmark.setLink(l);
		bmark.setUser(u);
		bmark.setNotes(bm.getNotes());
		bmark.setTags(bm.getTags());
		bmark.setTitle(bm.getTitle());
		if (bm.getCreatedOn() != null) {
			bmark.setCreatedOn((Date) bm.getCreatedOn().clone());
		} else {
			bmark.setCreatedOn(null);
		}
		if (bm.getLastUpdated() != null) {
			bmark.setLastUpdated((Date) bm.getLastUpdated().clone());
		} else {
			bmark.setLastUpdated(null);
		}
	}

	public static void fillObject(LinkDao linkDao, Link link)
			throws NoSuchLinkException {
		fillId(linkDao, link);
		Link l = linkDao.getLink(link.getId());
		if (l != null) {
			link.setCount(l.getCount());
			link.setMimeTypeId(l.getMimeTypeId());
			link.setUrl(l.getUrl());
			link.setUrlHash(l.getUrlHash());
		}
	}

	public static void fillObject(TagPropertyDao tagPrptDao, TagProperty tagPrpt)
			throws NoSuchTagPropertyException {
		fillId(tagPrptDao, tagPrpt);
		TagProperty p = tagPrptDao.getTagProperty(tagPrpt.getId());
		tagPrpt.setName(p.getName());
		tagPrpt.setDescription(p.getDescription());
		tagPrpt.setNamespacePrefix(p.getNamespacePrefix());
		tagPrpt.setPropertyType(p.getPropertyType());
		tagPrpt.setCardinality(p.getCardinality());
	}

	public static void fillObject(TagAssertionDao tagAssertionDao,
			TagPropertyDao tagPrptDao, TagDao tagDao, UserDao userDao,
			TagAssertion assertion) throws NoSuchUserException,
			NoSuchTagException, NoSuchUserTagException,
			NoSuchTagPropertyException, NoSuchTagAssertionException,
			MissingIdException {
		if (hasMissingId(assertion) == true) {
			fillId(tagAssertionDao, tagPrptDao, tagDao, userDao, assertion);
		}
		TagAssertion ta = tagAssertionDao.getTagAssertion(assertion.getId());
		UserTag s = ta.getSubject();
		TagProperty p = ta.getProperty();
		UserTag o = ta.getObject();
		User u = ta.getUser();
		fillObject(tagDao, userDao, s);
		fillObject(tagDao, userDao, o);
		fillObject(tagPrptDao, p);
		fillObject(userDao, u);
		assertion.setUser(u);
		assertion.setSubject(s);
		assertion.setProperty(p);
		assertion.setObject(o);
	}

	/**
	 * Creates a <code>Tag</code> data record for the input <code>tag</code>
	 * string. If a data record of the same <code>tag</code> string already
	 * exists in the database, then the ID of that data record is returned.
	 * Otherwise, returns the ID of the newly created <code>Tag</code> data
	 * record.
	 * 
	 * @param tagDao
	 *            the DAO object used for creating/fetching data record
	 * @param tag
	 *            a tag string, which may be a prefixed tag string (e.g.,
	 *            <code>gnizr:javaprogramming</code>) or a plain tag string
	 *            (e.g., <code>javaprogramming</code>)
	 * @return the ID of the <code>Tag<code> data records
	 * @throws ParseTagException an exception is thrown if unable to parse
	 * the input <code>tag</code> string.
	 *
	 */
	public static int createTagIfNotExist(TagDao tagDao, String tag)
			throws ParseTagException {
		Tag tagObj = null;
		String tagLabel = null;
		if (TagUtil.isPrefixedUserTag(tag) == true) {
			UserTag ut = TagUtil.parsePrefixedUserTag(tag);
			if (ut != null) {
				tagLabel = ut.getTag().getLabel();
			} else {
				throw new ParseTagException("unable to parse tag: " + tag);
			}
		} else {
			tagLabel = tag;
		}
		List<Tag> tags = tagDao.findTag(tagLabel);
		if (tags.isEmpty()) {
			tagObj = new Tag(tagLabel);
			int id = tagDao.createTag(tagObj);
			tagObj.setId(id);
		} else {
			tagObj = tags.get(0);
		}
		return tagObj.getId();
	}

	public static final int createUserTagIfNotExist(TagDao tagDao,
			UserDao userDao, String tag, User defaultUser)
			throws ParseTagException, NoSuchUserException {
		int userTagId = 0;
		UserTag tagParsed = null;
		if (TagUtil.isPrefixedUserTag(tag)) {
			tagParsed = TagUtil.parsePrefixedUserTag(tag);
			if (tagParsed == null) {
				throw new ParseTagException("unable to parse tag: " + tag);
			}
		} else {
			checkNull(defaultUser);
			tagParsed = new UserTag(defaultUser.getUsername(), tag);
		}
		Tag tagObj = new Tag();
		User userObj = new User();

		// fills the id of the tag
		int tagId = createTagIfNotExist(tagDao, tagParsed.getTag().getLabel());
		tagObj.setId(tagId);

		// fills the id of the user
		userObj.setUsername(tagParsed.getUser().getUsername());
		GnizrDaoUtil.fillId(userDao, userObj);
		if (tagObj.getId() > 0 && userObj.getId() > 0) {
			List<UserTag> ut = tagDao.findUserTag(userObj, tagObj);
			if (ut.isEmpty()) {
				userTagId = tagDao.createUserTag(new UserTag(userObj, tagObj));
			} else {
				UserTag userTag = ut.get(0);
				userTagId = userTag.getId();
			}
		}
		return userTagId;
	}

	public static void checkNull(ForUser forUser) {
		if (forUser == null) {
			throw new NullPointerException("forUser is NULL");
		}
	}

	public static void fillObject(ForUserDao forUserDao,
			BookmarkDao bookmarkDao, UserDao userDao, LinkDao linkDao,
			ForUser forUser) throws MissingIdException, NoSuchUserException,
			NoSuchLinkException, NoSuchBookmarkException {
		if (hasMissingId(forUser) == true) {
			throw new MissingIdException("missing forUser.getId()");
		}
		Bookmark bm = forUser.getBookmark();
		fillObject(bookmarkDao, userDao, linkDao, bm);
		User user = forUser.getForUser();
		fillObject(userDao, user);
	}

	/**
	 * Returns a <code>Date</code> object that represents the beginning
	 * hour,min,second of a day (i.e., 00:00:00). The returned object shares the
	 * same year, month, day values as the input <code>date</code>
	 * 
	 * @param date
	 *            an instantiated date
	 * @return the beginning hour/min/sec of <code>date</code>
	 */
	public static Date toDayBegins(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, cal
				.getActualMinimum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMinimum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMinimum(Calendar.SECOND));
		return cal.getTime();
	}

	/**
	 * Returns a <code>Date</code> object that represents the ending
	 * hour,min,second of a day (i.e., 23:59:59). The returned object shares the
	 * same year, month, day values as the input <code>date</code>
	 * 
	 * @param date
	 *            an instantiated date
	 * @return the ending hour/min/sec of <code>date</code>
	 */
	public static Date toDayEnds(Date date) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, cal
				.getActualMaximum(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
		cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
		return cal.getTime();
	}

	public static Date addNHours(Date date, int nHours) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, nHours);
		return cal.getTime();
	}

	public static Date subNHours(Date date, int nHours) {
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, (nHours * -1));
		return cal.getTime();
	}

	public static Date[] getTimeRangeDate(int timeRange) {
		Date[] dates = new Date[2];
		Date now = GnizrDaoUtil.getNow();
		dates[0] = GnizrDaoUtil.toDayBegins(now);
		dates[1] = GnizrDaoUtil.toDayEnds(now);
		if (TimeRange.TODAY == timeRange) {
			return dates;
		} else if (TimeRange.YESTERDAY == timeRange) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dates[0]);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			dates[0] = cal.getTime();

			cal.setTime(dates[1]);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			dates[1] = cal.getTime();
		} else if (TimeRange.LAST_7_DAYS == timeRange) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dates[0]);
			cal.add(Calendar.DAY_OF_MONTH, -6);
			dates[0] = cal.getTime();
		} else if (TimeRange.THIS_MONTH == timeRange) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dates[0]);
			cal.set(Calendar.DAY_OF_MONTH, cal
					.getActualMinimum(Calendar.DAY_OF_MONTH));
			dates[0] = cal.getTime();
		} else if (TimeRange.LAST_MONTH == timeRange) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(dates[0]);
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, cal
					.getActualMinimum(Calendar.DAY_OF_MONTH));
			dates[0] = cal.getTime();

			cal.setTime(dates[1]);
			cal.add(Calendar.MONTH, -1);
			cal.set(Calendar.DAY_OF_MONTH, cal
					.getActualMaximum(Calendar.DAY_OF_MONTH));
			dates[1] = cal.getTime();
		} else {
			return null;
		}
		return dates;
	}

	
	public static boolean isUrlSafeString(String s){
		if(s != null){
			if(s.matches(".*[?/\\\\;'\"&+*^%]+.*")){
				return false;
			}
		}else{
			return false;
		}
		return true;
	}
	
	public static boolean isLegalFolderName(String s){
		if(s == null || s.length() == 0){
			return false;
		}else{
			if(isUrlSafeString(s) && !s.contains("_")){
				return true;
			}else if(s.equals(FolderManager.MY_BOOKMARKS_LABEL) || 
					 s.equals(FolderManager.IMPORTED_BOOKMARKS_LABEL)){
				return true;
			}
		}
		return false;
	}
	
	public static String encodeURI(String uri){
		try {
			return URLEncoder.encode(uri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		
			e.printStackTrace();
		}
		return null;
	
	}
	
	/**
	 * Returns a list of String objects that appear aList but don't appear
	 * in bList.
	 * @param aList
	 * @param bList
	 * @return objects in aList but not in bList
	 */
	public static List<String> diffList(List<String> aList, List<String> bList){
		List<String> result = new ArrayList<String>();
		for(String a : aList){
			if(bList.contains(a) == false){
				result.add(a);
			}
		}
		return result;
	}
	
	public static String stripNonPrintableChar(String s){
		if(s != null){
			return s.replaceAll("[\\t\\n\\r\\f\\a\\e]+"," ");
		}
		return s;
	}
	
	/**
	 * Creates a <code>Map</code> view from a list of <code>Bookmark</code> objects. Map keys are
	 * created from <code>Bookmark.getId</code> and map values are object references to
	 * <code>bookmarks</code>. If multiple objects in <code>bookmarks</code> have the same <code>Bookmark.getId</code> 
	 * value, only one of those objects will appear in the output <code>Map</code>.
	 * 
	 * @param bookmarks objects with instantiated bookmark ID that will be used to create a <code>Map</code> 
	 * 
	 * @return a <code>Map</code> of ID (key) and <code>Bookmark</code> (value). This method will always return an instantiated
	 * <code>Map</code> object, even if <code>bookmarks</code> is <code>null</code>. 
	 */
	public static Map<Integer,Bookmark> getBookmarksMap(List<Bookmark> bookmarks){
		Map<Integer,Bookmark> map = new HashMap<Integer, Bookmark>();
		if(bookmarks != null){
			for(Bookmark b : bookmarks){
				map.put(b.getId(),b);
			}
		}
		return map;
	}
	
	public static String getRandomURI(){
		StringBuffer sb = new StringBuffer("urn-x:gnizr:");
		sb.append(UUID.randomUUID().toString());
		return sb.toString();
	}
}
