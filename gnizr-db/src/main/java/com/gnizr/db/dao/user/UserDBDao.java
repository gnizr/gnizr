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
package com.gnizr.db.dao.user;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.DBUtil;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserStat;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.tag.TagDBDao;
import com.gnizr.db.vocab.UserSchema;

public class UserDBDao implements UserDao{
	
	private static final long serialVersionUID = 1205142167824260031L;
	
	private static final Logger logger = Logger.getLogger(UserDBDao.class.getName());
	
	private static final String MEMBER_TAG_COL = "mem_tag";
	private static final String GROUP_TAG_COL = "grp_tag";
	private static final String MEMBER_USER_TAG_IDX_COL = "mem_tag_uti";
		
	private DataSource datasource;
	
	public UserDBDao(DataSource cm){
		logger.debug("created UserDBDao, datasource: " + cm.toString());
		this.datasource = cm;
	}
	
	public static User createUserObject(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		User aUser = new User();
		aUser = new User();
		aUser.setId(rs.getInt(UserSchema.ID));
		aUser.setFullname(rs.getString(UserSchema.FULLNAME));
		aUser.setUsername(rs.getString(UserSchema.USERNAME));
		aUser.setPassword(rs.getString(UserSchema.PASSWORD));
		aUser.setCreatedOn(rs.getTimestamp(UserSchema.CREATED_ON));
		aUser.setAccountStatus(rs.getInt(UserSchema.ACCT_STATUS));
		aUser.setEmail(rs.getString(UserSchema.EMAIL));
		return aUser;
	}
	
	public static User createUserObject(String tableAlias, ResultSet rs) throws SQLException{
		return createNamedUserObject(tableAlias, rs, false);
	}
	
	public static User createNamedUserObject(String tableAlias, ResultSet rs, boolean noColumnRef) throws SQLException {
		if(rs == null) return null;
		
		String idCol = tableAlias+UserSchema.ID_COL;
		String fullnameCol = tableAlias+UserSchema.FULLNAME_COL;
		String usernameCol = tableAlias+UserSchema.USERNAME_COL;
		String passwordCol = tableAlias+UserSchema.PASSWORD_COL;
		String createdOnCol = tableAlias+UserSchema.CREATED_ON_COL;
		String acctStatusCol = tableAlias+UserSchema.ACCT_STATUS_COL;
		String emailStatusCol = tableAlias+UserSchema.EMAIL_COL;
		
		if(noColumnRef == true){
			idCol = idCol.replaceAll("\\.", "_");
			fullnameCol = fullnameCol.replaceAll("\\.", "_");
			usernameCol = usernameCol.replaceAll("\\.", "_");
			passwordCol = passwordCol.replaceAll("\\.", "_");
			createdOnCol = createdOnCol.replaceAll("\\.", "_");
			acctStatusCol = acctStatusCol.replaceAll("\\.", "_");
			emailStatusCol = emailStatusCol.replaceAll("\\.", "_");
		}
		
		User aUser = new User();
		aUser.setId(rs.getInt(idCol));
		aUser.setFullname(rs.getString(fullnameCol));
		aUser.setUsername(rs.getString(usernameCol));
		aUser.setPassword(rs.getString(passwordCol));
		aUser.setCreatedOn(rs.getTimestamp(createdOnCol));
		aUser.setAccountStatus(rs.getInt(acctStatusCol));
		aUser.setEmail(rs.getString(emailStatusCol));
		return aUser;
	}
	
	
	public static UserStat createUserStatObject(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		User user = createUserObject(rs);
		UserStat userStat = new UserStat(user);
		userStat.setNumOfBookmarks(rs.getInt(UserSchema.NUM_OF_BOOKMARKS));
		userStat.setNumOfTags(rs.getInt(UserSchema.NUM_OF_TAGS));
		return userStat;
	}
	
	public List<User> findUser(String username){
		logger.debug("input: username=" + username);
		List<User> users = new ArrayList<User>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call findUserUsername(?)");
			stmt.setString(1,username);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				User aUser = createUserObject(rs);
				users.add(aUser);
				logger.debug("found: " + aUser);
			}
			if(users.size() == 0){
				logger.debug("found no matching users");
			}
		}catch(SQLException e){		
			logger.error(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return users;
	}
	
	public User getUser(int id) {
		logger.debug("input: id="+id);
		User aUser = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call getUser(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				aUser = createUserObject(rs);
				logger.debug("found: " + aUser);
			}else{
				logger.debug("found no matching users");
			}
		}catch(Exception e){		
			logger.error(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return aUser;
	}

	public int createUser(User user) {
		logger.debug("input: user=" + user);
		CallableStatement cStmt = null;
		Connection conn = null;
		int userId = -1;
		try{
			conn = datasource.getConnection();
			cStmt = conn.prepareCall("{call createUser(?,?,?,?,?,?,?)}");
			cStmt.setString(1,user.getUsername());
			cStmt.setString(2,user.getPassword());
			cStmt.setString(3,user.getFullname());
			cStmt.setString(4,user.getEmail());
			cStmt.setTimestamp(5,new Timestamp(user.getCreatedOn().getTime()));
			cStmt.setInt(6,user.getAccountStatus());
			cStmt.registerOutParameter(7,Types.INTEGER);
			cStmt.execute();
			userId = cStmt.getInt(7);
		}catch(Exception e){
			logger.error(e);
		}finally{
			try {			
				DBUtil.cleanup(conn, cStmt);			
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return userId;
	}
		
	public boolean updateUser(User user) {
		logger.debug("input: user="+user);
		Connection conn = null;
		PreparedStatement stmt = null;
		Boolean isChanged = false;
		try {
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call updateUser(?,?,?,?,?,?,?)");
			stmt.setInt(1,user.getId());
			stmt.setString(2,user.getUsername());
			stmt.setString(3,user.getPassword());
			stmt.setString(4,user.getFullname());
			stmt.setString(5,user.getEmail());
			stmt.setTimestamp(6,new Timestamp(user.getCreatedOn().getTime()));
			stmt.setInt(7,user.getAccountStatus());
			stmt.execute();
			if(stmt.getUpdateCount()>0){
				logger.debug("updateCount="+stmt.getUpdateCount());
				isChanged = true;
			}
			stmt.getResultSet();
		} catch (SQLException e) {
			logger.error(e);
		} finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}		
		return isChanged;
	}

	public List<User> findUser(String username, String password) {
		logger.debug("input: username="+username+", password="+password);
		Connection conn = null;
		PreparedStatement stmt = null;
		List<User> users = new ArrayList<User>();
		try {
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call findUserUnamePwd(?,?)");
			stmt.setString(1,username);
			stmt.setString(2,password);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				User u = createUserObject(rs);
				logger.debug("found user="+ u);
				users.add(u);
			}
		} catch (Exception e) {			
			logger.error(e);
		}finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return users;
	}

	public boolean deleteUser(int id) {
		logger.debug("input: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		Boolean deleted = false;
		try {
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call deleteUser(?)");
			stmt.setInt(1,id);
			if(stmt.executeUpdate() > 0){
				logger.debug("# row deleted=" + stmt.getUpdateCount());
				deleted = true;
			}
		} catch (SQLException e) {
			logger.error(e);
		} finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return deleted;
	}

	public List<User> listUsers() {
		logger.debug("listUsers");
		List<User> users = new ArrayList<User>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call findAllUsers()");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				User aUser = createUserObject(rs);
				users.add(aUser);
				logger.debug("found: " + aUser);
			}
			if(users.size() == 0){
				logger.debug("found no matching users");
			}
		}catch(SQLException e){		
			logger.error(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return users;
	}

	public List<UserStat> listUserStats() {
		logger.debug("listUsers");
		List<UserStat> users = new ArrayList<UserStat>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call listUserStats();");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				UserStat aUser = createUserStatObject(rs);
				users.add(aUser);
				logger.debug("found: " + aUser);
			}
			if(users.size() == 0){
				logger.debug("found no matching user stat");
			}
		}catch(SQLException e){		
			logger.error(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return users;
	}

	public Map<String, List<UserTag>> listTagGroups(User user, int minFreq, int sortBy, int order) {
		logger.debug("listTagGroup: user="+user+",minFreq="+minFreq+",sortBy="+sortBy + ",order="+order);
		Map<String,List<UserTag>> tagGroups = new HashMap<String, List<UserTag>>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = datasource.getConnection();
			stmt = conn.prepareStatement("call findTagGroupsOfUser(?,?,?,?);");
			stmt.setInt(1,user.getId());
			stmt.setInt(2,minFreq);
			stmt.setInt(3,sortBy);
			stmt.setInt(4,order);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){				
				Tag grpTag = TagDBDao.createNamedTagObject(GROUP_TAG_COL, rs, true);
				UserTag memUserTag = TagDBDao.createNamedUserTagObject(MEMBER_USER_TAG_IDX_COL,
						MEMBER_TAG_COL,UserSchema.TABLE_NAME, rs, true);
				List<UserTag> memTags = tagGroups.get(grpTag.getLabel());
				if(memTags == null){
					memTags = new ArrayList<UserTag>();
					tagGroups.put(grpTag.getLabel(),memTags);
				}
				memTags.add(memUserTag);
			}
		}catch(SQLException e){		
			logger.error(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return tagGroups;
	}
}
