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
package com.gnizr.db.dao.link;

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
import com.gnizr.db.dao.Link;
import com.gnizr.db.vocab.LinkSchema;

public class LinkDBDao implements LinkDao{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3670309014246507954L;

	private static final Logger logger = Logger.getLogger(LinkDBDao.class.getName());
	private DataSource dataSource;
	
	
	public LinkDBDao(DataSource ds){
		logger.debug("created LinkDBDao. dataSource=" + ds.toString());
		this.dataSource = ds;
	}
	public int createLink(Link link) {
		logger.debug("input: link="+link);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call createLink(?,?,?)}");
			cStmt.setString(1,link.getUrl());
			cStmt.setInt(2,link.getMimeTypeId());
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

	public boolean deleteLink(int id) {
		logger.debug("input: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deleteLink(?)");
			stmt.setLong(1,id);
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

	public List<Link> findLink(String url) {
		logger.debug("input: url=" + url);
		List<Link> links = new ArrayList<Link>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findLinkUrl(?)");
			stmt.setString(1,url);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Link aLink = createLinkObject(rs);
				links.add(aLink);
				logger.debug("found: " + aLink);
			}
			if(links.size() == 0){
				logger.debug("found no matching links");
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
		return links;
	}

	public static Link createLinkObject(ResultSet rs) throws SQLException {
		if(rs == null) return null;
		Link aLink = new Link();
		aLink.setId(rs.getInt(LinkSchema.ID));
		aLink.setUrl(rs.getString(LinkSchema.URL));
		aLink.setMimeTypeId(rs.getInt(LinkSchema.MIME_TYPE_ID));
		aLink.setUrlHash(rs.getString(LinkSchema.URL_HASH));
		aLink.setCount(rs.getInt(LinkSchema.COUNT));
		return aLink;
	}
	public Link getLink(int id) {
		logger.debug("getLink: id="+id);
		Link aLink = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getLink(?);");
			stmt.setLong(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				aLink = createLinkObject(rs);
				logger.debug("found: " + aLink);
			}else{
				logger.debug("found no matching links");
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
		return aLink;
	}

	public boolean updateLink(Link link) {
		logger.debug("input: link="+link);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updateLink(?,?,?)");
			stmt.setLong(1,link.getId());
			stmt.setString(2,link.getUrl());
			stmt.setInt(3,link.getMimeTypeId());			
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
	/*
	public List<Link> pageLink(Tag tag, int offset, int count) {
		logger.debug("pageLink, input: tag="+tag
				+",offset="+offset+",count="+count);
		Connection conn = null;
		PreparedStatement stmt = null;
		List<Link> links = new ArrayList<Link>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call pageLinkTagId(?,?,?)");
			stmt.setLong(1,tag.getId());
			stmt.setLong(2,offset);
			stmt.setInt(3,count);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Link aLink = createLinkObject(rs);
				logger.debug("found link="+aLink);
				links.add(aLink);
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
		return links;
	}
	*/
	/*
	public int getLinkCount(Tag tag) {
		logger.debug("getLinkCount, input: tag="+tag);
		Connection conn = null;
		CallableStatement cStmt = null;
		int count = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call getLinkCountTagId(?,?)}");
			cStmt.setLong(1,tag.getId());
			cStmt.registerOutParameter(2,Types.INTEGER);
			cStmt.execute();
			count = cStmt.getInt(2);
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return count;
	}
	*/
	/*
	public Link getLinkViewRecord(int id) {
		logger.debug("getLinkViewRecord: id="+id);
		Link aLink = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try{						
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getLinkViewRecord(?);");
			stmt.setLong(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				aLink = createLinkObject(rs);
				logger.debug("found: " + aLink);
			}else{
				logger.debug("found no matching links");
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
		return aLink;
	}
	*/
	public List<Link> findLinkByUrlHash(String urlHash) {
		logger.debug("findLinkByUrlHash: url=" + urlHash);
		List<Link> links = new ArrayList<Link>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findLinkUrlHash(?)");
			stmt.setString(1,urlHash);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Link aLink = createLinkObject(rs);
				links.add(aLink);
				logger.debug("found: " + aLink);
			}
			if(links.size() == 0){
				logger.debug("found no matching links");
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
		return links;
	}
	
	/*
	public int getTagSearchResultCount(String searchQuery) {
		logger.debug("getTagSearchResultCount input: searchQuery="+searchQuery);
		Connection conn = null;
		CallableStatement cStmt = null;
		int count = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call getLinkCountTagSearch(?,?)}");
			cStmt.setString(1,searchQuery);
			cStmt.registerOutParameter(2,Types.INTEGER);
			cStmt.execute();
			count = cStmt.getInt(2);
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return count;
	}
	*/
	/*
	public List<Link> pageTagSearch(String searchQuery, int offset, int count) {
		logger.debug("pageTagSearch: searchQuery=" + searchQuery +", offset="+offset+",count="+count);
		List<Link> links = new ArrayList<Link>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call pageLinkTagSearch(?,?,?)");
			stmt.setString(1,searchQuery);
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Link aLink = createLinkObject(rs);
				links.add(aLink);
				logger.debug("found: " + aLink);
			}
			if(links.size() == 0){
				logger.debug("found no matching links");
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
		return links;
	}
		*/
	/*
	public int getTextSearchResultCount(String searchQuery) {
		logger.debug("getTextSearchResultCount input: searchQuery="+searchQuery);
		Connection conn = null;
		CallableStatement cStmt = null;
		int count = -1;
		try {
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("{call getLinkCountTextSearch(?,?)}");
			cStmt.setString(1,searchQuery);
			cStmt.registerOutParameter(2,Types.INTEGER);
			cStmt.execute();
			count = cStmt.getInt(2);
		} catch (SQLException e) {
			logger.fatal(e);
		} finally{
			try {
				DBUtil.cleanup(conn, cStmt);
			} catch (SQLException e) {
				logger.fatal(e);
			}
		}
		return count;
	}
	*/
	/*
	public List<Link> pageTextSearch(String searchQuery, int offset, int count) {
		logger.debug("pageTextSearch: searchQuery=" + searchQuery +", offset="+offset+",count="+count);
		List<Link> links = new ArrayList<Link>();
		PreparedStatement stmt = null;
		Connection conn = null;
		try{				
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call pageLinkTextSearch(?,?,?)");
			stmt.setString(1,searchQuery);
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				Link aLink = createLinkObject(rs);
				links.add(aLink);
				logger.debug("found: " + aLink);
			}
			if(links.size() == 0){
				logger.debug("found no matching links");
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
		return links;
	}
	*/
}
