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

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

public class PointMarker extends GeometryMarker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6660247624861588585L;
	
	public PointMarker(){
		this(-1,null,0,null);
	}
	
	public PointMarker(int id,Point pt, int markerIconId, String notes){
		super(id,pt,markerIconId,notes);		
	}
	
	public PointMarker(PointMarker pm){
		super(pm);
	}

	public Point getPoint() {
		return (Point)geometry;
	}

	public void setPoint(Point point) {
		this.geometry = point;
	}
	
	public void setPoint(double x, double y){
		if(geometry == null){
			geometry = geometryFactory.createPoint(new Coordinate(x,y));
		}else{
			geometry.getCoordinate().x = x;
			geometry.getCoordinate().y = y;
			geometry.geometryChanged();
		}
	}
	
	public void setX(double x){
		if(geometry == null){
			geometry = geometryFactory.createPoint(new Coordinate(x,0));
		}else{
			geometry.getCoordinate().x = x;
			geometry.geometryChanged();
		}
	}
	
	public void setY(double y){
		if(geometry == null){
			geometry = geometryFactory.createPoint(new Coordinate(0,y));
		}else{
			geometry.getCoordinate().y = y;
			geometry.geometryChanged();
		}
	}
	
	public double getX(){
		if(geometry != null){
			return geometry.getCoordinate().x;
		}
		return 0.0;
	}
	
	public double getY(){
		if(geometry != null){
			return geometry.getCoordinate().y;
		}
		return 0.0;
	}
	
}
