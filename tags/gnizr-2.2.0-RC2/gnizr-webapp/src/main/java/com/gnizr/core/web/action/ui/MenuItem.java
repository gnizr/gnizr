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
package com.gnizr.core.web.action.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1244843249659050668L;

	private String actionPath;
	private String actionLabel;
	private String actionNamespace;
	
	private List<MenuItem> subItems;
	
	public MenuItem(){
		subItems = new ArrayList<MenuItem>();
		actionPath = "";
		actionLabel = "undef";
	}

	public String getActionLabel() {
		return actionLabel;
	}

	public void setActionLabel(String actionLabel) {
		this.actionLabel = actionLabel;
	}

	public String getActionPath() {
		return actionPath;
	}

	public void setActionPath(String actionPath) {
		this.actionPath = actionPath;
	}

	public List<MenuItem> getSubItems() {
		return subItems;
	}

	public void setSubItems(List<MenuItem> subItems) {
		this.subItems = subItems;
	}
	
	public void addSubItem(MenuItem item){
		this.subItems.add(item);
	}

	public boolean removeSubItem(MenuItem item){
		return this.subItems.remove(item);
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((actionNamespace == null) ? 0 : actionNamespace.hashCode());
		result = PRIME * result + ((actionLabel == null) ? 0 : actionLabel.hashCode());
		result = PRIME * result + ((actionPath == null) ? 0 : actionPath.hashCode());
		result = PRIME * result + ((subItems == null) ? 0 : subItems.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MenuItem other = (MenuItem) obj;
		if (actionNamespace == null) {
			if (other.actionNamespace != null)
				return false;
		} else if (!actionNamespace.equals(other.actionNamespace))
			return false;
		if (actionLabel == null) {
			if (other.actionLabel != null)
				return false;
		} else if (!actionLabel.equals(other.actionLabel))
			return false;
		if (actionPath == null) {
			if (other.actionPath != null)
				return false;
		} else if (!actionPath.equals(other.actionPath))
			return false;
		if (subItems == null) {
			if (other.subItems != null)
				return false;
		} else if (!subItems.equals(other.subItems))
			return false;
		return true;
	}

	public String getActionNamespace() {
		return actionNamespace;
	}

	public void setActionNamespace(String actionId) {
		this.actionNamespace = actionId;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("label:" + getActionLabel());
		sb.append(",namespace="+getActionNamespace());
		sb.append(",path="+getActionPath());
		return sb.toString();
	}
}
