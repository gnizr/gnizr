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
package com.gnizr.web.action.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.folder.FolderManager;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.web.action.AbstractLoggedInUserAction;
import com.gnizr.web.action.error.ActionErrorCode;

public class ExportNetscapeBookmarks extends AbstractLoggedInUserAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1655282635626280225L;

	private static final Logger logger = Logger.getLogger(ExportNetscapeBookmarks.class);
	
	private FolderManager folderManager;
	
	private File tmpFile;
	
	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return true;
	}

	@Override
	protected String go() throws Exception {
		super.resolveUser();
		
		if(getLoggedInUser() == null){
			logger.error("ExportNetscapeBookmarks: missing LoggedInUser object");
			addActionError(String.valueOf(ActionErrorCode.ERROR_NO_SUCH_USER));
			return ERROR;
		}
		
		logger.debug("ExportBookmark: user = " + getLoggedInUser());
		try{
			this.tmpFile = createTempDataFile();
			this.tmpFile.deleteOnExit();
		}catch(Exception e){
			logger.error("Unable to create temp file for bookmark export. " + e);
			addActionError(String.valueOf(ActionErrorCode.ERROR_INTERNAL));
			return ERROR;
		}
		if(tmpFile != null){
			FileWriter writer = null;
			try{
				writer = new FileWriter(tmpFile,false);
				writeExportData(writer);
			}catch(NoSuchUserException e){
				addActionError(String.valueOf(ActionErrorCode.ERROR_NO_SUCH_USER));
				logger.error("ExportNetscapeBookmarks. no such user = " + getLoggedInUser() + " " + e);
			}catch(IOException e){
				addActionError(String.valueOf(ActionErrorCode.ERROR_IO));
				logger.error("ExportNetscapeBookmarks. IO Error. file: " + tmpFile.getAbsolutePath() 
						+ " " + e);
			}finally{
				if(writer != null){
					writer.close();
				}
			}			
		}else{
			logger.error("Unable to create temp file for bookmark export. TmpFile is null");
			addActionError(String.valueOf(ActionErrorCode.ERROR_INTERNAL));
			return ERROR;
		}
		return SUCCESS;
	}
	
	private void writeExportData(FileWriter fwriter) throws NoSuchUserException, IOException{
		writePreBody(fwriter);
		int offset = 0;
		int ppc = 10;
		DaoResult<Folder> fldRes = folderManager.pageUserFolders(getLoggedInUser(),offset,ppc);
		while(offset < fldRes.getSize()){
			List<Folder> folders = fldRes.getResult();
			for(Folder fld : folders){
				writeFolderContent(fwriter, fld);
				offset++;
			}
			// do checking so to avoid one more DB connection
			if(offset < fldRes.getSize()){				
				fldRes = folderManager.pageUserFolders(getLoggedInUser(),offset,10);
			}else{
				break;
			}
		}
		writePostBody(fwriter);
	}
	
	private void writeFolderContent(FileWriter fwriter, Folder folder) throws IOException, NoSuchUserException{
		fwriter.append("<DT><H3>");
		StringEscapeUtils.escapeHtml(fwriter,folder.getName());
		fwriter.append("</H3>\n");
		if(folder.getDescription() != null && folder.getDescription().length() > 0){
			fwriter.append("<DD>");
			StringEscapeUtils.escapeHtml(fwriter, folder.getDescription());
			fwriter.append("\n");
		}
		int offset = 0;
		int ppc = 100;				
		DaoResult<Bookmark> result = folderManager.pageFolderContent(getLoggedInUser(),
				folder.getName(), offset, ppc);
		fwriter.append("<DL><p>\n");
		while(offset < result.getSize()){
			List<Bookmark> bookmarks = result.getResult();
			for(Bookmark bm : bookmarks){
				writeBookmark(fwriter, bm);
				offset++;
			}
			if(offset < result.getSize()){
				result = folderManager.pageFolderContent(getLoggedInUser(),folder.getName(),offset,ppc);
			}else{
				break;
			}
		}
		fwriter.append("</DL><p>\n");
	}
	
	private void writeBookmark(FileWriter fwriter, Bookmark bookmark) throws IOException{
		fwriter.append("<DT><A HREF=\"");
		fwriter.append(bookmark.getLink().getUrl());
		fwriter.append("\"");
		if(bookmark.getTags().length() > 0){
			fwriter.append(" TAGS=\"");
			fwriter.append(bookmark.getTags());
			fwriter.append("\" ");
		}
		fwriter.append(">");
		StringEscapeUtils.escapeHtml(fwriter, bookmark.getTitle());
		fwriter.append("</A>");
		if(bookmark.getNotes() != null && bookmark.getNotes().length() > 0){
			fwriter.append("<DD>");
			StringEscapeUtils.escapeHtml(fwriter, bookmark.getNotes());
		}
		fwriter.append("\n");
	}
	
	private void writePreBody(FileWriter fwriter) throws IOException{
		fwriter.append("<!DOCTYPE NETSCAPE-Bookmark-file-1>\n");
		fwriter.append("<!-- This is an automatically generated file. DO NOT EDIT! -->\n");
		fwriter.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=UTF-8\">\n");
		fwriter.append("<TITLE>Bookmarks saved by ");
		fwriter.append(getLoggedInUser().getUsername());
		fwriter.append("</TITLE>\n");
		fwriter.append("<H1>Bookmarks saved by ");
		fwriter.append(getLoggedInUser().getUsername());
		fwriter.append("</H1>\n");
		fwriter.append("<DL><p>\n");
	}
	
	private void writePostBody(FileWriter fwriter) throws IOException{
		fwriter.append("</DL><p>");
	}
	
	private File createTempDataFile() throws IOException{
		File tmpDir = null;
		String tmpDirPath = getGnizrConfiguration().getTempDirectoryPath();
		if(tmpDirPath != null){
			tmpDir = new File(tmpDirPath);
			if(tmpDir.exists() == false || tmpDir.isDirectory() == false){
				logger.error("ExportNetscapeBookmarks: tempDirectoryPath doesn't exist " + tmpDirPath);
				tmpDir = null;
			}
		}
		
		StringBuilder filename = new StringBuilder("be");
		filename.append("U");
		filename.append(getLoggedInUser().getId());
		Date now = getNow();
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmm");
		filename.append("T");
		filename.append(format.format(now));
				
		if(tmpDir != null){
			return File.createTempFile(filename.toString(),null,tmpDir);
		}else{
			return File.createTempFile(filename.toString(),null);
		}
	}
	
	public InputStream getNetscapeBookmarkStream(){		
		try{
			if(tmpFile != null && tmpFile.exists()){
				return new FileInputStream(tmpFile);
			}
		}catch(Exception e){
			
		}
		return null;
	}
}
