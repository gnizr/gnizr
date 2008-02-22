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
package com.gnizr.core.bookmark;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.gnizr.core.folder.FolderManager;
import com.gnizr.core.folder.NoSuchFolderException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.TagUtil;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.MachineTag;
import com.gnizr.db.dao.User;

/**
 * <p>This listener providers the support for adding bookmark to a folder. 
 * When a bookmark is added or updated, this listener checks if the tgas
 * of a given bookmark contains machine tag <code>folder:[folder_name]</code>
 * or <code>gn:folder=[folder_name]</code>. If this machine tag exists,
 * then a reference of this bookmark will be saved into the folder defined by
 * the <code>[folder_name]</code>. The save process will create a new folder
 * if the folder doesn't already exist.</p>
 * <p>Note that this listener doesn't remove bookmarks from folders if 
 * folder machine tags are deleted from the existing bookmark tags.</p>
 * 
 * @author Harry Chen
 *
 */
public class FolderTagListener implements BookmarkListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8697913813121601859L;
	private static final Logger logger = Logger.getLogger(FolderTagListener.class);

	private FolderManager folderManager;
	private static final Pattern fullSyntaxPattern = Pattern.compile("gn:folder=(.*)");
	private static final Pattern altSyntaxPattern = Pattern.compile("folder:(.*)");
	
	public FolderTagListener(FolderManager folderManager){
		this.folderManager = folderManager;
	}
	
	public void notifyAdded(BookmarkManager manager,Bookmark bookmark) throws Exception {
		List<MachineTag> machineTags = bookmark.getMachineTagList();
		List<String> folders = findFolderNames(machineTags);
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(bookmark);
		for(String aFolder : folders){
			User owner = bookmark.getUser();
			if(GnizrDaoUtil.isLegalFolderName(aFolder)){
				boolean[] opOkay = folderManager.addBookmarks(owner,aFolder, bmarks);
				logger.debug("add bookmark="+bookmark.getId() + " to folder="+aFolder + ". # added = " + opOkay);
			}
		}
	}

	public void notifyDeleted(BookmarkManager manager,Bookmark bookmark) throws Exception {
		// no code. 
		// SQL takes care the removal of bookmarks from folder when 
		// a bookmark is permanently removed. 
	}

	public void notifyUpdated(BookmarkManager manager,Bookmark oldBookmark, Bookmark newBookmark) throws Exception {
		List<MachineTag> oldMTags = oldBookmark.getMachineTagList();
		List<MachineTag> newMTags = newBookmark.getMachineTagList();
		
		List<String> oldFolders = findFolderNames(oldMTags);
		List<String> newFolders = findFolderNames(newMTags);
		
		List<String> delFromFolder = doSubtractFolderTags(oldFolders,newFolders);
		List<String> addToFolder = doSubtractFolderTags(newFolders,oldFolders);
		
		List<Bookmark> bmarks = new ArrayList<Bookmark>();
		bmarks.add(newBookmark);
		for(String aFolder : addToFolder){
			User owner = newBookmark.getUser();
			if(GnizrDaoUtil.isLegalFolderName(aFolder)){
				boolean[] opOkay = folderManager.addBookmarks(owner,aFolder,bmarks);
				logger.debug("add bookmark="+newBookmark.getId() + " to folder="+aFolder + ". # added = " + opOkay);
			}
		}
		
		bmarks = new ArrayList<Bookmark>();
		bmarks.add(oldBookmark);
		for(String aFolder : delFromFolder){
			User owner = oldBookmark.getUser();
			try{
				boolean[] opOkay = folderManager.removeBookmarks(owner,aFolder,bmarks);
				logger.debug("delete bookmark="+newBookmark.getId() + " from folder="+aFolder + ". # deleted = " + opOkay);
			}catch(NoSuchFolderException e){
				logger.debug("no such folder: " + aFolder +". folder was removed while folder: tag exists");
			}			
		}
		
	}
	
	private List<String> doSubtractFolderTags(List<String> minuend, List<String> subtrahend){
		List<String> difference = new ArrayList<String>();
		for(String m : minuend){
			if(subtrahend.contains(m) == false){
				difference.add(m);
			}
		}
		return difference;
	}
	
	private boolean isSystemFolder(String f){
		if(f != null){
			if(FolderManager.MY_BOOKMARKS_LABEL.equals(f) || 
		       FolderManager.IMPORTED_BOOKMARKS_LABEL.equals(f)){
				return true;
			}
		}
		return false;
	}
	
	private List<String> findFolderNames(List<MachineTag> machineTags){
		List<String> fnames = new ArrayList<String>();
		for(MachineTag mt : machineTags){
			String mtStr = mt.toString();
			Matcher matcher = altSyntaxPattern.matcher(mtStr);
			String folderName = null;
			if(matcher.matches()){
				String tag = matcher.group(1); 
				if(isSystemFolder(tag) == false){
					folderName = TagUtil.mapUnderscoreToSpace(tag);
				}else{
					folderName = tag;
				}
			}else{
				matcher = fullSyntaxPattern.matcher(mtStr);
				if(matcher.matches()){					
					String tag = matcher.group(1);
					if(isSystemFolder(tag) == false){
						folderName = TagUtil.mapUnderscoreToSpace(tag);
					}else{
						folderName = tag;
					}
				}
			}
			if(folderName != null){
				fnames.add(folderName);
			}
		}
		return fnames;
	}
	
	

}
