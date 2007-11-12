package com.gnizr.core.web.action.bookmark;

import java.util.ArrayList;
import java.util.List;

import com.gnizr.db.dao.Bookmark;
import com.gnizr.db.dao.PointMarker;

public class GeoBookmark extends Bookmark{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1957208327178341260L;

	private List<PointMarker> pointMarkers = new ArrayList<PointMarker>();

	public GeoBookmark(Bookmark b){
		super(b);
	}
	
	public List<PointMarker> getPointMarkers() {
		return pointMarkers;
	}

	public void setPointMarkers(List<PointMarker> pointMarkers) {
		this.pointMarkers = pointMarkers;
	}
	
	
	
}
