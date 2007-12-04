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
package com.gnizr.db.dao.tag;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.DBUtil;
import com.gnizr.db.dao.TagAssertion;
import com.gnizr.db.dao.TagProperty;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.user.UserDBDao;
import com.gnizr.db.vocab.TagAssertionSchema;

public class TagAssertionDBDao implements TagAssertionDao{

	private static final Logger logger = Logger.getLogger(TagAssertionDBDao.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static TagAssertion createTagAssertionObject(ResultSet rs) throws SQLException {
		if(rs == null) return null;
		TagAssertion assertion = new TagAssertion();
		assertion.setId(rs.getInt(TagAssertionSchema.ID));			
		UserTag subjectUserTag = TagDBDao.createNamedUserTagObject("s_user_tag", "s_tag", "s_user", rs);
		UserTag objectUserTag = TagDBDao.createNamedUserTagObject("o_user_tag", "o_tag", "o_user", rs);
		TagProperty prpt = TagPropertyDBDao.createTagPropertyObject(rs);
		User user = UserDBDao.createUserObject("tag_assertion_user", rs);		
		assertion.setSubject(subjectUserTag);
		assertion.setProperty(prpt);
		assertion.setObject(objectUserTag);
		assertion.setUser(user);		
		return assertion;
	}
	
	private DataSource dataSource;
	
	public TagAssertionDBDao(DataSource ds){
		logger.debug("created TagAssertionDBDao. dataSource="+ds);
		this.dataSource = ds;
	}

	public int createTagAssertion(TagAssertion asrt) {
		logger.debug("input: tagPrptStmt="+asrt);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createTagAssertion(?,?,?,?,?)}");
			cStmt.setLong(1,asrt.getSubject().getId());
			cStmt.setLong(2,asrt.getProperty().getId());
			cStmt.setLong(3,asrt.getObject().getId());
			cStmt.setLong(4,asrt.getUser().getId());
			cStmt.registerOutParameter(5,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(5);
		} catch (Exception e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return id;
	}

	public boolean deleteTagAssertion(int id) {
		logger.debug("input: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteTagAssertion(?)");
			stmt.setInt(1,id);
			if(stmt.executeUpdate() > 0){
				logger.debug("# row deleted=" + stmt.getUpdateCount());
				deleted = true;
			}
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return deleted;
	}

	public List<TagAssertion> findTagAssertion(User user) {
		logger.debug("input: user=" + user);
		List<TagAssertion> assertions = new ArrayList<TagAssertion>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTagAssertionUserId(?)");
			stmt.setLong(1,user.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				TagAssertion asrt = createTagAssertionObject(rs);
				assertions.add(asrt);
				logger.debug("found: " + asrt);
			}
			if(assertions.size() == 0){
				logger.debug("found no matching tag assertion");
			}
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return assertions;
	}

	private List<TagAssertion> findTagAssertion(User user, TagProperty tagPrpt, UserTag objectTag) {
		logger.debug("input: user="+user+",tagPrpt="+tagPrpt+",objectTag="+objectTag);
		List<TagAssertion> assertions = new ArrayList<TagAssertion>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTagAssertionUserPrptObjectId(?,?,?)");
			stmt.setLong(1,user.getId());			
			stmt.setLong(2,tagPrpt.getId());
			stmt.setLong(3,objectTag.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				TagAssertion asrt = createTagAssertionObject(rs);
				assertions.add(asrt);
				logger.debug("found: " + asrt);
			}
			if(assertions.size() == 0){
				logger.debug("found no matching tag assertion");
			}
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return assertions;
	}

	private List<TagAssertion> findTagAssertion(User user, UserTag subjectTag, TagProperty tagPrpt) {
		logger.debug("input: user="+user+",subjectTag="+subjectTag+",prpt="+tagPrpt);
		List<TagAssertion> assertions = new ArrayList<TagAssertion>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTagAssertionUserSubjectPrptId(?,?,?)");
			stmt.setLong(1,user.getId());
			stmt.setLong(2,subjectTag.getId());
			stmt.setLong(3,tagPrpt.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				TagAssertion asrt = createTagAssertionObject(rs);
				assertions.add(asrt);
				logger.debug("found: " + asrt);
			}
			if(assertions.size() == 0){
				logger.debug("found no matching tag assertion");
			}
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return assertions;
	}

	private List<TagAssertion> findTagAssertion3(User user, UserTag subjectTag, TagProperty tagPrpt, UserTag objectTag) {
		logger.debug("input: user="+user+",subjectTag="+subjectTag+",prpt="+tagPrpt+",objectTag="+objectTag);
		List<TagAssertion> assertions = new ArrayList<TagAssertion>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTagAssertion(?,?,?,?)");
			stmt.setLong(1,user.getId());
			stmt.setLong(2,subjectTag.getId());
			stmt.setLong(3,tagPrpt.getId());
			stmt.setLong(4,objectTag.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				TagAssertion asrt = createTagAssertionObject(rs);
				assertions.add(asrt);
				logger.debug("found: " + asrt);
			}
			if(assertions.size() == 0){
				logger.debug("found no matching tag assertion");
			}
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return assertions;
	}

	private List<TagAssertion> findTagAssertionObject(User user, UserTag objectTag) {
		logger.debug("input: user=" + user + ",objectTag="+objectTag);
		List<TagAssertion> assertions = new ArrayList<TagAssertion>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTagAssertionUserObjectId(?,?)");
			stmt.setLong(1,user.getId());
			stmt.setLong(2,objectTag.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				TagAssertion asrt = createTagAssertionObject(rs);
				assertions.add(asrt);
				logger.debug("found: " + asrt);
			}
			if(assertions.size() == 0){
				logger.debug("found no matching tag assertion");
			}
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return assertions;
	}

	private List<TagAssertion> findTagAssertionSubject(User user, UserTag subjectTag) {
		logger.debug("input: user=" + user + ",subjectTag="+subjectTag);
		List<TagAssertion> assertions = new ArrayList<TagAssertion>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTagAssertionUserSubjectId(?,?)");
			stmt.setLong(1,user.getId());
			stmt.setLong(2,subjectTag.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				TagAssertion asrt = createTagAssertionObject(rs);
				assertions.add(asrt);
				logger.debug("found: " + asrt);
			}
			if(assertions.size() == 0){
				logger.debug("found no matching tag assertion");
			}
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return assertions;
	}

	public TagAssertion getTagAssertion(int id) {
		logger.debug("input: id="+id);
		TagAssertion asrt = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getTagAssertion(?);");
			stmt.setLong(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				asrt = createTagAssertionObject(rs);
				logger.debug("found: " + asrt);
			}else{
				logger.debug("found no matching tag assertion");
			}
		}catch(Exception e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return asrt;
	}

	public boolean updateTagAssertion(TagAssertion asrt) {
		logger.debug("input: tagPrptStmt="+asrt);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateTagAssertion(?,?,?,?,?)");
			stmt.setLong(1,asrt.getId());
			stmt.setLong(2,asrt.getSubject().getId());
			stmt.setLong(3,asrt.getProperty().getId());
			stmt.setLong(4,asrt.getObject().getId());
			stmt.setLong(5,asrt.getUser().getId());
			stmt.execute();
			if(stmt.getUpdateCount()>0){
				logger.debug("updateCount="+stmt.getUpdateCount());
				isChanged = true;
			}
		stmt.getResultSet();
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}		
		return isChanged;
	}

	public List<TagAssertion> findTagAssertion(User user, UserTag subjectTag, TagProperty tagPrpt, UserTag objectTag) {
		logger.debug("findTagAssertion: user="+user+",subjectTag="+subjectTag+",tagPrpt="+tagPrpt+",objectTag="+objectTag);
		if(subjectTag != null && tagPrpt == null && objectTag == null){
			return findTagAssertionSubject(user, subjectTag);
		}else if(subjectTag != null && tagPrpt !=null && objectTag == null){
			return findTagAssertion(user, subjectTag, tagPrpt);
		}else if(subjectTag != null && tagPrpt != null && objectTag != null){
			return findTagAssertion3(user,subjectTag,tagPrpt,objectTag);
		}else if(subjectTag == null && tagPrpt != null && objectTag != null){
			return findTagAssertion(user,tagPrpt,objectTag);
		}else if(subjectTag == null && tagPrpt == null && objectTag != null){
			return findTagAssertionObject(user, objectTag);
		}else if(subjectTag == null && tagPrpt == null && objectTag == null){
			return findTagAssertion(user);
		}else if(subjectTag == null && tagPrpt != null && objectTag == null){
			return findTagAssertion(user,tagPrpt);
		}else{
			return new ArrayList<TagAssertion>();
		}
	}
	
	private List<TagAssertion> findTagAssertion(User user, TagProperty tagPrpt){
		logger.debug("findTagAssertion: user="+user+",tagPrpt="+tagPrpt);
		List<TagAssertion> assertions = new ArrayList<TagAssertion>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTagAssertionUserPrptId(?,?)");
			stmt.setLong(1,user.getId());			
			stmt.setLong(2,tagPrpt.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				TagAssertion asrt = createTagAssertionObject(rs);
				assertions.add(asrt);
				logger.debug("found: " + asrt);
			}
			if(assertions.size() == 0){
				logger.debug("found no matching tag assertion");
			}
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return assertions;
	}

	public boolean deleteRDFTypeAssertion(User user, UserTag subjectTag, UserTag[] objectTag) {
		logger.debug("deleteRDFTypeAssertion: user="+user);
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean isOkay = true;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call deleteRDFTypeAssertion(?,?,?)");
			int userId = user.getId();
			int subjectTagId = subjectTag.getId();
			for(int i = 0; i < objectTag.length; i++){
				cstmt.setInt(1,userId);
				cstmt.setInt(2,subjectTagId);
				cstmt.setInt(3,objectTag[i].getId());
				cstmt.addBatch();
			}
			int result[] = cstmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] < 0){
					isOkay = false;
					break;
				}
			}
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cstmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return isOkay;
	}

	public boolean deleteSKOSBroaderAssertion(User user, UserTag subjectTag, UserTag[] objectTag) {
		logger.debug("deleteSKOSBroaderAssertion: user="+user);
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean isOkay = true;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call deleteSKOSBroaderAssertion(?,?,?)");
			int userId = user.getId();
			int subjectTagId = subjectTag.getId();
			for(int i = 0; i < objectTag.length; i++){
				cstmt.setInt(1,userId);
				cstmt.setInt(2,subjectTagId);
				cstmt.setInt(3,objectTag[i].getId());
				cstmt.addBatch();
			}
			int result[] = cstmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] < 0){
					isOkay = false;
					break;
				}
			}
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cstmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return isOkay;
	}

	public boolean deleteSKOSNarrowerAssertion(User user, UserTag subjectTag, UserTag[] objectTag) {
		logger.debug("deleteSKOSNarrowerAssertion: user="+user);
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean isOkay = true;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call deleteSKOSNarrowerAssertion(?,?,?)");
			int userId = user.getId();
			int subjectTagId = subjectTag.getId();
			for(int i = 0; i < objectTag.length; i++){
				cstmt.setInt(1,userId);
				cstmt.setInt(2,subjectTagId);
				cstmt.setInt(3,objectTag[i].getId());
				cstmt.addBatch();
			}
			int result[] = cstmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] < 0){
					isOkay = false;
					break;
				}
			}
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cstmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return isOkay;
	}

	public boolean deleteSKOSRelatedAssertion(User user, UserTag subjectTag, UserTag[] objectTag) {
		logger.debug("deleteSKOSRelatedAssertion: user="+user);
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean isOkay = true;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call deleteSKOSRelatedAssertion(?,?,?)");
			int userId = user.getId();
			int subjectTagId = subjectTag.getId();
			for(int i = 0; i < objectTag.length; i++){
				cstmt.setInt(1,userId);
				cstmt.setInt(2,subjectTagId);
				cstmt.setInt(3,objectTag[i].getId());
				cstmt.addBatch();
			}
			int result[] = cstmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] < 0){
					isOkay = false;
					break;
				}
			}
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cstmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return isOkay;
	}

	public boolean createTagAssertion(TagAssertion[] asrt) {
		logger.debug("createTagAssertion");
		Connection conn = null;
		CallableStatement cStmt = null;
		//int id = -1;
		boolean isOkay = true;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createTagAssertionNoOutput(?,?,?,?)}");
			for(TagAssertion assertion : asrt){				
				cStmt.setInt(1,assertion.getSubject().getId());
				cStmt.setInt(2,assertion.getProperty().getId());
				cStmt.setInt(3,assertion.getObject().getId());
				cStmt.setInt(4,assertion.getUser().getId());				
				cStmt.addBatch();
			}
			int result[] = cStmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] < 0){
					isOkay = false;
					break;
				}
			}
		} catch (Exception e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return isOkay;
	}

	public boolean deleteRDFTypeClassAssertion(User user, UserTag classTag) {
		logger.debug("deleteRDFTypeClassAssertion: user="+user + ",classTag="+classTag);
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean isOkay = true;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call deleteRDFTypeClassAssertion(?,?)");
			cstmt.setInt(1,user.getId());
			cstmt.setInt(2,classTag.getId());			
			cstmt.execute();
		} catch (SQLException e) {
			logger.fatal(e);
			isOkay = false;
		} finally{
			try {
				DBUtil.cleanup(conn, cstmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return isOkay;
	}

	public boolean deleteRDFTypeAssertion(User user, UserTag[] subjectTag, UserTag objectTag) {
		logger.debug("deleteRDFInstanceAssertion: user="+user);
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean isOkay = true;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call deleteRDFTypeAssertion(?,?,?)");
			int userId = user.getId();
			int objectTagId = objectTag.getId();
			for(int i = 0; i < subjectTag.length; i++){
				cstmt.setInt(1,userId);
				cstmt.setInt(2,subjectTag[i].getId());
				cstmt.setInt(3,objectTagId);
				cstmt.addBatch();
			}
			int result[] = cstmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] < 0){
					isOkay = false;
					break;
				}
			}
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cstmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return isOkay;
	}
}
