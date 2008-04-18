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
package com.gnizr.db.dao.bookmark;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DBUtil;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.link.LinkDBDao;
import com.gnizr.db.dao.user.UserDBDao;
import com.gnizr.db.vocab.BookmarkSchema;

public class BookmarkDBDao implements BookmarkDao{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4420733276360895659L;

	private static final Logger logger = Logger.getLogger(BookmarkDBDao.class.getName());
	
	private DataSource dataSource;
	
	public BookmarkDBDao(DataSource ds){
		this.dataSource = ds;
	}
	
	public int createBookmark(Bookmark bm) {
		logger.debug("createBookmark: bookmark="+bm);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createBookmark(?,?,?,?,?,?,?,?)}");
			cStmt.setLong(1,bm.getUser().getId());
			cStmt.setLong(2,bm.getLink().getId());
			cStmt.setString(3,bm.getTitle());
			cStmt.setString(4,bm.getNotes());
			cStmt.setTimestamp(5,new Timestamp(bm.getCreatedOn().getTime()));
			cStmt.setTimestamp(6,new Timestamp(bm.getLastUpdated().getTime()));		
			cStmt.setString(7,bm.getTags());
			cStmt.registerOutParameter(8,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(8);
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

	public boolean deleteBookmark(int id) {
		logger.debug("deleteBookmark: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteBookmark(?)");
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

	public static Bookmark createBookmarkObject(ResultSet rs) throws SQLException {
		if(rs == null) return null;
		Bookmark bmark = new Bookmark();
		bmark.setId(rs.getInt(BookmarkSchema.ID));
		bmark.setTitle(rs.getString(BookmarkSchema.TITLE));
		bmark.setNotes(rs.getString(BookmarkSchema.NOTES));
		String tags = rs.getString(BookmarkSchema.TAGS);
		if(tags == null){
			tags = "";
		}
		bmark.setTags(tags);
		String folders = rs.getString(BookmarkSchema.FOLDERS);
		if(folders == null){
			folders = "";
		}
		bmark.setFolders(folders);
		bmark.setCreatedOn(rs.getTimestamp(BookmarkSchema.CREATED_ON));
		bmark.setLastUpdated(rs.getTimestamp(BookmarkSchema.LAST_UPDATED));		
		User u = new User();
		u.setId(rs.getInt(BookmarkSchema.USER_ID));
		bmark.setUser(u);
		
		Link l = new Link();
		l.setId(rs.getInt(BookmarkSchema.LINK_ID));
		bmark.setLink(l);
		
		return bmark;
	}

	public static Bookmark createBookmarkObject2(ResultSet rs) throws SQLException {
		if(rs == null) return null;
		Bookmark bmark = createBookmarkObject(rs);
		
		User u = UserDBDao.createUserObject(rs);
		bmark.setUser(u);
		
		Link l = LinkDBDao.createLinkObject(rs);
		bmark.setLink(l);
		
		return bmark;
	}

	public Bookmark getBookmark(int id) {
		logger.debug("input: id="+id);
		Bookmark bmark = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getBookmark(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				bmark = createBookmarkObject2(rs);
				logger.debug("found: " + bmark);
			}else{
				logger.debug("found no matching bookmark");
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
		return bmark;
	}

	public int getBookmarkCount(User user) {
		logger.debug("getBookmarkCount, input: user="+user);
		DaoResult<Bookmark> result = pageBookmarks(user,0,0);
		return result.getSize();
	}

	public int getBookmarkCount(Link link) {
		logger.debug("getBookmarkCount, input: link="+link);
		DaoResult<Bookmark> result = pageBookmarks(link,0,0);
		return result.getSize();
	}	

	public DaoResult<Bookmark> pageBookmarks(User user, int offset, int count) {
		logger.debug("pageBookmarks, input: user="+user
				+",offset="+offset+",count="+count);
		return pageBookmarks(user, offset, count, BookmarkDao.SORT_BY_CREATED_ON, BookmarkDao.DESCENDING);
	}

	public DaoResult<Bookmark> pageBookmarks(Link link, int offset, int count) {
		logger.debug("pageBookmark, input: link="+link
				+",offset="+offset+",count="+count);
		Connection conn = null;
		CallableStatement stmt = null;
		DaoResult<Bookmark> result= null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageBookmarksLinkId(?,?,?,?)");
			stmt.setInt(1,link.getId());
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			stmt.registerOutParameter(4,Types.INTEGER);
			ResultSet rs = stmt.executeQuery();
			int size = stmt.getInt(4);
			if(size < 0){
				size = 0;
			}
			while(rs.next()){
				Bookmark b = createBookmarkObject2(rs);
				logger.debug("found bmark="+b);
				bmarks.add(b);
			}
			result = new DaoResult<Bookmark>(bmarks,size);
		} catch (SQLException e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}		
		return result;
	}

	public DaoResult<Bookmark> pageBookmarks(Tag tag,int offset, int count) {
		logger.debug("pageBookmarks, input: tag="+tag 
				+",offset="+offset+",count="+count);
		Connection conn = null;
		CallableStatement cStmt = null;
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call pageBookmarksTagId(?,?,?,?);");			
			cStmt.setLong(1,tag.getId());
			cStmt.setLong(2,offset);
			cStmt.setInt(3,count);
			cStmt.registerOutParameter(4, Types.INTEGER);
			ResultSet rs = cStmt.executeQuery();
			int size = cStmt.getInt(4);
			if(size < 0){
				size = 0;
			}
			while(rs.next()){
				Bookmark b = createBookmarkObject2(rs);
				logger.debug("found bmark="+b);
				bmarks.add(b);
			}
			result = new DaoResult<Bookmark>(bmarks,size);
		} catch (SQLException e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}		
		return result;
	}

	public DaoResult<Bookmark> pageBookmarks(User user, Tag tag, int offset, int count) {
		logger.debug("pageBookmark, input: user="+user+",tag="+tag
				+",offset="+offset+",count="+count);
		return pageBookmarks(user, tag, offset, count, BookmarkDao.SORT_BY_CREATED_ON, BookmarkDao.DESCENDING);
	}

	public boolean updateBookmark(Bookmark bm) {
		logger.debug("input: bm="+bm);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateBookmark(?,?,?,?,?,?,?,?)");
			stmt.setLong(1,bm.getId());
			stmt.setLong(2,bm.getUser().getId());
			stmt.setLong(3,bm.getLink().getId());
			stmt.setString(4,bm.getTitle());
			stmt.setString(5,bm.getNotes());
			stmt.setTimestamp(6,new Timestamp(bm.getCreatedOn().getTime()));
			stmt.setTimestamp(7,new Timestamp(bm.getLastUpdated().getTime()));	
			stmt.setString(8,bm.getTags());
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

	public List<Bookmark> findBookmark(User user, Link link) {
		logger.debug("findBookmark, input: user="+user+",link="+link);
		Connection conn = null;
		PreparedStatement stmt = null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findBookmark(?,?)");
			stmt.setLong(1,user.getId());
			stmt.setLong(2,link.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Bookmark b = createBookmarkObject2(rs);
				logger.debug("found bmark="+b);
				bmarks.add(b);
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
		return bmarks;
	}

	public int getBookmarkCount(Tag tag) {
		logger.debug("getBookmarkCount, input: tag="+tag);
		DaoResult<Bookmark> result = pageBookmarks(tag,0,0);
		return result.getSize();
	}
	

	public DaoResult<Bookmark> searchCommunityBookmarks(String searchQuery, int offset, int count) {
		logger.debug("searchCommunityBookmarks: searchQuery=" + searchQuery +", offset="+offset+",count="+count);
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		CallableStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageCommunityBookmarkSearch(?,?,?,?)");
			stmt.setString(1,searchQuery);
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			stmt.registerOutParameter(4,Types.INTEGER);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Bookmark aBookmark = createBookmarkObject2(rs);
				bmarks.add(aBookmark);
				logger.debug("found: " + aBookmark);
			}
			int size = stmt.getInt(4);
			if(size < 0){
				size = 0;
			}
			result = new DaoResult<Bookmark>(bmarks,size);
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return result;
	}

	public DaoResult<Bookmark> searchUserBookmarks(String searchQuery, User user, int offset, int count) {
		logger.debug("searchCommunityBookmarks: searchQuery=" + searchQuery +", offset="+offset+",count="+count);
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		CallableStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageUserBookmarkSearch(?,?,?,?,?)");
			stmt.setString(1,searchQuery);
			stmt.setInt(2,user.getId());
			stmt.setInt(3,offset);
			stmt.setInt(4,count);
			stmt.registerOutParameter(5,Types.INTEGER);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Bookmark aBookmark = createBookmarkObject2(rs);
				bmarks.add(aBookmark);
				logger.debug("found: " + aBookmark);
			}
			int size = stmt.getInt(5);
			if(size < 0){
				size = 0;
			}
			result = new DaoResult<Bookmark>(bmarks,size);
		}catch(SQLException e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return result;
	}

	public DaoResult<Bookmark> pageBookmarks(User user, int offset, int count, int sortBy, int order) {
		Connection conn = null;
		CallableStatement stmt = null;
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageBookmarksUserId(?,?,?,?,?,?);");
			stmt.setInt(1,user.getId());
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			stmt.registerOutParameter(4, Types.INTEGER);		
			stmt.setInt(5,sortBy);
			stmt.setInt(6,order);
			ResultSet rs = stmt.executeQuery();
			int size = stmt.getInt(4);			
			if(size < 0){
				size = 0;
			}
			while(rs.next()){
				Bookmark b = createBookmarkObject2(rs);
				logger.debug("found bmark="+b);
				bmarks.add(b);
			}
			result = new DaoResult<Bookmark>(bmarks,size);
		} catch (SQLException e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}		
		return result;
	}

	public DaoResult<Bookmark> pageBookmarks(User user, Tag tag, int offset, int count, int sortBy, int order) {
		logger.debug("pageBookmark, input: user="+user+",tag="+tag
				+",offset="+offset+",count="+count);
		Connection conn = null;
		CallableStatement stmt = null;
		DaoResult<Bookmark> result = null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageBookmarksUserIdTagId(?,?,?,?,?,?,?)");
			stmt.setLong(1,user.getId());
			stmt.setLong(2,tag.getId());
			stmt.setLong(3,offset);
			stmt.setInt(4,count);
			stmt.registerOutParameter(5, Types.INTEGER);			
			stmt.setInt(6,sortBy);
			stmt.setInt(7,order);
			ResultSet rs = stmt.executeQuery();
			int size = stmt.getInt(5);
			if(size < 0){
				size = 0;
			}
			while(rs.next()){
				Bookmark b = createBookmarkObject2(rs);
				logger.debug("found bmark="+b);
				bmarks.add(b);
			}
			result = new DaoResult<Bookmark>(bmarks,size);
		} catch (SQLException e) {
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}		
		return result;
	}
	
}
	
