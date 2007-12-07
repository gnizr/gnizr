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

import org.apache.log4j.Logger;

import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.GnizrDao;
import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.Link;
import com.gnizr.db.dao.link.LinkDao;

public class UpdateMIMETypeListener implements BookmarkListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8878941157300660760L;
	private static final Logger logger = Logger.getLogger(UpdateMIMETypeListener.class);
	
	private LinkDao linkDao;
	
	public UpdateMIMETypeListener(GnizrDao gnizrDao){
		this.linkDao = gnizrDao.getLinkDao();
	}
	
	public void notifyAdded(BookmarkManager bookmarkManager,Bookmark bookmark) throws Exception {
		logger.debug("UpdateMIMETypeListener.notifyAdded: bookmark="+bookmark);
		this.updateLinkMIMEType(bookmark.getLink());		
	}

	public void notifyDeleted(BookmarkManager bookmarkManager,Bookmark bookmark) throws Exception {
		// no code;		
	}

	public void notifyUpdated(BookmarkManager bookmarkManager,Bookmark oldBookmark, Bookmark newBookmark) throws Exception {		
		logger.debug("UpdateMIMETypeListener.notifyUpdate: bookmark="+newBookmark);
		this.updateLinkMIMEType(newBookmark.getLink());
	}
	
	private void updateLinkMIMEType(Link link){
		try{
			if(link != null && link.getMimeTypeId() <= 0){
				logger.debug("detect MIME-TYPE: link="+link);
				String url = link.getUrl();
				int mimeTypeId = GnizrDaoUtil.detectMIMEType(url);
				if(mimeTypeId > 0){					
					logger.debug("update link record: setMimeTypeId = " + mimeTypeId);
					link.setMimeTypeId(mimeTypeId);
					boolean isOkay = linkDao.updateLink(link);
					logger.debug("update is okay: " + isOkay);
				}
			}
		}catch(Exception e){
			
		}
	}

}
