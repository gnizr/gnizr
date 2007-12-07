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
package com.gnizr.db.dao.subscription;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.gnizr.db.DBUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.FeedSubscription;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDBDao;
import com.gnizr.db.dao.folder.FolderDBDao;
import com.gnizr.db.vocab.FeedSubscriptionSchema;

/**
 *
 */
public class FeedSubscriptionDBDao implements FeedSubscriptionDao {

	/**
	 * 
	 */
	private static final long serialVersionUID = -893429788735802627L;
	private static final Logger logger = Logger.getLogger(FeedSubscriptionDBDao.class);
	
	private DataSource dataSource;
	
	public FeedSubscriptionDBDao(DataSource ds){
		this.dataSource = ds;
	}

	public int addImportFolders(FeedSubscription subscription, List<Folder> folders) {
		logger.debug("addImportFolders: subscription="+subscription+",folders="+folders);
		Connection conn = null;
		CallableStatement cStmt = null;
		int numUpdated = 0;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call addImportFolder(?,?)};");			
			for(Folder folder : folders){
				cStmt.setInt(1,subscription.getId());
				cStmt.setInt(2,folder.getId());				
				cStmt.addBatch();				
			}			
			int result[] = cStmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] >= 0 ){
					numUpdated++;
				}
			}		
		} catch (SQLException e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return numUpdated;
	}

	public int createSubscription(FeedSubscription subscription) {
		logger.debug("createSubscription: subscription=" + subscription);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createSubscription(?,?,?,?,?,?)}");
			cStmt.setInt(1,subscription.getBookmark().getId());
			Date lastSync = subscription.getLastSync();
			if(lastSync != null){
				cStmt.setTimestamp(2,new Timestamp(lastSync.getTime()));
			}else{
				cStmt.setTimestamp(2,null);
			}
			cStmt.setString(3,subscription.getMatchText());
			cStmt.setBoolean(4,subscription.isAutoImport());			
			Date pubDate = subscription.getPubDate();
			if(pubDate != null){
				cStmt.setTimestamp(5, new Timestamp(pubDate.getTime()));
			}else{
				cStmt.setTimestamp(5, null);
			}
			cStmt.registerOutParameter(6,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(6);
		} catch (Exception e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return id;
	}

	public boolean deleteSubscription(User owner, String feedUrl) {
		logger.debug("deleteSubscription: owner=" + owner+",feedUrl="+feedUrl);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteSubscriptionByUserIdFeedUrl(?,?)");
			stmt.setInt(1,owner.getId());
			stmt.setString(2,feedUrl);
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

	public static FeedSubscription createFeedSubscriptionObject(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		FeedSubscription feed = new FeedSubscription();
		Bookmark bookmark = BookmarkDBDao.createBookmarkObject2(rs);
		feed.setId(rs.getInt(FeedSubscriptionSchema.ID));
		feed.setAutoImport(rs.getBoolean(FeedSubscriptionSchema.AUTO_IMPORT));
		feed.setLastSync(rs.getTimestamp(FeedSubscriptionSchema.LAST_SYNC));
		feed.setMatchText(rs.getString(FeedSubscriptionSchema.MATCH_TEXT));
		feed.setPubDate(rs.getTimestamp(FeedSubscriptionSchema.PUB_DATE));
		feed.setBookmark(bookmark);
		
		List<String> folderList = new ArrayList<String>();
		String folders = rs.getString(FeedSubscriptionSchema.IMPORT_FOLDERS);
		if(folders != null){
			String[] fdrs = folders.split("/");
			if(fdrs != null && fdrs.length > 0){
				folderList = Arrays.asList(fdrs);
			}
		}
		feed.setImportFolders(folderList);		
		return feed;
	}
	
	
	public FeedSubscription getSubscription(int id) {
		logger.debug("getSubscription: id="+id);
		FeedSubscription feed = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getSubscriptionById(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				feed = createFeedSubscriptionObject(rs);
				logger.debug("found: " + feed);
			}else{
				logger.debug("found no matching feed subscription");
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
		return feed;
	}

	public FeedSubscription getSubscription(User owner, String feedUrl) {
		logger.debug("getFolder: owner="+owner + ",feedUrl="+feedUrl);
		FeedSubscription feed = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getSubscriptionByUserIdFeedUrl(?,?);");
			stmt.setInt(1,owner.getId());
			stmt.setString(2,feedUrl);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				feed = createFeedSubscriptionObject(rs);
				logger.debug("found: " + feed);
			}else{
				logger.debug("found no matching folder");
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
		return feed;
	}

	public DaoResult<FeedSubscription> pageSubscription(User user, int offset, int count) {
		logger.debug("pageSubscription: user="+user + ",offset="+offset+",count="+count);		
		List<FeedSubscription> feeds = new ArrayList<FeedSubscription>();
		DaoResult<FeedSubscription> result = null;
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call pageSubscriptionByOwnerId(?,?,?,?);");
			cStmt.setInt(1,user.getId());
			cStmt.setInt(2,offset);
			cStmt.setInt(3,count);
			cStmt.registerOutParameter(4,Types.INTEGER);
			ResultSet rs = cStmt.executeQuery();
			while(rs.next()){
				FeedSubscription feed = createFeedSubscriptionObject(rs);				
				feeds.add(feed);
			}
			int size = cStmt.getInt(4);
			if(size < 0){
				size = 0;
			}			
			result = new DaoResult<FeedSubscription>(feeds,size);
			logger.debug("DaoResult: feeds="+feeds+",size="+size);
		}catch(Exception e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return result;
	}

	public int removeImportFolders(FeedSubscription subscription, List<Folder> folders) {
		logger.debug("removeImportFolders: subscription="+subscription + ",folders="+folders);
		Connection conn = null;
		CallableStatement cstmt = null;
		int numUpdated = 0;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call removeImportFolder(?,?)");
			int feedId = subscription.getId();		
			for(Folder folder : folders){
				cstmt.setInt(1,feedId);
				cstmt.setInt(2,folder.getId());				
				cstmt.addBatch();
			}
			int result[] = cstmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] > 0){
					numUpdated++;					
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
		return numUpdated;
	}

	public boolean updateSubscription(FeedSubscription subscription) {
		logger.debug("updateSubscription: subscription=" + subscription);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateSubscription(?,?,?,?,?,?)");
			stmt.setInt(1,subscription.getId());
			stmt.setInt(2,subscription.getBookmark().getId());
			Date lastSync = subscription.getLastSync();
			if(lastSync != null){
				stmt.setTimestamp(3, new Timestamp(lastSync.getTime()));
			}else{
				stmt.setTimestamp(3, null);
			}
			stmt.setString(4,subscription.getMatchText());
			stmt.setBoolean(5,subscription.isAutoImport());
			Date pubDate = subscription.getPubDate();
			if(pubDate != null){
				stmt.setTimestamp(6, new Timestamp(pubDate.getTime()));
			}else{
				stmt.setTimestamp(6, null);
			}
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

	public List<Folder> listImportFolder(FeedSubscription subscription) {
		logger.debug("listImportFolder: subscription="+subscription);
		Connection conn = null;
		PreparedStatement stmt = null;
		List<Folder> folders = new ArrayList<Folder>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findImportFolders(?)");
			stmt.setLong(1,subscription.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Folder folder = FolderDBDao.createFolderObject(rs);
				logger.debug("found folder="+folder);
				folders.add(folder);
			}
		} catch (SQLException e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}		
		return folders;
	}

	public List<FeedSubscription> listAutoImportSubscription(int ageHour) {
		logger.debug("listAutoImportSubscription");		
		List<FeedSubscription> feeds = new ArrayList<FeedSubscription>();		
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call findAutoImportSubscription(?);");			
			cStmt.setInt(1,ageHour);
			cStmt.execute();
			ResultSet rs = cStmt.executeQuery();
			while(rs.next()){
				FeedSubscription feed = createFeedSubscriptionObject(rs);				
				feeds.add(feed);
			}		
			logger.debug("DaoResult: feeds="+feeds);
		}catch(Exception e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return feeds;
	}

	
	
}
