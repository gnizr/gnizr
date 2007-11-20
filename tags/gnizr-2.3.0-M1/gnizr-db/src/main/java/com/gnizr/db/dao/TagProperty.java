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

import java.io.Serializable;

public class TagProperty implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3083385796866844705L;
	private int cardinality;
	private String description;
	private int id;
	private String namespacePrefix;
	private String propertyType;
	private String name;
	
	public static final int CARDINALITY_ONE = 1;
	public static final int CARDINALITY_ONE_OR_MORE = -1;
	public static final String TYPE_SYSTEM = "system";
	public static final String TYPE_SPATIAL = "spatial";
	public static final String TYPE_TEMPORAL = "temporal";
	public static final String TYPE_DEFAULT = "default";
	
	public TagProperty(){
		this(-1,null,null,TYPE_DEFAULT,CARDINALITY_ONE_OR_MORE);
	}

	public TagProperty(int id){
		this(id,null,null,TYPE_DEFAULT,CARDINALITY_ONE_OR_MORE);
	}

	public TagProperty(int id, String nsPrefix, String description, String prptType, int cardinality){
		this.id = id;
		this.namespacePrefix = nsPrefix;
		this.description = description;
		this.propertyType = prptType;
		this.cardinality = cardinality;
	}

	public TagProperty(TagProperty tp){
		this.id = tp.id;		
		this.namespacePrefix = tp.namespacePrefix;
		this.description = tp.description;
		this.propertyType = tp.propertyType;
		this.cardinality = tp.cardinality;
	}

	public int getCardinality() {
		return cardinality;
	}

	public String getDescription() {
		return description;
	}

	public int getId() {
		return id;
	}

	public String getNamespacePrefix() {
		return namespacePrefix;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setNamespacePrefix(String namespacePrefix) {
		this.namespacePrefix = namespacePrefix;
	}
	
	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + cardinality;
		result = PRIME * result + ((description == null) ? 0 : description.hashCode());
		result = PRIME * result + id;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		result = PRIME * result + ((namespacePrefix == null) ? 0 : namespacePrefix.hashCode());
		result = PRIME * result + ((propertyType == null) ? 0 : propertyType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TagProperty other = (TagProperty) obj;
		if (cardinality != other.cardinality)
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (namespacePrefix == null) {
			if (other.namespacePrefix != null)
				return false;
		} else if (!namespacePrefix.equals(other.namespacePrefix))
			return false;
		if (propertyType == null) {
			if (other.propertyType != null)
				return false;
		} else if (!propertyType.equals(other.propertyType))
			return false;
		return true;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
		
	
}
