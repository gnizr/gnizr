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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.NotAuthorizedException;
import com.gnizr.core.exceptions.ServiceTermintedException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.TagUtil;
import com.gnizr.db.GnizrDao;
import com.gnizr.db.MIMEType;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.bookmark.BookmarkDao;
import com.gnizr.db.dao.bookmark.GeometryMarkerDao;
import com.gnizr.db.dao.link.LinkDao;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.user.UserDao;
import com.gnizr.db.vocab.UserSchema;

/**
 * <p>A manager for editing <code>Bookmark</code> and saving changes to the backend database. 
 * Typically clients should always use this class to create and modify bookmark objects in the database to ensure
 * indexing and accountings are properly computed.</p>
 * <p>Clients can register listeners with this manager to listen for 
 * bookmark change events (e.g., new bookmarks added, existing bookmarks deleted and updated). If one or more 
 * listeners are registered, client programs must call <code>shutdown</code> upon program termination.</p>
 * <p>This class also provides methods for "bulk" editing bookmark tags and mangaging <code>PointMarker</code> objects
 * associated with a given bookmark.
 * </p>
 * 
 * @author Harry Chen
 *
 */
public class BookmarkManager implements Serializable {

	private static final Logger logger = Logger
			.getLogger(BookmarkManager.class);

	private static final long serialVersionUID = -717938169778226275L;

	private BookmarkDao bookmarkDao;

	private User gnizrUser;

	private LinkDao linkDao;

	private TagDao tagDao;

	private UserDao userDao;

	private GeometryMarkerDao geomMarkerDao;

	private Queue<BookmarkListener> listeners = new ConcurrentLinkedQueue<BookmarkListener>();

	private static final int DEFAULT_WORKER_THREAD_NUM = 2;

	private ExecutorService listenerExecutor;

	private AtomicBoolean isTerminated;

	/**
	 * Creates an instance of this class and sets the DAO object will be used
	 * to access the backend database. 
	 * 
	 * @param gnizrDao the DAO object used for accessing database
	 */
	public BookmarkManager(GnizrDao gnizrDao) {
		this(gnizrDao, null, DEFAULT_WORKER_THREAD_NUM);
	}

	/**
	 * Creates an instance of this class, sets the DAO object will be used
	 * to access the backend database, and registers bookmark change event listeners.
	 * 
	 * @param gnizrDao the DAO object used for accessing database
	 * @param listeners listeners will be notified when one of the bookmark change event occured
	 * @param workerThreadNum the number of threads should be used to notify bookmark change evnet listeners.  
	 */
	public BookmarkManager(GnizrDao gnizrDao, List<BookmarkListener> listeners,
			int workerThreadNum) {
		this.userDao = gnizrDao.getUserDao();
		this.linkDao = gnizrDao.getLinkDao();
		this.bookmarkDao = gnizrDao.getBookmarkDao();
		this.tagDao = gnizrDao.getTagDao();
		this.geomMarkerDao = gnizrDao.getGeometryMarkerDao();
		this.gnizrUser = GnizrDaoUtil.getUser(userDao, UserSchema.GNIZR_USER);
		if (this.gnizrUser == null) {
			logger
					.error("Database is properly initialized. Missing user: gnizr");
		}
		if (listeners != null) {
			this.listeners.addAll(listeners);
		}
		int nThread = DEFAULT_WORKER_THREAD_NUM;
		if (workerThreadNum >= 1) {
			nThread = workerThreadNum;
		}
		this.listenerExecutor = Executors.newFixedThreadPool(nThread);
		this.isTerminated = new AtomicBoolean(false);
	}

	private void checkIsTerminited() {
		if (isTerminated.get() == true) {
			throw new ServiceTermintedException(
					"BookmarkManager has been terminted");
		}
	}

	/**
	 * Adds a listener to the current list of bookmark change event listeners.
	 * 
	 * @param listener a new listener
	 */
	public void addBookmarkListener(BookmarkListener listener) {
		listeners.add(listener);
	}

	/**
	 * Deletes a listener from the current list of bookmark change event listeners.
	 * 
	 * @param listener a listener to be removed from the list.
	 * @return Return <code>true</code> if <code>listener</code> is successfully rmeoved. Returns
	 * <code>false</code> otherwise.
	 */
	public boolean deleteBookmarkListener(BookmarkListener listener) {
		return listeners.remove(listener);
	}

	/**
	 * Returns a list of tags that is prepared for tag accouting. Duplicated
	 * tags are removed. Machine tags are shouldn't appear in the tag cloud are
	 * also removed.
	 * 
	 * @param unsafeTags
	 * @return
	 */
	private String getSafeTags(List<String> unsafeTags) {
		StringBuilder sb = new StringBuilder();
		List<String> taglist = TagUtil.sortUniqueTags(unsafeTags);
		List<String> sysTags = TagUtil.systemTags(taglist);
		for (String t : taglist) {
			String st = TagUtil.makeSafeTagString(t);
			if (st != null && st.length() > 0) {
				if (sysTags.contains(st) == false) {
					sb.append(st);
					sb.append(" ");
				}
			}
		}
		return sb.toString().trim();
	}

	private List<String> getSafeTagsList(List<String> unsafeTags) {
		String s = getSafeTags(unsafeTags);
		String[] tags = s.split("\\s+");
		return Arrays.asList(tags);
	}

	/**
	 * Adds a new bookmark record to the backend database and returns the ID
	 * of the bookmark in the database. A bookmark is added only if 
	 * it doesn't already exist in the database (i.e., no bookmark of the same URL and owner user 
	 * exists in the database). If a bookmark of the same URL and owner user exists, 
	 * the ID of the existing bookmark is returned.
	 * 
	 * @param bookmark a new bookmark to be added to the database
	 * @return the ID of the bookmark
	 * @throws NoSuchUserException The user defined by <code>Bookmark.getUser<code> doesn't currently exist 
	 * in the database. 
	 */
	public int addBookmark(Bookmark bookmark) throws NoSuchUserException {
		if (bookmark == null) {
			throw new NullPointerException("bookmark is null");
		}
		Bookmark existBmark = null;
		if (bookmark.getId() > 0) {
			existBmark = bookmarkDao.getBookmark(bookmark.getId());
		} else if (bookmark.getUser() != null && bookmark.getLink() != null) {
			User user = bookmark.getUser();
			Link link = bookmark.getLink();
			try {
				GnizrDaoUtil.fillId(linkDao, link);
				GnizrDaoUtil.fillId(userDao, user);
				existBmark = GnizrDaoUtil.getBookmark(bookmarkDao, user, link);
			} catch (Exception e) {
				// if exception, that's okay, which means no exist record of the
				// same bookmark exists
			}
		}
		if (existBmark != null) {
			return existBmark.getId();
		} else {
			return addBookmarkUpdateTags(bookmark);
		}
	}

	private int addBookmarkUpdateTags(Bookmark bookmark) throws NoSuchUserException {
		logger.debug("addBookmarkUpdateTags: bookmark=" + bookmark);

		checkIsTerminited();

		GnizrDaoUtil.checkNull(bookmark);
		GnizrDaoUtil.checkNull(bookmark.getUser());
		GnizrDaoUtil.checkNull(bookmark.getLink());

		// make a local copy
		Bookmark bmark = new Bookmark(bookmark);
		if (bmark.getTitle() == null) {
			bmark.setTitle("");
		}
		if (bmark.getNotes() == null) {
			bmark.setNotes("");
		}
		if (bmark.getTags() == null) {
			bmark.setTags("");
		}

		User user = bmark.getUser();
		Link link = bmark.getLink();

		// we assume the user already exists in the DB
		// if it doesn't exist, an exception will be thrown
		GnizrDaoUtil.fillId(userDao, user);

		// if no link.id is specified, check if it already exists
		// by lookup a link record of the specified URL
		if (GnizrDaoUtil.hasMissingId(link) == true) {
			link = GnizrDaoUtil.getLink(linkDao, link.getUrl());
			// if doesn't already exists, create a new link based
			// the information defined in the bookmark
			if (link == null) {
				link = new Link();
				int linkId = createNewLinkFromBookmark(bmark);
				if (linkId > 0) {
					// if a new link is created successfully,
					// fetch the link object
					link = linkDao.getLink(linkId);
				}
			}
			bmark.setLink(link);
		}
		// if no date properties set, we will set them as of now.
		if (bmark.getCreatedOn() == null) {
			bmark.setCreatedOn(GnizrDaoUtil.getNow());
		}
		if (bmark.getLastUpdated() == null) {
			bmark.setLastUpdated(GnizrDaoUtil.getNow());
		}

		bmark.setTags(getSafeTags(bmark.getTagList()));

		// create a bookmark DB record
		int bmId = bookmarkDao.createBookmark(bmark);
		// if the record is created successfully
		if (bmId > 0) {
			try {
				bmark.setId(bmId);
				List<Tag> tagObjs = initTagEntries(bmark, bmark.getTagList());
				boolean[] opOkay = tagDao.addTagCountOne(tagObjs
						.toArray(new Tag[0]), bmark.getUser(), bmark.getLink(),
						bmark);
				for (int i = 0; i < opOkay.length; i++) {
					if (opOkay[i] == false) {
						logger.debug("addTagCountOne failed: tag="
								+ tagObjs.get(i) + ",bmark=" + bmark);
					}
				}
				// when notifying all listener, use the original tag list.
				bmark.setTags(bookmark.getTags());
				listenerExecutor.execute(new RunBookmarkAddedNotify(bmark));
			} catch (Exception e) {
				logger.error("error occured RunBookmarkAddedNotify", e);
			}
		}
		return bmId;
	}

	private List<Tag> getTags(List<String> tagList) {
		logger.debug("getTags: tagList=" + tagList);
		List<Tag> tags = new ArrayList<Tag>();
		for (String t : tagList) {
			Tag aTag = GnizrDaoUtil.getTag(tagDao, t);
			if (aTag != null) {
				tags.add(aTag);
			}
		}
		return tags;
	}

	private List<Tag> initTagEntries(Bookmark bmark, List<String> tags) {
		logger.debug("initTagEntries: bmark=" + bmark);
		List<Tag> tagObj = new ArrayList<Tag>();
		Link link = bmark.getLink();
		User user = bmark.getUser();
		int pos = 1;
		for (String t : tags) {
			Tag tag = new Tag(t);
			tag.setId(tagDao.createTag(tag));
			tagObj.add(tag);

			int id = 0;
			id = tagDao.createUserTag(new UserTag(user, tag));
			logger.debug("userTag id = " + id);
			id = tagDao.createLinkTag(new LinkTag(link, tag));
			logger.debug("linkTag id = " + id);
			id = tagDao.getBookmarkTagId(bmark, tag);
			if (id <= 0) {
				id = tagDao.createBookmarkTag(new BookmarkTag(0, bmark, tag, 0,
						pos));
			} else {
				BookmarkTag bmtag = new BookmarkTag(id);
				bmtag.setBookmark(bmark);
				bmtag.setTag(tag);
				bmtag.setCount(0);
				bmtag.setPosition(pos);
				tagDao.updateBookmarkTag(bmtag);
			}
			pos++;
			logger.debug("bookmarkTag id = " + id);
		}
		return tagObj;
	}

	private int createNewLinkFromBookmark(Bookmark bookmark) {
		Link link = bookmark.getLink();
		link.setMimeTypeId(MIMEType.UNKNOWN);
		return linkDao.createLink(link);
	}

	/**
	 * Deletes a bookmark record from the databaes. The bookmark record to be deleted 
	 * is a record whose ID matches <code>bookmark.getId</code>. If the record is sucessfully deleted,
	 * registered bookmark change event listeners will be notified.
	 * 
	 * @param bookmark a bookmark record to be deleted from the database
	 * @return Return <code>true</code> if the record is successfully removed. 
	 * Returns <code>false</code>, otherwise. 
	 * @throws MissingIdException The ID of <code>bookmark</code> is invalid 
	 * (i.e., <code>bookmark.getId</code> &lt= <code>0</code>).
	 */
	public boolean deleteBookmark(Bookmark bookmark)
			throws MissingIdException {

		checkIsTerminited();

		GnizrDaoUtil.checkNull(bookmark);
		if (GnizrDaoUtil.hasMissingId(bookmark) == true) {
			throw new MissingIdException("bookmark is missing a valid id");
		}
		// make a local copy
		Bookmark delBmark = getBookmark(bookmark.getId());
		List<Tag> tagObjs = getTags(delBmark.getTagList());
		tagDao.subtractTagCountOne(tagObjs.toArray(new Tag[0]), delBmark
				.getUser(), delBmark.getLink(), delBmark);

		// attempt to delete the bookmark
		if (bookmarkDao.deleteBookmark(delBmark.getId()) == true) {
			try {
				listenerExecutor
						.execute(new RunBookmarkDeletedNotify(delBmark));
			} catch (Exception e) {
				logger.debug(e);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Updates the properties of a existing bookmark record in the databaes. The bookmark record to be updated is 
	 * the record whose ID matches <code>upBookmark.getId</code>. If the record is successfully updated,
	 * then registered bookmark change event listeners will be notified.
	 * 
	 * @param upBookmark the new set of bookmark properties to be set in the database.
	 * @return Returns <code>true</code> if the update operation is successful. Return <code>false</code>, otherwise.
	 * @throws MissingIdException The ID of <code>bookmark</code> is invalid 
	 * (i.e., <code>bookmark.getId</code> &lt= <code>0</code>).
	 */
	public boolean updateBookmark(Bookmark upBookmark)
			throws MissingIdException {

		checkIsTerminited();

		Bookmark oldBookmark = null;
		Bookmark bookmark = new Bookmark(upBookmark);
		bookmark.setTags(getSafeTags(bookmark.getTagList()));

		GnizrDaoUtil.checkNull(bookmark);
		if (GnizrDaoUtil.hasMissingId(bookmark) == true) {
			throw new MissingIdException("bookmark is missing a valid id");
		}

		// get a copy of the bookmark before it gets modified
		oldBookmark = getBookmark(bookmark.getId());

		// if bookmark-to-update doesn't provide
		// the following information, we will use the existing
		// information
		if (bookmark.getTitle() == null) {
			bookmark.setTitle(oldBookmark.getTitle());
		}
		if (bookmark.getNotes() == null) {
			bookmark.setNotes(oldBookmark.getNotes());
		}

		// NOTE: ensure some information not to be changed.
		bookmark.setCreatedOn(oldBookmark.getCreatedOn());
		bookmark.setUser(oldBookmark.getUser());

		// if bookmark.getLastUpdate() == null or it's not after
		// bookmark.getCreatedOn(), then set the date/time to NOW
		Date lupDate = bookmark.getLastUpdated();
		if (lupDate == null || !lupDate.after(bookmark.getCreatedOn())) {
			bookmark.setLastUpdated(GnizrDaoUtil.getNow());
		}

		// check if the link URL defined in the new bookmark
		// is any different from the existing one. if it's a
		// new URL, then create a new link record for, if necessary.
		Link link = bookmark.getLink();
		String urlHash = Link.computeUrlHash(link.getUrl());
		if (urlHash.equals(oldBookmark.getLink().getUrlHash()) == false) {
			Link ln = GnizrDaoUtil.getLink(linkDao, link.getUrl());
			if (ln != null) {
				// if the link already exists, set the new bookmark
				// to reference it.
				bookmark.setLink(ln);
			} else {
				// otherwise, create a new link record
				int lnId = createNewLinkFromBookmark(bookmark);
				if (lnId > 0) {
					link.setId(lnId);
				}
			}
		} else {
			// Otherwise, set this bookmark to reference
			// the link record that's in the database
			bookmark.setLink(oldBookmark.getLink());
		}

		boolean upOkay = bookmarkDao.updateBookmark(bookmark);

		List<Tag> tagObjs = getTags(oldBookmark.getTagList());
		tagDao.subtractTagCountOne(tagObjs.toArray(new Tag[0]), oldBookmark
				.getUser(), oldBookmark.getLink(), oldBookmark);

		tagObjs = initTagEntries(bookmark, bookmark.getTagList());
		tagDao.addTagCountOne(tagObjs.toArray(new Tag[0]), bookmark.getUser(),
				bookmark.getLink(), bookmark);

		try {
			bookmark.setTags(upBookmark.getTags());
			listenerExecutor.execute(new RunBookmarkUpdatedNotify(oldBookmark,
					bookmark));
		} catch (Exception e) {
			logger
					.error("error occurend executing RunBookmarkUpdatedNotify",
							e);
		}
		return upOkay;
	}

	/**
	 * Returns a bookmark record in the database whose ID matches <code>bookmarkId</code>.
	 * 
	 * @param bookmarkId ID used to find an existing bookmark record in the database.
	 * 
	 * @return Returns the matched bookmark with instantiated properties. Returns <code>null</code> if
	 * no match is found. 
	 */
	public Bookmark getBookmark(int bookmarkId) {
		checkIsTerminited();
		Bookmark bm = bookmarkDao.getBookmark(bookmarkId);
		return bm;
	}

	/**
	 * Returns the <code>id</code> of a bookmark that already exists in the
	 * system. A positive <code>id</code> value is returned if the bookmark is
	 * saved by <code>user</code> and the referenced URL link equals to
	 * <code>url</code>. This method returns <code>-1</code> if no matching
	 * bookmark is found.
	 * 
	 * @param user
	 *            the user who saved the bookmark.
	 * @param url
	 *            the URL link that is referenced by the bookmark.
	 * @return the <code>id</code> of the bookmark record if it exists.
	 *         Otherwise, <code>-1</code> is returned.
	 * @throws NoSuchUserException
	 *             the input user doesn't exist in the system
	 */
	public int getBookmarkId(User user, String url) throws NoSuchUserException {
		logger.debug("getBookmarkId: user=" + user + ",url=" + url);

		checkIsTerminited();

		int bookmarkId = -1;
		GnizrDaoUtil.fillId(userDao, user);
		Link link = GnizrDaoUtil.getLink(linkDao, url);
		if (link != null) {
			Bookmark bm;
			try {
				bm = GnizrDaoUtil.getBookmark(bookmarkDao, user, link);
				if (bm != null && bm.getId() > 0) {
					bookmarkId = bm.getId();
				}
			} catch (MissingIdException e) {
				// no code.
			}
		}
		return bookmarkId;
	}

	/**
	 * Terminates threads that have been created for notifying bookmark change event listeners.  
	 *
	 */
	public void shutdown() {
		try {
			if (isTerminated.compareAndSet(false, true)) {
				listenerExecutor.shutdown();
				while (listenerExecutor.awaitTermination(10, TimeUnit.SECONDS) == false) {
					// wait 10 secs and check if all tasks have been completed.
				}
			} else {
				logger.debug("BookmarkManager is already shutdowned.");
			}
		} catch (InterruptedException e) {
			logger.debug(e);
		}
	}

	private class RunBookmarkUpdatedNotify implements Runnable {
		private Bookmark oldBookmark;

		private Bookmark newBookmark;

		public RunBookmarkUpdatedNotify(Bookmark oldBookmark,
				Bookmark newBookmark) {
			this.oldBookmark = oldBookmark;
			this.newBookmark = newBookmark;
		}

		public void run() {
			for (BookmarkListener aListener : listeners) {
				try {
					aListener.notifyUpdated(BookmarkManager.this, new Bookmark(
							oldBookmark), new Bookmark(newBookmark));
				} catch (Exception e) {
					logger.error("RunBookmarkUpdatedNotify.run()" + e);
				}
			}
		}
	}

	private class RunBookmarkAddedNotify implements Runnable {

		private Bookmark bookmarkAdded;

		public RunBookmarkAddedNotify(Bookmark bookmark) {
			this.bookmarkAdded = bookmark;
		}

		public void run() {
			for (BookmarkListener aListener : listeners) {
				try {
					aListener.notifyAdded(BookmarkManager.this, new Bookmark(
							bookmarkAdded));
				} catch (Exception e) {
					logger.error("RunBookmarkAddedNotify.run()" + e);
				}
			}
		}
	}

	private class RunBookmarkDeletedNotify implements Runnable {
		private Bookmark bookmarkDeleted;

		public RunBookmarkDeletedNotify(Bookmark bookmark) {
			this.bookmarkDeleted = bookmark;
		}

		public void run() {
			for (BookmarkListener aListener : listeners) {
				try {
					aListener.notifyDeleted(BookmarkManager.this, new Bookmark(
							bookmarkDeleted));
				} catch (Exception e) {
					logger.error("RunBookmarkDeletedNotify.run()" + e);
				}
			}
		}
	}

	/**
	 * Renames a given tag used by a user to one or more tags. When a tag is renamed, 
	 * all bookmarks owned by <code>user</code> and were previously tagged <code>oldTag</code> will be tagged 
	 * with <code>newTags</code>.
	 * @param user The owner user of those bookmarks that will be updated
	 * @param oldTag the old tag be renamed
	 * @param newTags one or more new tags
	 * @return Returns <code>true</code> if tag renaming is successful. Returns <code>false</code>, otherwise. 
	 */
	public boolean renameTag(User user, String oldTag, String[] newTags) {
		Tag oldTagObj = null;
		boolean isOkay = false;
		List<Tag> tags = tagDao.findTag(oldTag);
		if (tags.size() > 0) {
			oldTagObj = tags.get(0);
		}
		if (oldTagObj != null) {
			DaoResult<Bookmark> result = null;
			List<Bookmark> oldBookmarks = null;
			List<Bookmark> newBookmarks = null;
			result = bookmarkDao.pageBookmarks(user, oldTagObj, 0, oldTagObj
					.getCount());
			oldBookmarks = result.getResult();
			List<String> safeNewTags = getSafeTagsList(Arrays.asList(newTags));
			for (Bookmark bm : oldBookmarks) {
				initTagEntries(bm, safeNewTags);
			}
			List<Tag> tagsObjs = getTags(safeNewTags);
			newBookmarks = tagDao.expandTag(user, oldTagObj, tagsObjs.toArray(new Tag[0]));
			if (safeNewTags.contains(oldTag) == false) {
				newBookmarks = tagDao.reduceTag(user, new Tag[] { oldTagObj });
			}
			Map<Integer, Bookmark> newBookmarksMap = GnizrDaoUtil
					.getBookmarksMap(newBookmarks);
			for (Bookmark oldBookmark : oldBookmarks) {
				Bookmark newBookmark = newBookmarksMap.get(oldBookmark.getId());
				if (newBookmark != null) {
					try {
						listenerExecutor.execute(new RunBookmarkUpdatedNotify(oldBookmark, newBookmark));
					} catch (Exception e) {
						logger.error("Error notifying BookmarkListeners", e);
					}
				} else {
					logger
							.error("After renaming a tag, bookmark "
									+ oldBookmark.getId()
									+ " doesn't appear in the expected list of updated bookmarks");
				}
			}
			if(oldBookmarks.size() == newBookmarks.size()){
				isOkay = true;
			}
		}
		return isOkay;
	}

	/**
	 * Deletes a given tag used by a user.  When a tag is deleted, 
	 * all bookmarks owned by <code>user</code> and were previously tagged <code>tag</code> will no longer
	 * be associated with <code>tag</code>
	 * @param user The owner user of those bookmarks that will be updated
	 * @param tag a tag to delete 
	 * @return Returns <code>true</code> if tag deletion is successful. Returns <code>false</code>, otherwise. 
	 */
	public boolean deleteTag(User user, String tag) {
		boolean isOkay = false;
		Tag tagObj = GnizrDaoUtil.getTag(tagDao, tag);
		if (tagObj != null) {
			List<Bookmark> oldBookmarks = bookmarkDao.pageBookmarks(user,
					tagObj, 0, tagObj.getCount()).getResult();
			List<Bookmark> newBookmarks = tagDao.reduceTag(user,
					new Tag[] { tagObj });
			Map<Integer, Bookmark> newBookmarksMap = GnizrDaoUtil
					.getBookmarksMap(newBookmarks);
			for (Bookmark oldBookmark : oldBookmarks) {
				Bookmark newBookmark = newBookmarksMap.get(oldBookmark.getId());
				if (newBookmark != null) {
					try {
						listenerExecutor.execute(new RunBookmarkUpdatedNotify(
								oldBookmark, newBookmark));
					} catch (Exception e) {
						logger.error("Error notifying BookmarkListeners", e);
					}
				} else {
					logger
					.error("After deleting a tag, bookmark "
							+ oldBookmark.getId()
							+ " doesn't appear in the expected list of updated bookmarks");
				}
			}
			if(oldBookmarks.size() == newBookmarks.size()){
				isOkay = true;
			}
		}
		return isOkay;
	}

	/**
	 * Adds a new <code>PointMarker</code> to a given bookmark. 
	 * 
	 * @param bmark a bookmark that exists in the database
	 * @param ptMarker a pointermaker description
	 * @return Return <code>true</code> if the pointmaker is successfully added
	 * @throws NoSuchUserException
	 * @throws NoSuchLinkException
	 * @throws MissingIdException
	 * @throws NoSuchBookmarkException
	 */
	public PointMarker addPointMarker(Bookmark bmark, PointMarker ptMarker)
			throws NoSuchUserException, NoSuchLinkException,
			MissingIdException, NoSuchBookmarkException {
		if (ptMarker != null) {
			List<PointMarker> pm = new ArrayList<PointMarker>();
			pm.add(ptMarker);
			pm = addPointMarkers(bmark, pm);
			if (pm != null && pm.isEmpty() == false) {
				return pm.get(0);
			}
		}
		return null;
	}
	/**
	 * Adds multiple <code>PointMarker</code> to a given bookmark. 
	 * 
	 * @param bmark a bookmark that exists in the database
	 * @param ptMarker multiple pointermaker descriptions
	 * @return Return <code>true</code> if the pointmakers are successfully added
	 * @throws NoSuchUserException
	 * @throws NoSuchLinkException
	 * @throws MissingIdException
	 * @throws NoSuchBookmarkException
	 */
	public List<PointMarker> addPointMarkers(Bookmark bmark,
			List<PointMarker> ptMarkers) throws NoSuchUserException,
			NoSuchLinkException, MissingIdException, NoSuchBookmarkException {
		logger
				.debug("addPointMarkers: bm=" + bmark + ",ptMarkers="
						+ ptMarkers);
		GnizrDaoUtil.fillId(bookmarkDao, userDao, linkDao, bmark);
		if (ptMarkers == null) {
			throw new NullPointerException("Input PointMarker list is null");
		} else {
			for (PointMarker pm : ptMarkers) {
				if (pm.getId() > 0) {
					if (geomMarkerDao.updatePointMarker(pm) == false) {
						logger.error("error updating PointerMarker: " + pm);
					}
					if (geomMarkerDao.addPointMarker(bmark, pm) == false) {
						logger
								.error("error associating bookmark with PointMarker: "
										+ pm);
					}
				} else {
					int pmId = geomMarkerDao.createPointMarker(pm);
					if (pmId > 0) {
						pm.setId(pmId);
						if (geomMarkerDao.addPointMarker(bmark, pm) == false) {
							logger
									.error("error associating bookmark with PointerMarker: "
											+ pm);
						}
					} else {
						logger.error("error creating new PointerMarker: " + pm);
					}
				}
			}
		}
		return ptMarkers;
	}

	/**
	 * Removes multiple <code>PointMarker</code> from a given bookmark. 
	 * 
	 * @param bmark a bookmark that exists in the database
	 * @param ptMarker multiple pointermaker descriptions
	 * @return Return <code>true</code> if the pointmakers are successfully removed
	 * @throws NoSuchUserException
	 * @throws NoSuchLinkException
	 * @throws MissingIdException
	 * @throws NoSuchBookmarkException
	 */
	public List<PointMarker> removePointMarkers(Bookmark bmark,
			List<PointMarker> ptMarkers) throws NoSuchUserException,
			NoSuchLinkException, MissingIdException, NoSuchBookmarkException {
		logger.debug("removePointMarkers: bm=" + bmark + ",ptMarkers="
				+ ptMarkers);
		GnizrDaoUtil.fillId(bookmarkDao, userDao, linkDao, bmark);
		List<PointMarker> sccdRmd = new ArrayList<PointMarker>();
		if (ptMarkers == null) {
			throw new NullPointerException("the input PointMarker list is null");
		} else {
			for (PointMarker pm : ptMarkers) {
				if (geomMarkerDao.removePointMarker(bmark, pm) == true) {
					sccdRmd.add(new PointMarker(pm));
				} else {
					logger
							.error("error removing PointMarker from a bookmark: pm="
									+ pm);
				}
			}
		}
		return sccdRmd;
	}

	/**
	 * Returns all pointermarkers that are currently associated a given bookmark.
	 * 
	 * @param bmark a bookmark currently exists in the database.
	 * @return Returns a list of pointmarker that is currently associated with <code>bmark</code>
	 * 
	 * @throws NoSuchUserException
	 * @throws NoSuchLinkException
	 * @throws MissingIdException
	 * @throws NoSuchBookmarkException
	 */
	public List<PointMarker> getPointMarkers(Bookmark bmark)
			throws NoSuchUserException, NoSuchLinkException,
			MissingIdException, NoSuchBookmarkException {
		logger.debug("getPointMarkers: bm=" + bmark);
		GnizrDaoUtil.fillId(bookmarkDao, userDao, linkDao, bmark);
		return geomMarkerDao.listPointMarkers(bmark);
	}

	/**
	 * Returns a user's bookmarks that each of which has at least one assocaited pointmarker.
	 * 
	 * @param user an existing bookmark owner.
	 * @param offset the starting index of paging, starting at 0.
	 * @param count total number of bookmarks to page.
	 * @return Returns non-<code>null</code> list of bookmarks that have associated pointmarkers.
	 * @throws NoSuchUserException
	 */
	public DaoResult<Bookmark> pageBookmarkHasGeomMarker(User user, int offset,
			int count) throws NoSuchUserException {
		logger.debug("pageBookmarkHasGeomMarker: user=" + user + ",offset="
				+ offset + ",count=" + count);
		GnizrDaoUtil.fillId(userDao, user);
		return geomMarkerDao.pageBookmarksInArchive(user, offset, count);
	}

}
