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

/**
 * <p>This class provides a representation of a <code>GeometryMarker</code> that is a point. A point marker 
 * is consisted of X and Y coordinates. In gnizr, when using this class to describe a geo-location, 
 * the X-value is longitude and the Y-value is the latitude.</p>
 * <p>The ID of a <code>PointMarker</code> is usually assigned by the database system when 
 * a point marker is created for the first time.</p> 
 *  
 * @author Harry Chen
 * @since 2.3
 */
public class PointMarker extends GeometryMarker{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6660247624861588585L;
	
	/**
	 * Creates a new instance of this class.
	 */
	public PointMarker(){
		this(-1,null,0,null);
	}
	
	/**
	 * Creates a new instance of this class with predefined properties.
	 * 
	 * @param id the ID of this object
	 * @param pt the point value
	 * @param markerIconId the ID of the desired marker icon.
	 * @param notes notes about this point marker.
	 */
	public PointMarker(int id,Point pt, int markerIconId, String notes){
		super(id,pt,markerIconId,notes);		
	}
	
	/**
	 * Copy constructor of this class.
	 * 
	 * @param pm object to copy from.
	 */
	public PointMarker(PointMarker pm){
		super(pm);
	}

	/**
	 * Returns the point geometry of this marker.
	 * @return a <code>Point</code> object
	 */
	public Point getPoint() {
		return (Point)geometry;
	}

	/**
	 * Sets the point geometry of this marker
	 * 
	 * @param point a <code>Point</code> object
	 */
	public void setPoint(Point point) {
		this.geometry = point;
	}
	
	/**
	 * Sets the point geometry of this marker by defining the X and Y values.
	 * @param x the X-value of a point
	 * @param y the Y-value of a point
	 */
	public void setPoint(double x, double y){
		if(geometry == null){
			geometry = geometryFactory.createPoint(new Coordinate(x,y));
		}else{
			geometry.getCoordinate().x = x;
			geometry.getCoordinate().y = y;
			geometry.geometryChanged();
		}
	}
	
	/**
	 * Sets the X-value of this point marker
	 * @param x the X-value to set
	 */
	public void setX(double x){
		if(geometry == null){
			geometry = geometryFactory.createPoint(new Coordinate(x,0));
		}else{
			geometry.getCoordinate().x = x;
			geometry.geometryChanged();
		}
	}
	
	/**
	 * Sets the Y-value of this point marker
	 * @param y the Y-value to set
	 */
	public void setY(double y){
		if(geometry == null){
			geometry = geometryFactory.createPoint(new Coordinate(0,y));
		}else{
			geometry.getCoordinate().y = y;
			geometry.geometryChanged();
		}
	}
	
	/**
	 * Returns the X-value of this point marker
	 * @return the X-value of the point. If not defined, returns <code>0.0</code>
	 */
	public double getX(){
		if(geometry != null){
			return geometry.getCoordinate().x;
		}
		return 0.0;
	}
	
	/**
	 * Returns the Y-value of this point marker.
	 * @return the Y-value of the point. If not defined, returns <code>0.0</code>.
	 */
	public double getY(){
		if(geometry != null){
			return geometry.getCoordinate().y;
		}
		return 0.0;
	}
	
}
