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
package com.gnizr.core.web.junit;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.bookmark.BookmarkDBDao;
import com.gnizr.db.dao.bookmark.GeometryMarkerDBDao;
import com.gnizr.db.dao.folder.FolderDBDao;
import com.gnizr.db.dao.foruser.ForUserDBDao;
import com.gnizr.db.dao.link.LinkDBDao;
import com.gnizr.db.dao.subscription.FeedSubscriptionDBDao;
import com.gnizr.db.dao.tag.TagAssertionDBDao;
import com.gnizr.db.dao.tag.TagDBDao;
import com.gnizr.db.dao.tag.TagPropertyDBDao;
import com.gnizr.db.dao.user.UserDBDao;

public abstract class GnizrWebappTestBase extends DatabaseTestCase{

	public GnizrWebappTestBase() {
		super();
		init();
	}

	public GnizrWebappTestBase(String name) {
		super(name);
		init();
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}


	private GnizrDao gnizrDBDao;
	
	private void init() {
		if (dataSource == null) {
			dataSource = new BasicDataSource();
			dataSource.setUsername("gnizr");
			dataSource.setPassword("gnizr");
			dataSource.setUrl("jdbc:mysql://localhost/gnizr_test");
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		}
		
		gnizrDBDao = new GnizrDao();
		gnizrDBDao.setBookmarkDao(new BookmarkDBDao(dataSource));
		gnizrDBDao.setUserDao(new UserDBDao(dataSource));
		gnizrDBDao.setLinkDao(new LinkDBDao(dataSource));
		gnizrDBDao.setTagDao(new TagDBDao(dataSource));
		gnizrDBDao.setTagPropertyDao(new TagPropertyDBDao(dataSource));
		gnizrDBDao.setTagAssertionDao(new TagAssertionDBDao(dataSource));
		gnizrDBDao.setFeedSubscriptionDao(new FeedSubscriptionDBDao(dataSource));
		gnizrDBDao.setForUserDao(new ForUserDBDao(dataSource));
		gnizrDBDao.setFolderDao(new FolderDBDao(dataSource));
		gnizrDBDao.setGeometryMarkerDao(new GeometryMarkerDBDao(dataSource));
	}

	private BasicDataSource dataSource;
	
	@Override
	protected IDatabaseConnection getConnection() throws Exception {
		return new DatabaseConnection(dataSource.getConnection());
	}

	public BasicDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public GnizrDao getGnizrDao(){
		return gnizrDBDao;
	}

}
