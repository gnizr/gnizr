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
package com.gnizr.core.delicious;

public class ImportStatus {

	private int totalNumber;
	private int numberAdded;
	private int numberUpdated;
	private int numberError;
	
	public int getNumberAdded() {
		return numberAdded;
	}
	public void setNumberAdded(int numberAdded) {
		this.numberAdded = numberAdded;
	}
	public int getNumberError() {
		return numberError;
	}
	public void setNumberError(int numberError) {
		this.numberError = numberError;
	}
	public int getNumberUpdated() {
		return numberUpdated;
	}
	public void setNumberUpdated(int numberUpdated) {
		this.numberUpdated = numberUpdated;
	}
	public int getTotalNumber() {
		return totalNumber;
	}
	public void setTotalNumber(int totalNumber) {
		this.totalNumber = totalNumber;
	}
	
	
}
