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
package com.gnizr.core;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;

import com.gnizr.db.dao.GnizrDao;

public abstract class GnizrCoreTestBase extends DatabaseTestCase{

	public GnizrCoreTestBase() {
		super();
		init();
	}

	public GnizrCoreTestBase(String name) {
		super(name);
		init();
	}

	private GnizrDao gnizrDBDao;
	
	private void init() {
		if (dataSource == null) {
			dataSource = new BasicDataSource();
			dataSource.setUsername("gnizr");
			dataSource.setPassword("gnizr");
			dataSource.setUrl("jdbc:mysql://localhost/gnizr_test");
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.addConnectionProperty("characterEncoding", "UTF-8");
			dataSource.addConnectionProperty("useUnicode", "TRUE");	
		}
		
		gnizrDBDao = GnizrDao.getInstance(dataSource);
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
