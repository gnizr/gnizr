/**
 * <p>Classes that define the vocabularies of the underlying database schemas. This includes 
 * table names, table column names and column aliases.</p>
 * <p>Use these classes to access SQL query result values when implementing DAO objects.</p> 
 * <h5>Sample code</h5>
 * <pre>
 * 	public static User createUserObject(ResultSet rs) throws SQLException{
 *		if(rs == null) return null;
 *		User aUser = new User();
 *		aUser = new User();
 *		aUser.setId(rs.getInt(UserSchema.ID));
 *		aUser.setFullname(rs.getString(UserSchema.FULLNAME));
 *		aUser.setUsername(rs.getString(UserSchema.USERNAME));
 *		aUser.setPassword(rs.getString(UserSchema.PASSWORD));
 * 		aUser.setCreatedOn(rs.getTimestamp(UserSchema.CREATED_ON));
 *		aUser.setAccountStatus(rs.getInt(UserSchema.ACCT_STATUS));
 *		aUser.setEmail(rs.getString(UserSchema.EMAIL));
 *		return aUser;
 *	}
 * </pre>
 */
package com.gnizr.db.vocab;