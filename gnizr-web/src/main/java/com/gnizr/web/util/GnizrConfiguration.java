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
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

public class GnizrConfiguration implements Serializable, InitializingBean {

	private static final Logger logger = Logger.getLogger(GnizrConfiguration.class);
	
	private static final long serialVersionUID = 3110706144455468541L;
	
	public static final String WEB_APPLICATION_URL = "webApplicationUrl";
	public static final String GOOGLE_MAPS_KEY = "googleMapsKey";
	public static final String ENABLE_RSS_ROBOT = "enableRssRobot";
	public static final String SNAPSHOTS_KEY = "snapShotsKey";	
	public static final String ANONYMOUS_READER_POLICY = "anonymousReaderPolicy";
	public static final String REGISTRATION_POLICY = "registrationPolicy";
	public static final String SERVER_MAINTENANCE = "serverMaintenanceModeEnabled";
	public static final String OPENSEARCH_SERVICES = "openSearchServices";
	
	private Properties gnizrProperties;

	/**
	 * Creates an instance of this class to hold
	 * gnizr system configuration properties.
	 *
	 */
	public GnizrConfiguration(){
		gnizrProperties = new Properties();
	}
	
	/**
	 * Returns the <code>Properties</code> object that holds the
	 * gnizr system configuration properties.
	 * 
	 * @return an instantiated <code>Properties</code> object
	 * that holds the current system configruation
	 */
	public Properties getGnizrProperties() {
		return gnizrProperties;
	}

	/**
	 * Sets the <code>Properties</code> object that defines
	 * gnizr system configuration properties. Old properties
	 * will be replaced with <code>gnizrProperties</code>
	 * 
	 * @param gnizrProperties new system configuration properties
	 */
	public void setGnizrProperties(Properties gnizrProperties) {
		this.gnizrProperties = gnizrProperties;
	}

	public void setOpenSearchServices(List<String> serviceUrl){
		this.gnizrProperties.put(OPENSEARCH_SERVICES, serviceUrl);
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getOpenSearchServices(){
		return (List<String>)gnizrProperties.get(OPENSEARCH_SERVICES);
	}
	
	
	/**
	 * Sets the URL of this gnizr web application. If this 
	 * paramater is defined, the system will use it to generate
	 * URL full path for various links. If a gnizr web application
	 * deployment is installed behind a firewall or on a J2EE container
	 * whose hostname is different from the one that is seen by client browser,
	 * set this URL paramater. 
	 * <pre>
	 *  http://host.domain.com
	 * </pre>
	 * 
	 * @param url the web application URL
	 */
	public void setWebApplicationUrl(String url){
		gnizrProperties.put(WEB_APPLICATION_URL,url);
	}
	
	/**
	 * Returns the URL of this gnizr web application. 
	 * @return a URL string. If not defined, returns <code>null</code>.
	 */
	public String getWebApplicationUrl(){
		return gnizrProperties.getProperty(WEB_APPLICATION_URL);
	}
	
	/**
	 * Sets the key string of Google Maps API Key
	 * @param key key string
	 */
	public void setGoogleMapsKey(String key){
		gnizrProperties.put(GOOGLE_MAPS_KEY, key);
	}

	/**
	 * Returns a string to be used as the Google Map API key
	 * 
	 * @return a key string. Returns <code>null</code> if no key is set.
	 */
	public String getGoogleMapsKey(){
		return gnizrProperties.getProperty(GOOGLE_MAPS_KEY);
	}
	
	/**
	 * Sets a flag to define whether the service provided
	 * by gnizr RSS robot should be turned on by default.
	 * 
	 * @param enabled Set <code>true</code> if the service should be 
	 * turned by default. Set <code>false</code>, otherwise.
	 */
	public void setEnableRssRobot(boolean enabled){
		gnizrProperties.put(ENABLE_RSS_ROBOT,Boolean.toString(enabled));
	}
	
	/**
	 * Returns the flag that defines whether the service 
	 * provided by gnizr RSS robot should be turned on by default.
	 * 
	 * @return <code>true</code> if the service is turned by default. Returns
	 * <code>false</code>, otherwise.
	 */
	public boolean isEnableRssRobot(){
		String flag = gnizrProperties.getProperty(ENABLE_RSS_ROBOT,Boolean.toString(false));
		return Boolean.parseBoolean(flag);
	}
	
	
	public String getSnapShotsKey(){
		return gnizrProperties.getProperty(SNAPSHOTS_KEY);
	}
	
	public void setSnapShotsKey(String key){
		gnizrProperties.put(SNAPSHOTS_KEY,key);
	}
	
	
	public void setAnonymousReaderPolicy(String policy){
		gnizrProperties.put(ANONYMOUS_READER_POLICY,policy);
	}
	
	public String getAnonymousReaderPolicy(){
		return gnizrProperties.getProperty(ANONYMOUS_READER_POLICY);
	}
	
	public void setRegistrationPolicy(String policy){
		gnizrProperties.setProperty(REGISTRATION_POLICY,policy);
	}
	
	public String getRegistrationPolicy(){
		return gnizrProperties.getProperty(REGISTRATION_POLICY);
	}
	
	
	public boolean isServerMaintenanceModeEnabled(){
		String flag = gnizrProperties.getProperty(SERVER_MAINTENANCE,Boolean.toString(false));
		return Boolean.parseBoolean(flag);
	}
	
	public void setServerMaintenanceModeEnabled(boolean enabled){
		gnizrProperties.put(SERVER_MAINTENANCE,Boolean.toString(enabled));
	}
	
	
	/**
	 * Called by Spring to initialize this class when it is created
	 * for the first.
	 */
	public void afterPropertiesSet() throws Exception {
		for(Object key : gnizrProperties.keySet()){
			if(gnizrProperties.get(key) instanceof String){
				System.setProperty((String)key, (String)gnizrProperties.get(key));
				logger.debug("set system property: key="+key+",value="+gnizrProperties.get(key));
			}else{
				logger.debug("skip system property: key="+key + " value is not a String");
			}
		}
	}
	
	
	
}
