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
package com.gnizr.db;

public class MIMEType {

	public static final int UNKNOWN = 0;
	public static final int TEXT_XML = 1001;
	public static final int TEXT_PLAIN = 1002;
	public static final int TEXT_HTML = 1003;
	
	public static final int IMG_JPEG = 2001;
	public static final int IMG_PNG = 2002;
	public static final int IMG_TIFF = 2003;
	public static final int IMG_GIF = 2004;
	
	/** <code>application/rss+xml</code> **/
	public static final int APP_RSS_XML = 3001;

	/** <code>application/rdf+xml</code> **/
	public static final int APP_RDF_XML = 3002;
	
	/** <code>application/owl+xml</code> **/
	public static final int APP_OWL_XML = 3003;
}
