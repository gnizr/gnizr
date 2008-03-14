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
package com.gnizr.db.dao.util;

import java.io.FileOutputStream;

import org.apache.commons.dbcp.BasicDataSource;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;

/**
 * Export DB into DBUnit XML datafile.
 * See also: http://www.dbunit.org/faq.html#extract
 * @author harryc
 * 
 */
public class MySQLDBExport {
	
	public static void main(String[] args) throws Exception
    {	
		BasicDataSource dataSource = new BasicDataSource();
		dataSource.setUsername("gnizr");
		dataSource.setPassword("gnizr");
		dataSource.setUrl("jdbc:mysql://localhost/gnizr_db");
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		
		IDatabaseConnection dc = new DatabaseConnection(dataSource.getConnection());
		DatabaseConfig config = dc.getConfig();
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
				new MySqlDataTypeFactory());

        // full database export
        IDataSet fullDataSet = dc.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream("full.xml"));             
    }
}
