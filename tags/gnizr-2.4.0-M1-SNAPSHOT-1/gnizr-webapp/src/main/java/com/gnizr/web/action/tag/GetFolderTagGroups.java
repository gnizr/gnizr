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
package com.gnizr.web.action.tag;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.folder.FolderManager;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.FolderTag;
import com.gnizr.web.action.AbstractTagCloudAction;
import com.gnizr.web.action.SessionConstants;

public class GetFolderTagGroups extends AbstractTagCloudAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8331824152141458379L;

	private static final Logger logger = Logger.getLogger(GetFolderTagGroups.class);
	
	private FolderManager folderManager;
	
	// read-only data
	private Map<String, List<FolderTag>> folderTagGroups;
	private Folder folder;
	
	// action parameters
	private String folderName;
	
	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	@Override
	protected String go() throws Exception {
		super.resolveUser();
		logger.debug("GetFolderTagGroups.go()");
		if(folderName != null){
			folder = folderManager.getUserFolder(getUser(), folderName);
			if(folder != null){
				int frq = getMinTagFreq();
				if(getSortBy().equalsIgnoreCase(SessionConstants.SORT_ALPH)){
					folderTagGroups = folderManager.getTagGroupsSortByAlpha(folder, frq, FolderManager.ASCENDING_ORDER);
				}else{
					folderTagGroups = folderManager.getTagGroupsSortByFreq(folder,frq,FolderManager.DESCENDING_ORDER);
				}
			}else{
				logger.debug("no such folder: " + folderName);
				folderTagGroups = null;
			}
		}
		return SUCCESS;
	}

	public Map<String, List<FolderTag>> getFolderTagGroups() {
		return folderTagGroups;
	}

	public Folder getFolder() {
		return folder;
	}

	public FolderManager getFolderManager() {
		return folderManager;
	}

	public void setFolderManager(FolderManager folderManager) {
		this.folderManager = folderManager;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return false;
	}

}
