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
package com.gnizr.core.vocab;

import com.gnizr.db.dao.MachineTag;

public class MachineTags {

	
	public static final MachineTag GN_FOLDER(String folder){
		String f = folder.replaceAll("\\s+","_");
		return new MachineTag(NS_GNIZR,FOLDER_PRED,f);
	};
	
	public static final MachineTag GN_FOR(String username){
		return new MachineTag(NS_GNIZR,FOR_PRED,username);
	}
	
	public static final MachineTag GN_TAG(String tag){
		return new MachineTag(NS_GNIZR,TAG_PRED,tag);
	}
	
	public static final String NS_GNIZR = "gn";
	public static final String NS_DC = "dc";
	public static final String NS_FOAF = "foaf";
	public static final String NS_VCARD = "v";
	public static final String NS_GEO = "geo";
	public static final String NS_KS = "ks";
	
	public static final String FOR = NS_GNIZR + ":for";
	public static final String FOR_PRED = "for";
	
	public static final String FOLDER = NS_GNIZR + ":folder";
	public static final String FOLDER_PRED = "folder";
	
	public static final String TAG = NS_GNIZR+":tag";
	public static final String TAG_PRED = "tag";
	
	public static final String GEO_LAT = NS_GEO+":lat";
	public static final String GEO_LNG = NS_GEO+":lng";
	
	public static final String DC_DATE = NS_DC+":date";
	public static final String DC_FORMAT = NS_DC+":format";
	
	public static final String VCARD_BDAY = NS_VCARD+":bday";
	public static final String VCARD_POSCODE = NS_VCARD + ":postal-code";
	
	public static final String GNIZR_SUBSCRIBED = "g:subscribed";
	public static final String KS_FEED = "ks:feed";
	
	public static final String GEONAMES = NS_GNIZR+":geonames";
	public static final String GEONAMES_PRED = "geonames";

}
