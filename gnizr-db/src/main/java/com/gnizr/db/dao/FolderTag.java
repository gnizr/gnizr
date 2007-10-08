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
package com.gnizr.db.dao;

import java.io.Serializable;

public class FolderTag implements Serializable, TagLabel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2602319113999806063L;

	private Folder folder;
	private Tag tag;
	private int count;
	
	public FolderTag(){
		this(null,null,0);
	}
	
	public FolderTag(Folder f, Tag t, int count){
		this.folder = f;
		this.tag = t;
		this.count = count;
	}
	
	public FolderTag(FolderTag folderTag){
		if(folderTag != null){
			if(folderTag.getFolder() != null){
				this.folder = new Folder(folderTag.getFolder());
			}
			if(folderTag.getTag() != null){
				this.tag = new Tag(folderTag.getTag());
			}
			this.count = folderTag.getCount();
		}
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public Folder getFolder() {
		return folder;
	}
	public void setFolder(Folder folder) {
		this.folder = folder;
	}
	public Tag getTag() {
		return tag;
	}
	public void setTag(Tag tag) {
		this.tag = tag;
	}

	public String getLabel() {
		if(tag != null){
			return tag.getLabel();
		}
		return null;
	}
}
