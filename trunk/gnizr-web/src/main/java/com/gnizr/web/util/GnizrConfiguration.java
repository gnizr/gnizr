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
package com.gnizr.web.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>Holds the configuration of a gnizr web application. An instance of this class 
 * is usually created via Spring IoC in <code>gnizr-config.xml</code>. In this XML file,
 * the administrator defines the local settings for the gnizr web application. 
 * </p>  
 * <p>
 * When extending gnizr web application, developers are encouraged to extend 
 * this class to define addition configurations that are specific to the extended web application. 
 * The advantage of sub-classing configuration from this class is that it allows 
 * all settings of a gnizr web application to be grouped in a single <code>gnizr-config.xml</code>.
 * </p>
 * <p>
 * For example, if you want to introduce a new application configuration parameter: <code>foo</code>. 
 * You can create a subclass call <code>MyOwnGnizrConfiguration</code>:
 * </p>
 * <pre>
 * package org.example;
 * public class MyOwnGnizrConfiguration extends GnizrConfiguration{
 *   private String foo;
 *   
 *   public void setFoo(String fooValue){
 *     this.foo = fooValue;
 *   }
 *   
 *   public String getFoo(){
 *     return this.foo;
 *   }
 * }
 * </pre>
 * <p>In <code>gnizr-config.xml</code>, you can define the bean object as the follows:</p>
 * <pre>
 * &lt;bean id="gnizrConfiguration" 
 *          class="org.example.MyOwnGnizrConfiguration" singleton="true"&gt;
 *    &lt;!-- configure standard gnizr settings --&gt;
 *    ...
 *    ...
 *    &lt;!-- configure my application settings --&gt;
 *    &lt;property name=\&quot;foo\&quot;&gt;
 *       &lt;value&gt;[foo value goes here]&lt;/value&gt;
 *    &lt;/property&gt;
 * &lt;/bean&gt;		  
 * </pre>
 * @author Harry Chen
 *
 */
public class GnizrConfiguration implements Serializable {
	
	private static final long serialVersionUID = 3110706144455468541L;
	
	public static final String WEB_APPLICATION_URL = "webApplicationUrl";
	public static final String GOOGLE_MAPS_KEY = "googleMapsKey";
	public static final String ENABLE_RSS_ROBOT = "enableRssRobot";
	public static final String SNAPSHOTS_KEY = "snapShotsKey";	
	public static final String ANONYMOUS_READER_POLICY = "anonymousReaderPolicy";
	public static final String REGISTRATION_POLICY = "registrationPolicy";
	public static final String SERVER_MAINTENANCE = "serverMaintenanceModeEnabled";
	public static final String OPENSEARCH_SERVICES = "openSearchServices";

	private String siteName;
	private String siteDescription;
	private String siteContactEmail;
	private String webApplicationUrl;
	private String googleMapsKey;
	private String snapShotsKey;
	private String anonymousReaderPolicy;
	private String registrationPolicy;
	private String tempDirectoryPath;
	private boolean serverMaintenanceModeEnabled;
	private List<String> openSearchServices;
	
	/**
	 * Creates an instance of this class to hold
	 * gnizr system configuration properties.
	 *
	 */
	public GnizrConfiguration(){
		openSearchServices = new ArrayList<String>();
	}
	
	public String getWebApplicationUrl() {
		return webApplicationUrl;
	}

	public void setWebApplicationUrl(String webApplicationUrl) {
		this.webApplicationUrl = webApplicationUrl;
	}

	public String getGoogleMapsKey() {
		return googleMapsKey;
	}

	public void setGoogleMapsKey(String googleMapsKey) {
		this.googleMapsKey = googleMapsKey;
	}

	public String getSnapShotsKey() {
		return snapShotsKey;
	}

	public void setSnapShotsKey(String snapShotsKey) {
		this.snapShotsKey = snapShotsKey;
	}

	public String getAnonymousReaderPolicy() {
		return anonymousReaderPolicy;
	}

	public void setAnonymousReaderPolicy(String anonymousReaderPolicy) {
		this.anonymousReaderPolicy = anonymousReaderPolicy;
	}

	public String getRegistrationPolicy() {
		return registrationPolicy;
	}

	public void setRegistrationPolicy(String registrationPolicy) {
		this.registrationPolicy = registrationPolicy;
	}

	public boolean isServerMaintenanceModeEnabled() {
		return serverMaintenanceModeEnabled;
	}

	public void setServerMaintenanceModeEnabled(boolean serverMaintenanceModeEnabled) {
		this.serverMaintenanceModeEnabled = serverMaintenanceModeEnabled;
	}

	public List<String> getOpenSearchServices() {
		return openSearchServices;
	}

	public void setOpenSearchServices(List<String> openSearchServices) {
		this.openSearchServices = openSearchServices;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getSiteDescription() {
		return siteDescription;
	}

	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}

	public String getSiteContactEmail() {
		return siteContactEmail;
	}

	public void setSiteContactEmail(String siteContactEmail) {
		this.siteContactEmail = siteContactEmail;
	}

	public String getTempDirectoryPath() {
		return tempDirectoryPath;
	}

	public void setTempDirectoryPath(String tempDirectoryPath) {
		this.tempDirectoryPath = tempDirectoryPath;
	}
}
