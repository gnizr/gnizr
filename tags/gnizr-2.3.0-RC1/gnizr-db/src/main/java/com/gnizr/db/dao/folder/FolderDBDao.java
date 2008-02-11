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
package com.gnizr.db.dao.folder;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DBUtil;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.db.dao.Tag;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.bookmark.BookmarkDBDao;
import com.gnizr.db.dao.tag.TagDBDao;
import com.gnizr.db.dao.user.UserDBDao;
import com.gnizr.db.vocab.FolderSchema;
import com.gnizr.db.vocab.FolderTagSchema;

public class FolderDBDao implements FolderDao{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8291307434207262302L;

	private static final Logger logger = Logger.getLogger(FolderDBDao.class);
	private static final String MEMBER_TAG_COL = "mem_tag";
	private static final String GROUP_TAG_COL = "grp_tag";
	private static final String FOLDER_TAG_IDX_COL = "fti"; 
	
	private DataSource dataSource;
	
	public FolderDBDao(DataSource ds){
		this.dataSource = ds;
	}
	
	public boolean[] addBookmarks(Folder folder, List<Bookmark> bookmarks, Date timestamp) {
		logger.debug("addBookmarks: folder="+folder+",bookmarks="+bookmarks);
		Connection conn = null;
		CallableStatement cStmt = null;
		boolean[] opOkay = new boolean[bookmarks.size()];
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call addBookmarkToFolder(?,?,?)};");
			Timestamp tsmp = new Timestamp(timestamp.getTime());
			for(Bookmark bm : bookmarks){
				cStmt.setInt(1,folder.getId());
				cStmt.setInt(2,bm.getId());
				cStmt.setTimestamp(3,tsmp);
				cStmt.addBatch();				
			}			
			int result[] = cStmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] >= 0 ){
					opOkay[i] = true;
				}else{
					opOkay[i] = false;
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
		return opOkay;
	}

	public int createFolder(Folder folder) {
		logger.debug("createFolder: folder=" + folder);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createFolder(?,?,?,?,?)}");
			cStmt.setInt(1,folder.getUser().getId());
			cStmt.setString(2,folder.getName());
			cStmt.setString(3,folder.getDescription());
			cStmt.setTimestamp(4,new Timestamp(folder.getLastUpdated().getTime()));
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

	public boolean deleteFolder(User owner, String folderName) {
		logger.debug("deleteFolder: owner=" + owner+",folderName="+folderName);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteFolderByOwnerIdFolderName(?,?)");
			stmt.setInt(1,owner.getId());
			stmt.setString(2,folderName);
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
	
	public static Folder createFolderObject(ResultSet rs) throws SQLException{
		if(rs == null) return null;
		Folder folder = new Folder();
		folder.setId(rs.getInt(FolderSchema.ID));
		folder.setName(rs.getString(FolderSchema.FOLDER_NAME));
		folder.setDescription(rs.getString(FolderSchema.DESCRIPTION));
		folder.setLastUpdated(rs.getTimestamp(FolderSchema.LAST_UPDATED));
		folder.setSize(rs.getInt(FolderSchema.SIZE));
		User user = UserDBDao.createUserObject("owner",rs);
		folder.setUser(user);
		
		return folder;		
	}
	
	public DaoResult<Folder> pageFolders(User owner, int offset, int count) {
		logger.debug("pageFolders: owner="+owner + ",offset="+offset+",count="+count);		
		List<Folder> folders = new ArrayList<Folder>();
		DaoResult<Folder> result = null;
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call pageFoldersByOwnerId(?,?,?,?);");
			cStmt.setInt(1,owner.getId());
			cStmt.setInt(2,offset);
			cStmt.setInt(3,count);
			cStmt.registerOutParameter(4,Types.INTEGER);
			ResultSet rs = cStmt.executeQuery();
			while(rs.next()){
				Folder folder = createFolderObject(rs);				
				folders.add(folder);
			}
			int size = cStmt.getInt(4);
			if(size < 0){
				size = 0;
			}
			result = new DaoResult<Folder>(folders,size);
			logger.debug("DaoResult: folders="+folders+",size="+size);
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

	public Folder getFolder(int id) {
		logger.debug("getFolder: id="+id);
		Folder folder = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getFolderById(?);");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				folder = createFolderObject(rs);
				logger.debug("found: " + folder);
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
		return folder;
	}

	public Folder getFolder(User owner, String folderName) {
		logger.debug("getFolder: owner="+owner + ",folderName="+folderName);
		Folder folder = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getFolderByOwnerIdFolderName(?,?);");
			stmt.setInt(1,owner.getId());
			stmt.setString(2,folderName);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				folder = createFolderObject(rs);
				logger.debug("found: " + folder);
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
		return folder;
	}

	public int removeAllBookmarks(Folder folder) {
		logger.debug("removeAllBookmarks: folder="+folder);
		Connection conn = null;
		CallableStatement cstmt = null;
		int upCnt = 0;
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call removeAllBookmarksFromFolder(?)");
			cstmt.setInt(1,folder.getId());
			ResultSet rs = cstmt.executeQuery();
			if(rs.next()){
				upCnt = rs.getInt(1);
			}
		}catch(SQLException e){
			logger.fatal(e);			
		}finally{
			try {
				DBUtil.cleanup(conn, cstmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return upCnt;
	}

	public boolean[] removeBookmarks(Folder folder, List<Bookmark> bookmarks) {
		logger.debug("removeBookmarkFromFolder: folder="+folder + ",boomarks="+bookmarks);
		Connection conn = null;
		CallableStatement cstmt = null;
		boolean[] opOkay = new boolean[bookmarks.size()];
		try {
			conn = dataSource.getConnection();
			cstmt = conn.prepareCall("call removeBookmarkFromFolder(?,?)");
			int folderId = folder.getId();		
			for(Bookmark bm : bookmarks){
				cstmt.setInt(1,folderId);
				cstmt.setInt(2,bm.getId());				
				cstmt.addBatch();
			}
			int result[] = cstmt.executeBatch();
			for(int i = 0; i < result.length; i++){
				if(result[i] >= 0){
					opOkay[i] = true;
				}else{
					opOkay[i] = false;
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
		return opOkay;
	}

	public boolean updateFolder(Folder folder) {
		logger.debug("updateFolder: folder=" + folder);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateFolder(?,?,?,?,?)");
			stmt.setInt(1,folder.getId());
			stmt.setInt(2,folder.getUser().getId());
			stmt.setString(3,folder.getName());
			stmt.setString(4,folder.getDescription());			
			stmt.setTimestamp(5, new Timestamp(folder.getLastUpdated().getTime()));
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

	public DaoResult<Bookmark> pageBookmarks(Folder folder, int offset, int count) {
		logger.debug("pageBookmarks: folder="+folder + ",offset="+offset+",count="+count);		
		return pageBookmarks(folder, offset, count, FolderDao.SORT_BY_BMRK_FLDR_LAST_UPDATED,FolderDao.DESCENDING);
	}

	public DaoResult<Folder> pageContainedInFolders(Bookmark bookmark, int offset, int count) {
		logger.debug("pageContainedInFolders: bookmark="+bookmark + ",offset="+offset+",count="+count);		
		List<Folder> folders = new ArrayList<Folder>();
		DaoResult<Folder> result = null;
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call pageContainedInFolders(?,?,?,?);");
			cStmt.setInt(1,bookmark.getId());
			cStmt.setInt(2,offset);
			cStmt.setInt(3,count);
			cStmt.registerOutParameter(4,Types.INTEGER);			
			ResultSet rs = cStmt.executeQuery();
			int size = cStmt.getInt(4);
			if(size < 0){
				size = 0;
			}	
			while(rs.next()){
				Folder aFolder = createFolderObject(rs);				
				folders.add(aFolder);
			}
			result = new DaoResult<Folder>(folders,size);
			logger.debug("DaoResult: folders="+folders+",size="+size);
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

	public DaoResult<Bookmark> pageBookmarks(Folder folder, Tag tag, int offset, int count) {
		logger.debug("pageBookmarks: folder="+folder + ", tag=" + tag +",offset="+offset+",count="+count);		
		return pageBookmarks(folder, tag, offset, count, FolderDao.SORT_BY_BMRK_FLDR_LAST_UPDATED, FolderDao.DESCENDING);
	}

	public DaoResult<Bookmark> pageBookmarks(Folder folder, int offset, int count, int sortBy, int order) {
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		DaoResult<Bookmark> result = null;
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call pageBookmarksByFolderId(?,?,?,?,?,?);");
			cStmt.setInt(1,folder.getId());
			cStmt.setInt(2,offset);
			cStmt.setInt(3,count);
			cStmt.registerOutParameter(4,Types.INTEGER);			
			cStmt.setInt(5,sortBy);
			cStmt.setInt(6,order);
			ResultSet rs = cStmt.executeQuery();
			int size = cStmt.getInt(4);
			if(size < 0){
				size = 0;
			}	
			while(rs.next()){
				Bookmark bookmark = BookmarkDBDao.createBookmarkObject2(rs);				
				bmarks.add(bookmark);
			}
			result = new DaoResult<Bookmark>(bmarks,size);
			logger.debug("DaoResult: bookmarks="+bmarks+",size="+size);
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

	public DaoResult<Bookmark> pageBookmarks(Folder folder, Tag tag, int offset, int count, int sortBy, int order) {
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		DaoResult<Bookmark> result = null;
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call pageBookmarksByFolderIdTagId(?,?,?,?,?,?,?);");
			cStmt.setInt(1,folder.getId());
			cStmt.setInt(2,tag.getId());
			cStmt.setInt(3,offset);
			cStmt.setInt(4,count);
			cStmt.registerOutParameter(5,Types.INTEGER);			
			cStmt.setInt(6,sortBy);
			cStmt.setInt(7,order);
			ResultSet rs = cStmt.executeQuery();
			int size = cStmt.getInt(5);
			if(size < 0){
				size = 0;
			}	
			while(rs.next()){
				Bookmark bookmark = BookmarkDBDao.createBookmarkObject2(rs);				
				bmarks.add(bookmark);
			}
			result = new DaoResult<Bookmark>(bmarks,size);
			logger.debug("DaoResult: bookmarks="+bmarks+",size="+size);
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
	
	public boolean hasFolderTag(Folder folder, Tag tag){
		logger.debug("hasFolderTag: folder=" + folder+",tag="+tag);
		Connection conn = null;
		CallableStatement cStmt = null;		
		boolean exists = false;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call hasFolderTag(?,?)}");
			cStmt.setInt(1,folder.getId());
			cStmt.setInt(2,tag.getId());
			ResultSet rs = cStmt.executeQuery();			
			if(rs.next()){
				exists = true;
			}
		} catch (Exception e) {
			logger.fatal(e);			
		} finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return exists;
	}
	
	public List<FolderTag> findTagsInFolder(Folder folder, int minFreq, int sortBy, int order) {
		logger.debug("findTagsInFolder: folder="+folder + ",minFreq=" + minFreq+",sortBy="+sortBy+",order="+order);		
		List<FolderTag> folderTags = new ArrayList<FolderTag>();
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call findAllTagsInFolder(?,?,?,?);");
			cStmt.setInt(1,folder.getId());
			cStmt.setInt(2,minFreq);
			cStmt.setInt(3,sortBy);
			cStmt.setInt(4,order);			
			ResultSet rs = cStmt.executeQuery();
			while(rs.next()){
				FolderTag ft = new FolderTag();
				Tag t = TagDBDao.createTagObject(rs);
				ft.setTag(t);
				ft.setCount(rs.getInt(FolderTagSchema.COUNT));
				folderTags.add(ft);
			}
			if(folderTags.isEmpty() == false){
				setFolderOnFolderTags(folderTags,folder);
			}			
		}catch(Exception e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return folderTags;
	}
	
	private void setFolderOnFolderTags(List<FolderTag> folderTags, Folder f){
		logger.debug("setFolderOnFolderTags: folderTags=" + folderTags + ",folder="+f);
		Folder folder = getFolder(f.getId());
		if(folder != null){
			for(FolderTag ft : folderTags){
				ft.setFolder(new Folder(folder));
			}
		}
	}

	public Map<String, List<FolderTag>> listTagGroups(Folder folder, int minFreq, int sortBy, int order) {
		logger.debug("listTagGroups: folder="+folder + ",minFreq=" + minFreq+",sortBy="+sortBy+",order="+order);		
		Map<String, List<FolderTag>> tagGroups = new HashMap<String, List<FolderTag>>();
		CallableStatement cStmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call findTagGroupsInFolder(?,?,?,?);");
			cStmt.setInt(1,folder.getId());
			cStmt.setInt(2,minFreq);
			cStmt.setInt(3,sortBy);
			cStmt.setInt(4,order);			
			ResultSet rs = cStmt.executeQuery();
			while(rs.next()){
				FolderTag ft = new FolderTag();
				Tag memTag = TagDBDao.createNamedTagObject(MEMBER_TAG_COL, rs, true);
				Tag grpTag = TagDBDao.createNamedTagObject(GROUP_TAG_COL, rs, true);
				ft.setTag(memTag);
				ft.setCount(rs.getInt(FOLDER_TAG_IDX_COL+"_count"));
				List<FolderTag> folderTags = tagGroups.get(grpTag.getLabel());
				if(folderTags == null){
					folderTags = new ArrayList<FolderTag>();
					tagGroups.put(grpTag.getLabel(),folderTags);
				}
				folderTags.add(ft);
			}
			if(tagGroups.isEmpty() == false){
				for(List<FolderTag> ftags: tagGroups.values()){
					setFolderOnFolderTags(ftags,folder);
				}
			}			
		}catch(Exception e){		
			logger.fatal(e);
		}finally{
			try {
				DBUtil.cleanup(conn,cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return tagGroups;
	}
	
}
