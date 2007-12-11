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

/**
 * Represents a gnizr user account in the database. This class serves as a transport object for encapsulating 
 * information about an user account in user DAO operations. 
 * <p>
 * Gnizr user account database impose certain restrictions on user account properties. 
 * <ul>
 * <li>All user accounts must have a unique ID (i.e., positive integer).</li>
 * <li>All user accounts must have a unique username.</li>
 * <li>Email address and full name of an user account must not be <code>null</code>.</li>
 * </ul>
 * </p>
 * <p>User DAO operations usually provide methods to look up an user account information, either by ID or by username. 
 * When a user DAO operation returns an instantiated <code>User</code> object, the password field of the user
 * is always encrypted in a MD5 format. When creating a new user account, the password field 
 * must be set in the <em>plain text</em> format.</p>
 * 
 * @author Harry Chen
 *
 */
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

	/**
	 * Creates a new instance of this class. 
	 */
	public User(){
		// no code
	}
	
	/**
	 * Creates a new instance of this class and sets the ID of this user account.
	 * @param id the ID of this user account
	 */
	public User(int id){
		this.id = id;
	}
	
	/**
	 * Creates a new instance of this class and sets the username of this user account.
	 * @param username the user name of this account
	 */
	public User(String username){
		this();
		this.username = username;
	}
	
	/**
	 * Creates a new instance of this class and sets the username and password of this user account.
	 * @param username the user name of this account
	 * @param password plain text password value
	 */
	public User(String username, String password){
		this();
		this.username = username;
		this.password = password;
	}
	
	/**
	 * Creates a new instance of this class and sets the username, password, full name 
	 * and email of this user account.
	 * 
	 * @param username the user name of this account
	 * @param password plain text password value
	 * @param fullname user full name
	 * @param email email address string
	 */
	public User(String username, String password, String fullname, String email){
		this();
		this.username = username;
		this.password = password;
		this.fullname = fullname;
		this.email = email;
	}
	
	/**
	 * Creates a new instance of this class and sets the username, password, full name, 
	 * email, account status and created-on property of this user account.
	 * @param username the user name of this account
	 * @param password plain text password value
	 * @param fullname user full name
	 * @param email email address string
	 * @param acctStatus a valid status value from {@link com.gnizr.db.vocab.AccountStatus}
	 * @param createdOn when this user account is created
	 */
	public User(String username, String password, String fullname, String email,Integer acctStatus, Date createdOn){
		this(username,password,fullname,email);
		this.accountStatus = acctStatus;
		if(createdOn != null){
			this.createdOn = (Date)createdOn.clone();
		}
	}
	
	/**
	 * Creates a new instance of this class by copying all properties from the input <code>User</code>.
	 * This is a copy-constructor.
	 * @param user
	 */
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
	
	/**
	 * Returns the account status of this user.
	 * @return one of the value from {@link com.gnizr.db.vocab.AccountStatus}
	 */
	public Integer getAccountStatus() {
		return accountStatus;
	}
	
	/**
	 * Returns the date on which this account is created.
	 * @return creation date
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * Returns the email address of the user.
	 * @return email address string 
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns the full name of the user.
	 * @return user full name
	 */
	public String getFullname() {
		return fullname;
	}

	/**
	 * Returns the ID of this user account.
	 * @return valid user ID if it's a positive integer. Otherwise, the ID is either not defined or invalid.  
	 */
	public int getId() {
		return id;
	}

	/**
	 * Returns the password of the user. If this object is returned
	 * from a user DAO operation, this password value will be encrypted in MD5 format.
	 * Otherwise, the password value is in the <em>plain text</em> format.
	 * @return
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Returns the username of this account
	 * @return username string
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the account status of this user account.
	 * 
	 * @param accountStatus one of the values from {@link com.gnizr.db.vocab.AccountStatus}
	 */
	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	/**
	 * Sets the creation date of this user account
	 * @param createdOn when this account is created
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * Sets the email address of this user
	 * 
	 * @param email user email string
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Sets the full name of this user
	 * @param fullname user full name 
	 */
	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	/**
	 * Sets the ID of this user account
	 * 
	 * @param id the ID is undefined or invalid if it's less than or equal to 0.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Sets the password of this user account. 
	 * 
	 * @param password password string in the plain text format.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Sets the username of this user
	 * 
	 * @param username user name string
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Returns a debug text string of this object's property.
	 */
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
