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

import java.io.Serializable;
import java.util.List;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.DaoResult;
import com.gnizr.db.dao.Folder;
import com.gnizr.db.dao.PointMarker;
import com.gnizr.db.dao.User;

public interface GeometryMarkerDao extends Serializable{

	public int createPointMarker(PointMarker pm);

	public PointMarker getPointMarker(int id);
	
	public boolean updatePointMarker(PointMarker pm);
	
	public boolean deletePointMarker(int id);
	
	public boolean addPointMarker(Bookmark bm, PointMarker ptMarker);
	
	public boolean removePointMarker(Bookmark bm, PointMarker ptMarker);

	public List<PointMarker> listPointMarkers(Bookmark bm);
	
	public DaoResult<Bookmark> pageBookmarksInFolder(Folder folder, int offset, int count);
	
	public DaoResult<Bookmark> pageBookmarksInArchive(User user, int offset, int count);
}
