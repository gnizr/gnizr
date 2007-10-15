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
package com.gnizr.db.dao;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.DatabaseTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

public abstract class GnizrDBTestBase extends DatabaseTestCase {

	public GnizrDBTestBase() {
		super();
		init();
	}

	public GnizrDBTestBase(String name) {
		super(name);
		init();
	}

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
	}

	private BasicDataSource dataSource;
	
	@Override
	protected IDatabaseConnection getConnection() throws Exception {
		IDatabaseConnection dc = new DatabaseConnection(dataSource.getConnection());
		DatabaseConfig config = dc.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());
		return dc;
	}
	

	public BasicDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(BasicDataSource dataSource) {
		this.dataSource = dataSource;
	}

}