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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.gnizr.core.bookmark.BookmarkManager;
import com.gnizr.core.exceptions.MissingIdException;
import com.gnizr.core.exceptions.NoSuchBookmarkException;
import com.gnizr.core.exceptions.NoSuchLinkException;
import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.feed.FeedSubscriptionManager;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.TagUtil;
import com.gnizr.core.vocab.MachineTags;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.UserTag;
import com.gnizr.web.action.AbstractLoggedInUserAction;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class EditSubscription extends AbstractLoggedInUserAction {

	public static final String NO_SUCH_FEED = "NO_SUCH_FEED";

	public static final String NOT_VALID_FEED_URL = "NOT_VALID_FEED_URL";

	public static final String ILLEGAL_FOLDER_NAME = "ILLEGAL_FOLDER_NAME";

	/**
	 * 
	 */
	private static final long serialVersionUID = -6164827055389541381L;

	private static final Logger logger = Logger
			.getLogger(EditSubscription.class);

	// action parameters
	private String feedUrl;

	private String feedTitle;

	private boolean autoImport;

	private String[] importFolders;

	private String tag;

	// read-only objects
	private FeedSubscription feed;

	// folders that this.user has access to
	private List<String> folders = new ArrayList<String>();

	private List<Folder> currentImportFolders = new ArrayList<Folder>();

	private List<String> currentSelectedTags = new ArrayList<String>();

	private List<UserTag> userTags;

	private FeedSubscriptionManager feedSubscriptionManager;

	private FolderManager folderManager;

	private BookmarkManager bookmarkManager;

	public BookmarkManager getBookmarkManager() {
		return bookmarkManager;
	}

	public void setBookmarkManager(BookmarkManager bookmarkManager) {
		this.bookmarkManager = bookmarkManager;
	}

	public String doDefault() throws Exception {
		resolveUser();
		if (feedUrl != null && user != null) {
			try {
				feed = feedSubscriptionManager.getSubscription(user, feedUrl);
				int totalNum = folderManager.getUserFolderCount(user);
				if (totalNum > 0) {
					if (totalNum == 1) {
						folders.add("");
					}
					DaoResult<Folder> result = folderManager.pageUserFolders(
							user, 0, totalNum);
					for (Folder f : result.getResult()) {
						if (f.getName()
								.equals(FolderManager.MY_BOOKMARKS_LABEL) == false) {
							folders.add(f.getName());
						}
					}
				}
				currentImportFolders = feedSubscriptionManager
						.listImportFolder(user, feedUrl);
				currentSelectedTags = getMachineTagGNTag(feed.getBookmark()
						.getMachineTagList());
				userTags = getUserManager().getTagsSortByAlpha(getUser(), 1);
			} catch (Exception e) {
				addActionError(e.toString());
				return ERROR;
			}
		}
		return SUCCESS;
	}

	private List<String> getMachineTagGNTag(List<MachineTag> mtags) {
		List<String> tags = new ArrayList<String>();
		if (mtags != null && mtags.isEmpty() == false) {
			for (MachineTag mt : mtags) {
				if (mt.getPredicate().equals(MachineTags.TAG_PRED)
						&& (mt.getNsPrefix() == null || mt.getNsPrefix()
								.equals(MachineTags.NS_GNIZR))) {
					tags.add(mt.getValue());
				}
			}
		}
		return tags;
	}

	public String doRemoveImportFolders() throws Exception {
		resolveUser();
		if (feedUrl != null) {
			if (importFolders != null && importFolders.length > 0) {
				List<String> safeFolders = new ArrayList<String>();
				try {
					safeFolders = getSafeImportFolders();
				} catch (IllegalFolderNameException e) {
					addActionMessage(ILLEGAL_FOLDER_NAME);
					return INPUT;
				}
				try {
					feedSubscriptionManager.removeImportFolders(user, feedUrl,
							user, safeFolders);
					doDefault();
					return INPUT;
				} catch (Exception e) {
					addActionError(e.toString());
					doDefault();
					return ERROR;
				}
			}
		}
		doDefault();
		return SUCCESS;
	}
	
	public String doRemoveTag() throws Exception{
		resolveUser();
		if (feedUrl != null && tag != null) {			
			String safeTagStr = TagUtil.makeSafeTagString(tag);
			if (safeTagStr.length() > 0) {
				MachineTag mt = MachineTags.GN_TAG(safeTagStr);
				String fullSynx = mt.toString();
				String shrtSynx = mt.getPredicate()+":"+mt.getValue();
				FeedSubscription feed = feedSubscriptionManager.getSubscription(getUser(),feedUrl);
				if(feed != null){
					String tagline = feed.getBookmark().getTags();
					if(tagline.contains(fullSynx) == true){
						tagline = tagline.replace(fullSynx," ");
					}else if(tagline.contains(shrtSynx) == true){
						tagline = tagline.replace(shrtSynx, " ");
					}
					Bookmark bm = feed.getBookmark();
					bm.setTags(tagline);
					bookmarkManager.updateBookmark(bm);
				}
				doDefault();
				return INPUT;
			}
		}
		doDefault();
		return SUCCESS;
	}

	public String doDeleteSubscription() throws Exception {
		resolveUser();
		logger.debug("EditSubscription.doDeleteSubscription: user=" + user
				+ ",feedUrl=" + feedUrl);
		if (feedUrl != null) {
			try {
				feedSubscriptionManager.deleteSubscription(user, feedUrl);
			} catch (Exception e) {
				addActionError(e.toString());
				return ERROR;
			}
		}
		return SUCCESS;
	}

	public String doUpdateSubscription() throws Exception {
		resolveUser();
		if (feedUrl != null) {
			List<String> safeFolders = new ArrayList<String>();
			feed = feedSubscriptionManager.getSubscription(user, feedUrl);
			if (feed != null) {
				try {
					feed.setAutoImport(autoImport);
					updateBookmarkMachineTags(feed.getBookmark());
					feedSubscriptionManager.updateSubscription(feed);
					safeFolders = getSafeImportFolders();
					feedSubscriptionManager.addImportFolders(user, feedUrl,
							null, user, safeFolders);
				} catch (IllegalFolderNameException e) {
					addActionMessage(ILLEGAL_FOLDER_NAME);
				} catch (Exception e){
					logger.error(e);
				}
			} else {
				addActionMessage(NO_SUCH_FEED);
			}
			doDefault();
			return INPUT;
		}
		return SUCCESS;
	}

	private void updateBookmarkMachineTags(Bookmark bm)
			throws MissingIdException, NoSuchUserException,
			NoSuchLinkException, NoSuchBookmarkException {
		if (tag != null) {
			String safeTagStr = TagUtil.makeSafeTagString(tag);
			if (safeTagStr.length() > 0) {
				MachineTag newTag = MachineTags.GN_TAG(safeTagStr);
				List<MachineTag> mtags = bm.getMachineTagList();
				for (MachineTag mt : mtags) {
					if (mt.equals(newTag) == true) {
						return;
					}
				}
				if (bm.getTags() != null) {
					bm.setTags(bm.getTags() + " " + newTag.toString());
				} else {
					bm.setTags(newTag.toString());
				}
				bookmarkManager.updateBookmark(bm);
			}
		}
	}

	private List<String> getSafeImportFolders()
			throws IllegalFolderNameException {
		List<String> safeFolders = new ArrayList<String>();
		if (importFolders != null) {
			for (String fname : importFolders) {
				fname = fname.trim();
				if (fname.length() > 0) {
					if (GnizrDaoUtil.isLegalFolderName(fname)) {
						if (safeFolders.contains(fname) == false) {
							safeFolders.add(fname);
						}
					} else {
						throw new IllegalFolderNameException(fname);
					}
				}
			}
		}
		return safeFolders;
	}

	public FeedSubscription getFeed() {
		return feed;
	}

	public FeedSubscriptionManager getFeedSubscriptionManager() {
		return feedSubscriptionManager;
	}

	public String getFeedTitle() {
		return feedTitle;
	}

	public String getFeedUrl() {
		return feedUrl;
	}

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public List<String> getFolders() {
		return folders;
	}

	public String[] getImportFolders() {
		return importFolders;
	}

	public boolean isAutoImport() {
		return autoImport;
	}

	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
	}

	public void setFeedSubscriptionManager(
			FeedSubscriptionManager feedSubscriptionManager) {
		this.feedSubscriptionManager = feedSubscriptionManager;
	}

	public void setFeedTitle(String feedTitle) {
		this.feedTitle = feedTitle;
	}

	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	public void setImportFolders(String[] importFolders) {
		this.importFolders = importFolders;
	}

	@Override
	protected String go() throws Exception {
		resolveUser();
		logger.debug("EditSubscription.go(): user=" + user + ",feedUrl="
				+ feedUrl + ",feedTitle=" + feedTitle);
		if (feedUrl != null && user != null) {
			// if a feed that we asked to create already exists,
			// fetch it from the database and return SUCCESS
			try {
				FeedSubscription existFeed = feedSubscriptionManager
						.getSubscription(user, feedUrl);
				if (existFeed != null) {
					feed = existFeed;
					return SUCCESS;
				}
			} catch (Exception e) {
				// safely ignore
			}
			// if a feed that we asked to create doesn't exists in the db,
			// will try to create it for the first time
			if (feedTitle == null) {
				feedTitle = guessFeedTitle(feedUrl);
				if (feedTitle == null) {
					addActionMessage(NOT_VALID_FEED_URL);
					doDefault();
					return INPUT;
				}
			}
			try {
				feed = feedSubscriptionManager.createSubscription(user,
						feedUrl, feedTitle);
				feedUrl = GnizrDaoUtil.encodeURI(feedUrl);
			} catch (Exception e) {
				addActionError(e.toString());
				return ERROR;
			}
		}
		return SUCCESS;
	}

	private String guessFeedTitle(String url) {
		String title = null;
		ExecutorService service = null;
		try {
			FetchRssDataWorker worker = new FetchRssDataWorker(url);
			service = Executors.newFixedThreadPool(1);
			service.execute(worker);
			service.shutdown();
			service.awaitTermination(5, TimeUnit.SECONDS);
			title = worker.getRssTitle();
			logger.debug("detected RSS title: " + title);
		} catch (Exception e) {
			logger.debug(e);
		} finally {
			if (service != null) {
				try {
					service.shutdown();
				} catch (Exception e) {
					logger.debug(e);
				}
			}
		}
		return title;
	}

	private class FetchRssDataWorker implements Runnable {
		private String rssTitle;

		private String feedUrl;

		public FetchRssDataWorker(String feedUrl) {
			this.feedUrl = feedUrl;
			this.rssTitle = null;
		}

		public String getRssTitle() {
			return this.rssTitle;
		}

		public void run() {
			try {
				SyndFeed feed = null;
				SyndFeedInput input = new SyndFeedInput();
				feed = input.build(new XmlReader(new URL(feedUrl)));
				rssTitle = feed.getTitle();
			} catch (Exception e) {
				logger.debug(e);
			}

		}

	}

	public List<Folder> getCurrentImportFolders() {
		return currentImportFolders;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return true;
	}

	public List<UserTag> getUserTags() {
		return userTags;
	}

	public List<String> getMyTags() {
		if (userTags != null) {
			List<String> tagStr = new ArrayList<String>();
			for (UserTag ut : userTags) {
				String label = ut.getLabel();
				if(label.matches("gn:tag=.*|tag:.*") == false){
					tagStr.add(ut.getLabel());
				}
			}
			return tagStr;
		}
		return new ArrayList<String>();
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public List<String> getCurrentSelectedTags() {
		return currentSelectedTags;
	}

}
