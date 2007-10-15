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
package com.gnizr.core.web.typeconverters;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.gnizr.db.dao.PointMarker;
import com.opensymphony.webwork.util.WebWorkTypeConverter;
import com.opensymphony.xwork.util.TypeConversionException;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

public class GeometryMarkerConverter extends WebWorkTypeConverter{

	private static final GeometryFactory factory = new GeometryFactory();
	
	@Override
	public Object convertFromString(Map ctx, String[] args, Class toType) {
		String gMarkerJson = ((String[])args)[0];
		JSONObject jsonObj = JSONObject.fromObject(gMarkerJson);
		if(toType == PointMarker.class){
			PointMarker pm = new PointMarker(); 	
			pm.setNotes(jsonObj.getString("notes"));
			pm.setId(jsonObj.getInt("id"));
			pm.setMarkerIconId(jsonObj.getInt("iconId"));
			Point pt = parsePoint(jsonObj.getString("point"));
			if(pt != null){
				pm.setPoint(pt);
			}					
			return pm;
		}
		throw new TypeConversionException("can't convert from string to PointMarker");
	}

	@Override
	public String convertToString(Map ctx, Object obj) {
		Map<String,Object> map = new HashMap<String, Object>();
		if(obj instanceof PointMarker){
			PointMarker pm = (PointMarker)obj;
			map.put("id",pm.getId());
			map.put("notes",pm.getNotes());
			map.put("point",pm.getPoint().getX()+","+pm.getPoint().getY());
			map.put("iconId",pm.getMarkerIconId());
		}
		JSONObject json = JSONObject.fromMap(map);
		return json.toString();
	}

	private Point parsePoint(String pv){
		if(pv != null){
			String[] pt = pv.split(",");
			if(pt != null && pt.length == 2){
				double lon = Double.parseDouble(pt[0]);
				double lat = Double.parseDouble(pt[1]);
				return factory.createPoint(new Coordinate(lon,lat));
			}
		}
		return null;
	}
}
