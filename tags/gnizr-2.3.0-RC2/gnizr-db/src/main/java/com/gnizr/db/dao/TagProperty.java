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

/**
 * <p>This class provides the representation of a tag property, 
 * which can be used to construct a <code>TagAssertion</code></p>
 * <p>
 * For predefined tag property, see <code>tag_prpt</code> DB table.
 * </p>
 * 
 * @author Harry Chen
 * @since 2.2
 */
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
	
	/**
	 * Flag for system-specific tag property
	 */
	public static final String TYPE_SYSTEM = "system";
	/**
	 * Flag for geospatial-related tag proeprty
	 */
	public static final String TYPE_SPATIAL = "spatial";
	/**
	 * Flag for temporal-related tag property
	 */
	public static final String TYPE_TEMPORAL = "temporal";
	/**
	 * Flag for tag property that doesn't fit other types.
	 */
	public static final String TYPE_DEFAULT = "default";
	
	/**
	 * Creates a new instance of this class
	 */
	public TagProperty(){
		this(-1,null,null,TYPE_DEFAULT,CARDINALITY_ONE_OR_MORE);
	}

	/**
	 * Creates a new instance of this class with defined ID
	 * @param id this <code>TagProperty</code> ID
	 */
	public TagProperty(int id){
		this(id,null,null,TYPE_DEFAULT,CARDINALITY_ONE_OR_MORE);
	}

	/**
	 * Creates a new instance of this class with defined properties.
	 * @param id the ID of this <code>TagProperty</code>
	 * @param nsPrefix the namespace prefix for this property
	 * @param description a text description about this property
	 * @param prptType integer ID code for this property type
	 * @param cardinality integer defines the cardinality of this property
	 */
	public TagProperty(int id, String nsPrefix, String description, String prptType, int cardinality){
		this.id = id;
		this.namespacePrefix = nsPrefix;
		this.description = description;
		this.propertyType = prptType;
		this.cardinality = cardinality;
	}

	/**
	 * Copy constructor for this class
	 * @param tp object to copy from
	 */
	public TagProperty(TagProperty tp){
		this.id = tp.id;		
		this.namespacePrefix = tp.namespacePrefix;
		this.description = tp.description;
		this.propertyType = tp.propertyType;
		this.cardinality = tp.cardinality;
	}

	/**
	 * Gets the cardinality of this property
	 * @return the cardinality of this property
	 */
	public int getCardinality() {
		return cardinality;
	}

	/**
	 * Gets the text description of this property 
	 * @return property text description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the ID of this tag property
	 * @return property ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the namespace prefix of this property
	 * @return namespace prefix string
	 */
	public String getNamespacePrefix() {
		return namespacePrefix;
	}

	/**
	 * Gets the property type ID code for this property
	 * @return property type integer ID
	 */
	public String getPropertyType() {
		return propertyType;
	}

	/**
	 * Sets the cardinality of this property
	 * @param cardinality property cardinality
	 */
	public void setCardinality(int cardinality) {
		this.cardinality = cardinality;
	}
	
	/**
	 * Sets the text description of this property
	 * @param description text description of this property
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Sets the ID of this property
	 * @param id ID value
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Sets the namespace prefix string of this property
	 * @param namespacePrefix namespace prefix string
	 */
	public void setNamespacePrefix(String namespacePrefix) {
		this.namespacePrefix = namespacePrefix;
	}
	
	/**
	 * Sets the type flag of this property type
	 * 
	 * @param propertyType one of the predefined property type integer code
	 */
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
	 * Gets the name  of this property
	 * @return property name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the property name
	 * @param name property name
	 */
	public void setName(String name) {
		this.name = name;
	}
		
	
}
