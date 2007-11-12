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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6052228717766333230L;

	private int accountStatus;

	private Date createdOn;

	private String email;

	private String fullname;

	private int id;

	private String password;

	private String username;

	public User(){
		// no code
	}
	
	public User(int id){
		this.id = id;
	}
	
	public User(String username){
		this();
		this.username = username;
	}
	
	public User(String username, String password){
		this();
		this.username = username;
		this.password = password;
	}
	
	public User(String username, String password, String fullname, String email){
		this();
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.email = email;
	}
	
	public User(String username, String password, String fullname, String email,Integer acctStatus, Date createdOn){
		this(username,password,fullname,email);
		this.accountStatus = acctStatus;
		if(createdOn != null){
			this.createdOn = (Date)createdOn.clone();
		}
	}
	
	public User(User user){
		this.id = user.id;		
		this.accountStatus = user.accountStatus;		
		if(user.createdOn != null){
			this.createdOn = (Date)user.createdOn.clone();
		}
		this.email = user.email;
		this.fullname = user.fullname;
		this.password = user.password;
		this.username = user.username;
	}
	
	public Integer getAccountStatus() {
		return accountStatus;
	}
	
	public Date getCreatedOn() {
		return createdOn;
	}

	public String getEmail() {
		return email;
	}

	public String getFullname() {
		return fullname;
	}

	public int getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id",getId());
		map.put("username",getUsername());
		map.put("password", getPassword());
		map.put("acctStatus", getAccountStatus());
		map.put("email",getEmail());
		map.put("fullname",getFullname());
		return map.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + accountStatus;
		result = PRIME * result + ((createdOn == null) ? 0 : createdOn.hashCode());
		result = PRIME * result + ((email == null) ? 0 : email.hashCode());
		result = PRIME * result + ((fullname == null) ? 0 : fullname.hashCode());
		result = PRIME * result + id;
		result = PRIME * result + ((password == null) ? 0 : password.hashCode());
		result = PRIME * result + ((username == null) ? 0 : username.hashCode());
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
		final User other = (User) obj;
		if (accountStatus != other.accountStatus)
			return false;
		if (createdOn == null) {
			if (other.createdOn != null)
				return false;
		} else if (!createdOn.equals(other.createdOn))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (fullname == null) {
			if (other.fullname != null)
				return false;
		} else if (!fullname.equals(other.fullname))
			return false;
		if (id != other.id)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}	
	
}
