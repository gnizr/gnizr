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

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.BookmarkTag;
import com.gnizr.db.dao.DBUtil;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.LinkTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.bookmark.BookmarkDBDao;
import com.gnizr.db.dao.link.LinkDBDao;
import com.gnizr.db.dao.user.UserDBDao;
import com.gnizr.db.vocab.BookmarkTagIdxSchema;
import com.gnizr.db.vocab.LinkTagIdxSchema;
import com.gnizr.db.vocab.TagSchema;
import com.gnizr.db.vocab.UserTagIdxSchema;

public class TagDBDao implements TagDao{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7006296066378980411L;

	private static final Logger logger = Logger.getLogger(TagDBDao.class.getName());
		
	private DataSource dataSource;
	
	public TagDBDao(DataSource ds){
		logger.debug("created TagDBDao. dataSource="+ds.toString());
		this.dataSource = ds;
	}

	public int createLinkTag(LinkTag tag) {
		logger.debug("input: linkTag="+tag);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createLinkTag(?,?,?,?)}");
			cStmt.setInt(1,tag.getLink().getId());
			cStmt.setInt(2,tag.getTag().getId());
			cStmt.setInt(3,tag.getCount());
			cStmt.registerOutParameter(4,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(4);
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

	public int createTag(Tag tag) {
		logger.debug("input: tag="+tag);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createTag(?,?,?)}");
			cStmt.setString(1,tag.getLabel());
			cStmt.setInt(2,tag.getCount());
			cStmt.registerOutParameter(3,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(3);
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

	public int createUserTag(UserTag tag) {
		logger.debug("input: userTag="+tag);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createUserTag(?,?,?,?)}");
			cStmt.setInt(1,tag.getUser().getId());
			cStmt.setInt(2,tag.getTag().getId());
			cStmt.setInt(3,tag.getCount());
			cStmt.registerOutParameter(4,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(4);
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

	public boolean deleteLinkTag(int id) {
		logger.debug("input: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteLinkTag(?)");
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

	public boolean deleteTag(int id) {
		logger.debug("input: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteTag(?)");
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

	public boolean deleteUserTag(int id) {
		logger.debug("input: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteUserTag(?)");
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
	
	public List<Tag> findTag(String tag) {
		logger.debug("input: tag=" + tag);
		List<Tag> tags = new ArrayList<Tag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findTag(?)");
			stmt.setString(1,tag);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Tag aTag = createTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching tags");
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
		return tags;
	}

	public List<LinkTag> findLinkTag(Link link, Tag tag) {
		logger.debug("input: link="+link+",tag=" + tag);
		List<LinkTag> tags = new ArrayList<LinkTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findLinkTag(?,?)");
			stmt.setInt(1,link.getId());
			stmt.setInt(2,tag.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				LinkTag aTag = createLinkTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching link tags");
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
		return tags;
	}

	public List<UserTag> findUserTag(User user, Tag tag) {
		logger.debug("input: user="+user+",tag=" + tag);
		List<UserTag> tags = new ArrayList<UserTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findUserTag(?,?)");
			stmt.setInt(1,user.getId());
			stmt.setInt(2,tag.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				UserTag aTag = createUserTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching user tags");
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
		return tags;
	}

	public LinkTag getLinkTag(int id) {
		logger.debug("input: id="+id);
		LinkTag tag = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getLinkTag(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				tag = createLinkTagObject(rs);
				logger.debug("found: " + tag);
			}else{
				logger.debug("found no matching linkTag");
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
		return tag;
	}

	public static LinkTag createLinkTagObject(ResultSet rs) throws SQLException {
		if(rs == null) return null;
		LinkTag linkTag = new LinkTag();
		linkTag.setId(rs.getInt(LinkTagIdxSchema.ID));
		linkTag.setCount(rs.getInt(LinkTagIdxSchema.COUNT));
		
		Tag t = createTagObject(rs);
		linkTag.setTag(t);
		
		Link l = LinkDBDao.createLinkObject(rs);
		linkTag.setLink(l);
		
		return linkTag;
	}

	public Tag getTag(int id) {
		logger.debug("input: id="+id);
		Tag tag = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getTag(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				tag = createTagObject(rs);
				logger.debug("found: " + tag);
			}else{
				logger.debug("found no matching tag");
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
		return tag;
	}

	public static Tag createTagObject(ResultSet rs) throws SQLException {
		if(rs == null) return null;
		Tag tag = new Tag();
		tag.setId(rs.getInt(TagSchema.ID));
		tag.setLabel(rs.getString(TagSchema.TAG));
		tag.setCount(rs.getInt(TagSchema.COUNT));
		return tag;
	}
	
	public static Tag createNamedTagObject(String tblAliasName, ResultSet rs) throws SQLException{
		return createNamedTagObject(tblAliasName, rs, false);
	}
	
	public static Tag createNamedTagObject(String tblAliasName, ResultSet rs, boolean noColumnRef) throws SQLException{
		if(rs == null) return null;
		String idCol = tblAliasName+TagSchema.ID_COL;
		String tagCol = tblAliasName + TagSchema.TAG_COL;
		String countCol = tblAliasName +TagSchema.COUNT_COL;
		if(noColumnRef == true){
			idCol = idCol.replaceAll("\\.", "_");
			tagCol = tagCol.replaceAll("\\.", "_");
			countCol = countCol.replaceAll("\\.", "_");
		}		
		Tag tag = new Tag();
		tag.setId(rs.getInt(idCol));
		tag.setLabel(rs.getString(tagCol));
		tag.setCount(rs.getInt(countCol));
		return tag;
	}
	
	
	public UserTag getUserTag(int id) {
		logger.debug("input: id="+id);
		UserTag tag = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getUserTag(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				tag = createUserTagObject(rs);
				logger.debug("found: " + tag);
			}else{
				logger.debug("found no matching userTag");
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
		return tag;
	}

	public static UserTag createUserTagObject(String idCol, ResultSet rs) throws SQLException {
		if(rs == null) return null;
		UserTag userTag = new UserTag();
		if(idCol != null){
			userTag.setId(rs.getInt(idCol));
		}else{
			userTag.setId(rs.getInt(UserTagIdxSchema.ID));	
		}
		userTag.setCount(rs.getInt(UserTagIdxSchema.COUNT));
		
		Tag t = createTagObject(rs);
		userTag.setTag(t);
		
		User u = UserDBDao.createUserObject(rs);
		userTag.setUser(u);
		
		return userTag;
	}
	
	public static UserTag createNamedUserTagObject(String userTagTblAlias, String tagTblAlias, String userTblAlias, ResultSet rs) throws SQLException{
		return createNamedUserTagObject(userTagTblAlias, tagTblAlias, userTblAlias, rs,false);
	}
	
	public static UserTag createNamedUserTagObject(String userTagTblAlias, String tagTblAlias, String userTblAlias, ResultSet rs, boolean noColumnRef) throws SQLException{
		if(rs == null) return null;
		UserTag userTag = new UserTag();		
		Tag tag = createNamedTagObject(tagTblAlias, rs, noColumnRef);
		userTag.setTag(tag);
		User user = UserDBDao.createNamedUserObject(userTblAlias, rs, noColumnRef);		
		userTag.setUser(user);		
		
		String idCol = userTagTblAlias+UserTagIdxSchema.ID_COL;
		String countCol = userTagTblAlias+UserTagIdxSchema.COUNT_COL;
		if(noColumnRef == true){
			idCol = idCol.replaceAll("\\.","_");
			countCol = countCol.replaceAll("\\.","_");
		}		
		userTag.setId(rs.getInt(idCol));
		userTag.setCount(rs.getInt(countCol));
		return userTag;	
	}
	
	
	public static UserTag createUserTagObject(ResultSet rs) throws SQLException {
		return createUserTagObject(null, rs);
	}

	public static BookmarkTag createBookmarkTagObject(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		BookmarkTag bookmarkTag = new BookmarkTag();
		bookmarkTag.setId(rs.getInt(BookmarkTagIdxSchema.ID));
		bookmarkTag.setCount(rs.getInt(BookmarkTagIdxSchema.COUNT));
		bookmarkTag.setPosition(rs.getInt(BookmarkTagIdxSchema.POSITION));
		
		Tag tag = createTagObject(rs);
		bookmarkTag.setTag(tag);
		
		Bookmark bookmark = BookmarkDBDao.createBookmarkObject2(rs);
		bookmarkTag.setBookmark(bookmark);
				
		return bookmarkTag;
	}
	
	public boolean updateLinkTag(LinkTag tag) {
		logger.debug("input: linkTag="+tag);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateLinkTag(?,?,?,?)");
			stmt.setInt(1,tag.getId());
			stmt.setInt(2,tag.getLink().getId());
			stmt.setInt(3,tag.getTag().getId());
			stmt.setInt(4,tag.getCount());
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

	public boolean updateTag(Tag tag) {
		logger.debug("input: tag="+tag);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateTag(?,?,?)");
			stmt.setInt(1,tag.getId());
			stmt.setString(2,tag.getLabel());
			stmt.setInt(3,tag.getCount());
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

	public boolean updateUserTag(UserTag tag) {
		logger.debug("input: tag="+tag);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateUserTag(?,?,?,?)");
			stmt.setInt(1,tag.getId());
			stmt.setInt(2,tag.getUser().getId());
			stmt.setInt(3,tag.getTag().getId());
			stmt.setInt(4,tag.getCount());
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
	
	
	public List<UserTag> findUserTag(User user) {
		logger.debug("input: user="+user);
		List<UserTag> tags = new ArrayList<UserTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findUserTagAll(?)");
			stmt.setInt(1,user.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				UserTag aTag = createUserTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching user tags");
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
		return tags;
	}


	public List<LinkTag> findLinkTag(Link link, int minFreq) {
		logger.debug("findLinkTag: link="+link +",minFreq="+minFreq);
		List<LinkTag> tags = new ArrayList<LinkTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findLinkTagMinFreq(?,?)");
			stmt.setInt(1,link.getId());
			stmt.setInt(2,minFreq);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				LinkTag aTag = createLinkTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching link tags");
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
		return tags;
	}
	
	public List<Tag> findTag(int topN){
		logger.debug("findTag: topN="+topN);
		return findTag(topN,TagDao.SORT_FREQ);
	}

	public List<Tag> findTag(int topN, int sortBy) {
		logger.debug("findTag: topN="+topN +",sortBy="+sortBy);
		List<Tag> tags = new ArrayList<Tag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			if(sortBy == SORT_ALPH){
				stmt = conn.prepareStatement("call findTagTopNSortByAlpha(?)");
			}else{
				stmt = conn.prepareStatement("call findTagTopNSortByFreq(?)");
			}
			stmt.setInt(1,topN);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Tag aTag = createTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching link tags");
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
		return tags;
	}
	
	public List<UserTag> findUserTag(User user, int minFreq){
		return findUserTag(user, minFreq,TagDao.SORT_ALPH);
	}

	public List<UserTag> findUserTag(User user, int minFreq, int sortBy) {
		logger.debug("findUserTag: user="+user+",minFreq="+minFreq+",sortBy="+sortBy);
		List<UserTag> tags = new ArrayList<UserTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			if(TagDao.SORT_FREQ == sortBy){
				stmt = conn.prepareStatement("call findUserTagMinFreqSortFreq(?,?);");
			}else{
				stmt = conn.prepareStatement("call findUserTagMinFreqSortAlph(?,?);");
			}
			stmt.setInt(1,user.getId());
			stmt.setInt(2,minFreq);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				UserTag aTag = createUserTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching user tags");
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
		return tags;
	}

	public int createBookmarkTag(BookmarkTag tag) {
		logger.debug("createBookmarkTag: bookmarkTag="+tag);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createBookmarkTag(?,?,?,?,?)}");
			cStmt.setInt(1,tag.getBookmark().getId());
			cStmt.setInt(2,tag.getTag().getId());			
			cStmt.setInt(3,tag.getCount());
			cStmt.setInt(4,tag.getPosition());
			cStmt.registerOutParameter(5,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(5);
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

	public boolean deleteBookmarkTag(int id) {
		logger.debug("deleteBookmarkTag: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteBookmarkTag(?)");
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

	public BookmarkTag getBookmarkTag(int id) {
		logger.debug("getBookmarkTag: id="+id);
		BookmarkTag tag = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getBookmarkTag(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				tag = createBookmarkTagObject(rs);
				logger.debug("found: " + tag);
			}else{
				logger.debug("found no matching bookmarkTag");
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
		return tag;
	}

	public boolean updateBookmarkTag(BookmarkTag tag) {
		logger.debug("updateBookmarkTag: bookmarkTag="+tag);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateBookmarkTag(?,?,?,?,?)");
			stmt.setInt(1,tag.getId());
			stmt.setInt(2,tag.getBookmark().getId());
			stmt.setInt(3,tag.getTag().getId());
			stmt.setInt(4,tag.getCount());
			stmt.setInt(5,tag.getPosition());
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

	public int getBookmarkTagId(Bookmark bookmark, Tag tag) {
		logger.debug("getBookmarkTagId: bookmark="+bookmark+",tag="+tag);	
		int id = -1;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getBookmarkTagId(?,?);");
			stmt.setInt(1,bookmark.getId());
			stmt.setInt(2,tag.getId());
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				id = rs.getInt(BookmarkTagIdxSchema.ID);			
			}else{
				logger.debug("found no matching bookmarkTag");
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
		return id;
	}

	public List<BookmarkTag> findBookmarkTag(User user) {
		logger.debug("input: user="+user);
		List<BookmarkTag> tags = new ArrayList<BookmarkTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findBookmarkTagUserIdGrouped(?)");
			stmt.setInt(1,user.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				BookmarkTag aTag = createBookmarkTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching bookmark tags");
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
		return tags;
	}

	public List<BookmarkTag> findBookmarkTag(Folder folder) {
		logger.debug("findBookmarkTagFolder: folder="+folder);
		List<BookmarkTag> tags = new ArrayList<BookmarkTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findBookmarkTagByFolderId(?)");
			stmt.setInt(1,folder.getId());
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				BookmarkTag aTag = createBookmarkTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching bookmark tags");
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
		return tags;
	}

	public boolean[] addTagCountOne(Tag[] tags, User user, Link link, Bookmark bookmark) {
		logger.debug("addTagCountOne: tags="+tags+",user="+user+",link="+link+",bookmark="+bookmark);
		Connection conn = null;
		CallableStatement stmt = null;
		boolean[] opOkay = new boolean[tags.length];
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call addTagCountOne(?,?,?,?)");
			for(Tag tag : tags){
				stmt.setInt(1,tag.getId());
				stmt.setInt(2,user.getId());
				stmt.setInt(3,link.getId());
				stmt.setInt(4,bookmark.getId());
				stmt.addBatch();
			}
			int result[] = stmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] >= 0){
					opOkay[i] = true;
				}else{
					opOkay[i] = false;
				}
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
		return opOkay;
	}

	public boolean[] subtractTagCountOne(Tag[] tags, User user, Link link, Bookmark bookmark) {
		logger.debug("subtractTagCountOne: tags="+tags+",user="+user+",link="+link+",bookmark="+bookmark);
		Connection conn = null;
		CallableStatement stmt = null;
		boolean[] opOkay = new boolean[tags.length];
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call subtractTagCountOne(?,?,?,?)");
			for(Tag tag: tags){
				stmt.setInt(1,tag.getId());
				stmt.setInt(2,user.getId());
				stmt.setInt(3,link.getId());
				stmt.setInt(4,bookmark.getId());
				stmt.addBatch();
			}		
			int result[] = stmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] >= 0){
					opOkay[i] = true;
				}else{
					opOkay[i] = false;
				}
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
		return opOkay;
	}

	public List<Bookmark> expandTag(User user, Tag fromTag, Tag[] toTags) {
		logger.debug("expandTag: user="+user+", fromTag="+fromTag+",toTags="+toTags);
		Connection conn = null;
		CallableStatement stmt = null;
		List<Bookmark> changedBookmarks = new ArrayList<Bookmark>();
		try {
			ResultSet rs = null;
			conn = dataSource.getConnection();			
			stmt = conn.prepareCall("call expandTag(?,?,?)");
			for(Tag t : toTags){
				stmt.setInt(1,fromTag.getId());
				stmt.setInt(2,t.getId());
				stmt.setInt(3,user.getId());
				rs = stmt.executeQuery();
			}			
			while(rs.next()){
				Bookmark bm = BookmarkDBDao.createBookmarkObject2(rs);
				changedBookmarks.add(bm);
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
		return changedBookmarks;
	}

	public List<Bookmark> reduceTag(User user, Tag[] tags) {
		logger.debug("expandTag: user="+user+", tags="+tags);
		Connection conn = null;
		CallableStatement stmt = null;
		List<Bookmark> changeBookmarks = new ArrayList<Bookmark>();
		try {
			ResultSet rs = null;
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call reduceTag(?,?)");
			for(Tag t : tags){
				stmt.setInt(1,t.getId());
				stmt.setInt(2,user.getId());
				rs = stmt.executeQuery();
			}			
			while(rs.next()){
				Bookmark bm = BookmarkDBDao.createBookmarkObject2(rs);
				changeBookmarks.add(bm);
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
		return changeBookmarks;
	}

	public List<BookmarkTag> findBookmarkTagCommunitySearch(String searchQuery) {
		logger.debug("findBookmarkTagCommunitySearch: searchQuery="+searchQuery);
		List<BookmarkTag> tags = new ArrayList<BookmarkTag>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findBookmarkTagCommunitySearch(?)");
			stmt.setString(1,searchQuery);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				BookmarkTag aTag = createBookmarkTagObject(rs);
				tags.add(aTag);
				logger.debug("found: " + aTag);
			}
			if(tags.size() == 0){
				logger.debug("found no matching bookmark tags");
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
		return tags;
	}

	public DaoResult<LinkTag> pageLinkTagSortByFreq(Link link, int offset, int count) {
		logger.debug("findLinkTagSortByFreq: link="+link+",offset="+offset+",count="+count);
		DaoResult<LinkTag> result = null;
		List<LinkTag> tags = new ArrayList<LinkTag>();
		CallableStatement stmt = null;
		Connection conn = null;
		try{
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageLinkTagSortByFreq(?,?,?,?)");
			stmt.setInt(1,link.getId());
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			stmt.registerOutParameter(4,Types.INTEGER);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				LinkTag linktag =createLinkTagObject(rs);
				tags.add(linktag);
				logger.debug("found: " + linktag);
			}
			int size = stmt.getInt(4);
			if(size < 0){
				size = 0;
			}
			result = new DaoResult<LinkTag>(tags,size);
		}catch (Exception e) {
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
