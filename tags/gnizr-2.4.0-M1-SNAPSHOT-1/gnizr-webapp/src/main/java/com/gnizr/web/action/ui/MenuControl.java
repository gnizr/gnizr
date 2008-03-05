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
package com.gnizr.web.action.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.LoggedInUserAware;
import com.opensymphony.webwork.interceptor.SessionAware;

public class MenuControl extends AbstractAction implements LoggedInUserAware, SessionAware {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750469850579528417L;
	private static final Logger logger = Logger.getLogger(MenuControl.class);
	
	public static final String KEY_TOP_MENU = "gnizr.top.menu";
	public static final String KEY_LINK = "link";
	public static final String KEY_ACTION_NAMESPACE = "actionNamespace";
	public static final String KEY_NAME = "name";
	public static final String KEY_SUB_MENU = "submenu";
	public static final String KEY_USER_AUTH_REQ = "userAuthReq";
	
	private List<MenuItem> menuItems;

	private User loggedInUser;

	private String username;

	private String sourceActionId;

	@SuppressWarnings("unused")
	private Map session;
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected String go() throws Exception {
		if (menuItems == null) {
			readMenuConfig();
			session.put("menuItems", menuItems);
		}
		return SUCCESS;
	}

	public List<MenuItem> getMenuItems() {
		return this.menuItems;
	}

	public void setLoggedInUser(User user) {
		this.loggedInUser = user;
	}

	public User getLoggedInUser() {
		return this.loggedInUser;
	}

	public String getSourceActionId() {
		return this.sourceActionId;
	}

	public void setSourceActionId(String actionId) {
		this.sourceActionId = actionId;
	}

	private void readMenuConfig() {
		menuItems = new ArrayList<MenuItem>();

		Properties config = new Properties();
		try{
			config.load(MenuControl.class.getResourceAsStream("/gnizr-ui-menu.properties"));
		}catch(Exception e){
			logger.error("error reading gnizr-ui-menu.properties");
		}
		String[] topMenu = parseMenuItems(config.getProperty(KEY_TOP_MENU));
		for(int i = 0 ; i < topMenu.length ; i++){
			boolean authReq = getBooleanMenuValue(config, topMenu[i], KEY_USER_AUTH_REQ);
			if(authReq == true && getLoggedInUser() == null){
				continue;
			}
			try{
				MenuItem newItem = getMenuItem(config,topMenu[i]);
				menuItems.add(newItem);
				logger.debug("found menuItem: " + newItem.toString());
			}catch(RuntimeException e){
				
			}
		}		
	}
	
	private MenuItem getMenuItem(Properties config, String menuItemKey){
		String name = getMenuValue(config, menuItemKey,KEY_NAME);
		String link = getMenuValue(config, menuItemKey,KEY_LINK);
		String ns = getMenuValue(config, menuItemKey,KEY_ACTION_NAMESPACE);
				
		MenuItem newItem = new MenuItem();
		newItem.setActionLabel(mapVariable(name));
		newItem.setActionNamespace(ns);
		newItem.setActionPath(mapVariable(link));
		
		String subMenuItems = getMenuValue(config, menuItemKey, KEY_SUB_MENU);
		if(subMenuItems != null){
			String[] subMenu = parseMenuItems(subMenuItems);
			for(int i = 0; i < subMenu.length; i++){
				MenuItem newSubItem = getMenuItem(config, subMenu[i]);
				newItem.addSubItem(newSubItem);
			}
		}		
		return newItem;
	}
	
	private boolean getBooleanMenuValue(Properties config, String menuItemKey, String keyName){	
		String v = getMenuValue(config, menuItemKey, keyName);
		if(v != null){
			return Boolean.parseBoolean(v);
		}
		return false;
	}
	
	private String getMenuValue(Properties config, String menuItemKey, String keyName){
		String name = config.getProperty("gnizr.menu."+menuItemKey+"."+keyName);
		if(name != null){
			return name;
		}
		return null;
	}
	
	private String[] parseMenuItems(String value){
		if(value != null){
			return value.split(",\\s*");
		}
		return new String[0];
	}

	private String mapVariable(String pathExpr) {
		if(pathExpr == null){
			return null;
		}		
		String rPath = pathExpr;
		if (getUsername() != null) {
			rPath = rPath.replaceAll("\\$\\{username\\}", getUsername());
		}else if (getLoggedInUser() != null) {
			rPath = rPath.replaceAll("\\$\\{username\\}", getLoggedInUser().getUsername());
		}
		if(rPath.matches(".*\\$\\{username\\}.*")){
			throw new RuntimeException("unmapped variable found");
		}
		return rPath;
	}

	public void setSession(Map session) {
		this.session = session;		
	}
}
