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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Utility class that implements the proper way to close JDBC connection.  
 *  
 * @author Harry Chen
 * @since 2.2
 *
 */
public class DBUtil {

	/**
	 * Closes the input <code>conn</code> if it's not already closed.
	 * @param conn an instantiated JDBC connection
	 * @throws SQLException an exception is thrown if unable to
	 * close the connection
	 */
	public static final void cleanup(Connection conn) throws SQLException {
		if (conn != null && conn.isClosed() == false) {
			conn.close();
		}
	}

	/**
	 * Closes the connection used by the input JDBC <code>Statement</code>
	 * object. 
	 * @param stmt an instantiated object
	 * @throws SQLException an exception is thrown if unable to 
	 * close the connection
	 */
	public static final void cleanup(Statement stmt) throws SQLException {
		if (stmt != null) {
			stmt.close();
		}
	}

	/**
	 * Close the connection used by the input <code>Connection</code> and
	 * the connection used by the input <code>Statement</code>. This
	 * method doesn't require the <code>stmt</code> to use the same connection
	 * as the input <code>conn</code>.
	 * 
	 * @param conn an instantiated JDBC connection
	 * @param stmt an instantiated <code>Statement</code> object
	 * @throws SQLException
	 */
	public static final void cleanup(Connection conn, Statement stmt)
			throws SQLException {
		cleanup(conn);
		cleanup(stmt);
	}
}
