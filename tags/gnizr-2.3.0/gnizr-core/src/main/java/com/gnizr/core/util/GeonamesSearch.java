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
package com.gnizr.core.util;

import org.apache.log4j.Logger;
import org.geonames.Toponym;
import org.geonames.ToponymSearchCriteria;
import org.geonames.ToponymSearchResult;
import org.geonames.WebService;

public class GeonamesSearch {

	private static final Logger logger = Logger.getLogger(GeonamesSearch.class);
	
	public static Geo searchGeonames(String geoString) {
		if (geoString == null || geoString.length() == 0) {
			return null;
		}

		String searchString = replaceUnderscoreChar(geoString);

		Geo geo = null;
		try {
			ToponymSearchCriteria searchObj = new ToponymSearchCriteria();
			searchObj.setMaxRows(1);
			searchObj.setQ(searchString);
			logger.debug("search criteria: " + searchObj);
			ToponymSearchResult result = WebService.search(searchObj);
			if (result != null && result.getTotalResultsCount() > 0) {
				Toponym topo = result.getToponyms().get(0);
				geo = new Geo(topo.getLatitude(), topo.getLongitude());
			}
		} catch (Exception e) {
			logger.debug("search geo failed: original geoString=" + geoString
					+ ", actual searchString=" + searchString, e);
		}
		return geo;
	}
	

	/**
	 * Replaces all underscore char '_' with white space char ' '
	 * 
	 * @param geoString
	 *            a string
	 * @return input string with '_' char replaced with ' '
	 */
	private static String replaceUnderscoreChar(String geoString) {
		String s = new String(geoString);
		return s.replaceAll("[_]+"," ");
	}

}
