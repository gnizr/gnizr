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
import java.util.List;

/**
 * <p>This class holds the search or paging results. It
 * augments the basic semantic of a Java <code>List</code> with an extra attribute
 * <code>size</code>, which defines the maximum number of items that can be fetched 
 * from a search or a paging operation.</p> 
 * <p>
 * The total number of result items in this object may or may not equal
 * to <code>size</code>. The list of result items usually represent
 * a partial list of result items that are fetched from a search or paging
 * operation. 
 * </p>
 * 
 * @author Harry Chen
 *
 * @param <E> Type of object that this class holds.
 * @since 2.2
 */
public class DaoResult<E> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7106655211775993033L;

	private int size;
	
	private List<E> result;

	/**
	 * Creates a new instance of this class with a defined list of 
	 * search or paging results and the maximum number of items 
	 * can be fetched from the search or paging operation.
	 * @param result a list of result items to be wrapped by this class
	 * @param size the maximum number of items that can be fetched 
	 * from a search or paging operation. It may be equal to or 
	 * greater than <code>result.getSize()</code>. 
	 */
	public DaoResult(List<E> result, int size){
		this.size = size;
		this.result = result;
	}
	
	/**
	 * Gets the list of result items wrapped in this class.
	 * 
	 * @return a list of result items. May return <code>null</code> if
	 * <code>result</code> is not instantiated when it's passed into the 
	 * constructor of this class.
	 */
	public List<E> getResult() {
		return result;
	}

	/**
	 * Returns the maximum number of items can be fetched from a
	 * search or paging operation. The value is exactly what
	 * is passed into the <code>size</code> value of the 
	 * class constructor.
	 * 
	 * @return the maximum item number
	 */
	public int getSize() {
		return size;
	}	
	
}
