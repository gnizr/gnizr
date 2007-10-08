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

public class DaoResult<E> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7106655211775993033L;

	private int size;
	
	private List<E> result;

	public DaoResult(List<E> result, int size){
		this.size = size;
		this.result = result;
	}
	
	public List<E> getResult() {
		return result;
	}

	public int getSize() {
		return size;
	}	
	
}
