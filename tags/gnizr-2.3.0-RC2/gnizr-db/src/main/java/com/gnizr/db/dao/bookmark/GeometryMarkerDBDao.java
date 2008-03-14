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
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DBUtil;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;
import com.gnizr.db.vocab.PointMarkerSchema;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ByteOrderValues;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

public class GeometryMarkerDBDao implements GeometryMarkerDao{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3967899988219000744L;
	private static final Logger logger = Logger.getLogger(GeometryMarkerDBDao.class);
	
	private static final WKBWriter wkbWriter  = new WKBWriter(2,ByteOrderValues.LITTLE_ENDIAN);
	private static final WKBReader wkbReader = new WKBReader();
	
	private DataSource dataSource;
		
	public GeometryMarkerDBDao(DataSource ds){
		this.dataSource = ds;
	}
	
	public int createPointMarker(PointMarker pm) {
		logger.debug("createPointMarker: pm=" + pm);
		Connection conn = null;
		CallableStatement cStmt = null;
		int id = -1;
		try{
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call createPointMarker(PointFromWKB(?),?,?,?)");
			byte[] buf = wkbWriter.write(pm.getPoint());
			cStmt.setBytes(1,buf);
			cStmt.setString(2,pm.getNotes());
			cStmt.setInt(3,pm.getMarkerIconId());
			cStmt.registerOutParameter(4,Types.INTEGER);
			cStmt.execute();
			id = cStmt.getInt(4);
		}catch(Exception e){
			logger.error("createPointMarker error",e);
		}finally{
			try{
				DBUtil.cleanup(conn, cStmt);
			}catch(Exception e){
				logger.fatal("createPointMarker DB connection cleanup error",e);
			}
		}
		return id;
	}

	public boolean deletePointMarker(int id) {
		logger.debug("deletePointMarker: id=" + id);
		Connection conn = null;
		PreparedStatement stmt = null;
		Boolean deleted = false;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call deletePointMarker(?);");
			stmt.setInt(1,id);
			if(stmt.executeUpdate() > 0){
				logger.debug("# row deleted=" + stmt.getUpdateCount());
				deleted = true;
			}
		} catch (SQLException e) {
			logger.error("deletePointMarker error",e);
		} finally{
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal("deletePointMarker DB cleanup error",e);
			}
		}
		return deleted;
	}

	public PointMarker getPointMarker(int id) {
		logger.debug("getPointMarker: id=" + id);
		PointMarker marker = null;
		PreparedStatement stmt = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call getPointMaker(?)");
			stmt.setInt(1,id);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()){
				marker = createPointMarkerObject(rs);
			}
		} catch (SQLException e) {
			logger.error("getPointMarker error",e);
		} catch (ParseException e) {
			logger.error("getPointMarker error parsing returned Point geom",e);
		}finally{
			try{
				DBUtil.cleanup(conn, stmt);
			}catch(Exception e){
				logger.error("getPointMarker DB cleanup error",e);
			}
		}
		return marker;
	}

	public boolean updatePointMarker(PointMarker pm) {
		logger.debug("updatePointMarker: pm="+pm);
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean isChanged = false;
		try{
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call updatePointMarker(?,PointFromWKB(?),?,?)");
			stmt.setInt(1,pm.getId());
			byte[] buf = wkbWriter.write(pm.getPoint());
			stmt.setBytes(2, buf);
			stmt.setString(3,pm.getNotes());
			stmt.setInt(4, pm.getMarkerIconId());
			stmt.execute();
			if(stmt.getUpdateCount() > 0){
				isChanged = true;
			}
		}catch(Exception e){
			logger.error("updatePointMarker error",e);
		}finally{
			try{	
				DBUtil.cleanup(conn, stmt);
			}catch(Exception e){
				logger.error("updatePointMarker DB cleanup error",e);
			}
		}
		return isChanged;
	}
	
	public static PointMarker createPointMarkerObject(ResultSet rs) throws ParseException, SQLException{
		if(rs == null) return null;
		PointMarker pm = new PointMarker();
		pm.setId(rs.getInt(PointMarkerSchema.ID));
		pm.setNotes(rs.getString(PointMarkerSchema.NOTES));
		pm.setMarkerIconId(rs.getInt(PointMarkerSchema.ICON_ID));
		
		byte[] buf = rs.getBytes(PointMarkerSchema.GEOM_WKB);
		Point pt = (Point)wkbReader.read(buf);		
		pm.setPoint(pt);				
		return pm;
	}

	public boolean addPointMarker(Bookmark bm, PointMarker ptMarker) {
		logger.debug("addPointMarker: bm=" + bm + ", ptMarker=" + ptMarker);
		Connection conn = null;
		CallableStatement cStmt = null;
		try{
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call addPointMarker(?,?)");
			cStmt.setInt(1,bm.getId());
			cStmt.setInt(2,ptMarker.getId());
			cStmt.execute();	
			return true;
		}catch(Exception e){
			logger.error("addPointMarker error",e);
		}finally{
			try{
				DBUtil.cleanup(conn, cStmt);
			}catch(Exception e){
				logger.fatal("addPointMarker DB connection cleanup error",e);
			}
		}
		return false;
	}

	public boolean removePointMarker(Bookmark bm, PointMarker ptMarker) {
		logger.debug("removePointMarker: bm=" + bm + ", ptMarker=" + ptMarker);
		Connection conn = null;
		CallableStatement cStmt = null;
		try{
			conn = dataSource.getConnection();
			cStmt = conn.prepareCall("call removePointMarker(?,?)");
			cStmt.setInt(1,bm.getId());
			cStmt.setInt(2,ptMarker.getId());
			cStmt.execute();	
			return true;
		}catch(Exception e){
			logger.error("removePointMarker error",e);
		}finally{
			try{
				DBUtil.cleanup(conn, cStmt);
			}catch(Exception e){
				logger.fatal("removePointMarker DB connection cleanup error",e);
			}
		}
		return false;
	}
	
	public List<PointMarker> listPointMarkers(Bookmark bm) {
		PreparedStatement stmt = null;
		Connection conn = null;
		List<PointMarker> pmarks = new ArrayList<PointMarker>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement("call findPointMarkers(?);");
			stmt.setInt(1, bm.getId());
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				PointMarker pm = createPointMarkerObject(rs);
				pmarks.add(pm);
			}
		} catch (Exception e) {
			logger.fatal("getPointMarkers error",e);
		} finally {
			try {
				DBUtil.cleanup(conn, stmt);
			} catch (SQLException e) {
				logger.fatal("getPointMarkers clean up DB connection error",e);
			}
		}
		return pmarks;
	}

	public DaoResult<Bookmark> pageBookmarksInArchive(User user,int offset,int count) {
		logger.debug("pageBookmarkInArchive, input: user="+user + ",offset="+offset+",count="+count);
		Connection conn = null;
		CallableStatement stmt = null;
		DaoResult<Bookmark> result= null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageArcBookmarkHasGeomMarker(?,?,?,?)");
			stmt.setInt(1,user.getId());
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			stmt.registerOutParameter(4,Types.INTEGER);
			ResultSet rs = stmt.executeQuery();
			int size = stmt.getInt(4);
			if(size < 0){
				size = 0;
			}
			while(rs.next()){
				Bookmark b = BookmarkDBDao.createBookmarkObject2(rs);
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

	public DaoResult<Bookmark> pageBookmarksInFolder(Folder folder, int offset, int count) {
		logger.debug("pageBookmarksInFolder, input: folder="+folder + ",offset="+offset+",count="+count);
		Connection conn = null;
		CallableStatement stmt = null;
		DaoResult<Bookmark> result= null;
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareCall("call pageFlrBookmarkHasGeomMarker(?,?,?,?)");
			stmt.setInt(1,folder.getId());
			stmt.setInt(2,offset);
			stmt.setInt(3,count);
			stmt.registerOutParameter(4,Types.INTEGER);
			ResultSet rs = stmt.executeQuery();
			int size = stmt.getInt(4);
			if(size < 0){
				size = 0;
			}
			while(rs.next()){
				Bookmark b = BookmarkDBDao.createBookmarkObject2(rs);
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
