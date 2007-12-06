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

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public abstract class GeometryMarker implements Serializable{

	protected int markerIconId;
	
	protected String notes;
	
	protected Geometry geometry;
	
	protected int id;
	
	protected static final GeometryFactory geometryFactory = new GeometryFactory();
	
	public GeometryMarker(){
		this(-1,null,0,null);
	}
	
	public GeometryMarker(int id, Geometry geom, int markerIconId, String notes){
		this.id = id;
		if(geom != null){
			this.geometry = (Geometry)geom.clone();
		}
		this.markerIconId = markerIconId;
		this.notes = notes;		
	}
	
	public GeometryMarker(GeometryMarker marker){		
		if(marker != null){
			this.id = marker.id;
			this.markerIconId = marker.markerIconId;
			this.notes = marker.notes;
			if(marker.geometry != null){
				this.geometry = (Geometry)marker.geometry.clone();
			}
		}
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public boolean isPoint(){
		if(geometry != null && geometry instanceof Point){
			return true;
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMarkerIconId() {
		return markerIconId;
	}

	public void setMarkerIconId(int markerIconId) {
		this.markerIconId = markerIconId;
	}
	
}
