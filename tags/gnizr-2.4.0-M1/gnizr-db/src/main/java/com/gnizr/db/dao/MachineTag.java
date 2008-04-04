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

import org.apache.log4j.Logger;


/**
 * <p>This class provides an representation of machine tag. A machine tag is consists of three parts:
 * the namespace URL prefix of the predicate, predicate name and value. </p> 
 * <h4>A machine tag example:</h5>
 * <pre>
 *  dc:name=bob.smith
 *  
 *  - namespace URL prefix: dc
 *  - predicate name: name
 *  - value: bob.smith
 * </pre>
 * <p>
 * The namespace URL prefix is optionally. If not defined, the default prefix <code>gn</code> is assumed. 
 * </p>
 * @author Harry Chen
 * @since 2.2
 *
 */
public class MachineTag {

	private static final Logger logger = Logger.getLogger(MachineTag.class);
	private String nsPrefix;
	private String predicate;
	private String value;
	
	/**
	 * Creates a new instance of this class with undefined namespace URL prefix,
	 * predicate name and value.
	 */
	public MachineTag(){
		this(null,null,null);
	}
	
	/**
	 * Creates a new instance of this class.
	 * @param nsPrefix the namespace URL prefix of the predicate
	 * @param predicate the predicate name
	 * @param value the value of this machine tag
	 */
	public MachineTag(String nsPrefix, String predicate, String value){
		logger.debug("machineTag: nsPrefix="+nsPrefix +",predicate="+predicate+",value="+value);		
		if(nsPrefix != null){
			this.nsPrefix = nsPrefix;
		}else{
			this.nsPrefix = "gn";
		}
		this.predicate = predicate;
		this.value = value;
	}

	/**
	 * Returns the namespace URL prefix of the predicate 
	 * @return the NS predicate string
	 */
	public String getNsPrefix() {
		return nsPrefix;
	}

	/**
	 * Returns the string name of the predicate
	 * @return the predicate name string
	 */
	public String getPredicate() {
		return predicate;
	}

	/**
	 * Return the value string of this machine tag
	 * 
	 * @return the value string
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * Returns a string representation of this machine tag.
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		if(nsPrefix != null){
			sb.append(nsPrefix);
			sb.append(":");
		}
		if(predicate != null && value != null){
			sb.append(predicate);
			if(nsPrefix == null){
				sb.append(":");
			}
		
			if(sb.charAt(sb.length()-1) != ':'){
				sb.append("=");
			}			
			if(value.matches("\\s")){
				sb.append("\"");
				sb.append(value);
				sb.append("\"");
			}else{
				sb.append(value);
			}			
		}else{
			return "";
		}
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((nsPrefix == null) ? 0 : nsPrefix.hashCode());
		result = PRIME * result + ((predicate == null) ? 0 : predicate.hashCode());
		result = PRIME * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final MachineTag other = (MachineTag) obj;
		if (nsPrefix == null) {
			if (other.nsPrefix != null)
				return false;
		} else if (!nsPrefix.equals(other.nsPrefix))
			return false;
		if (predicate == null) {
			if (other.predicate != null)
				return false;
		} else if (!predicate.equals(other.predicate))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
}
