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
package com.gnizr.core.managers;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.exceptions.UsernameTakenException;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.db.dao.GnizrDao;
import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserStat;
import com.gnizr.db.dao.UserTag;
import com.gnizr.db.dao.tag.TagDao;
import com.gnizr.db.dao.user.UserDao;
import com.gnizr.db.vocab.AccountStatus;

/**
 * Manages gnizr user registration, profile change and authentication, and provide methods to query user created tags. 
 * 
 * <h4>How to instantiate <code>UserManager</code></h4>
 * <code>
 *   import com.gnizr.db.GnizrDao;<br/>
 *   ...<br/>
 *   GnizrDao gnizrDao = ... // create GnizrDao object<br/>
 *   UserManager userManager = new UserManager(gnizrDao);  
 * </code>
 * 
 * <h4>Create a new user account</h4>
 * <code>
 * import com.gnizr.db.AccountStatus; <br/>
 * ....
 *    String username = "newUser";<br/>
 *    String password = "safePass";<br/>
 *    String fullname = "John Smith";<br/>
 *    String email = "johns@example.com";<br/>
 *    int acctStatus = AccountStatus.ACTIVE; <br/>
 *    Date createdOn = ... // create today's date<br/>
 *    User u = new User(username,password,fullname,email,acctStatus,createdOn);   
 *    <br/><br/>
 *    UserManager userManager = ... // create User Manager<br/>
 *    userManager.createUser(u);    
 * </code>
 * 
 * 
 * @author harryc
 *
 */
public class UserManager implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7857173178475565244L;

	private static final Logger logger = Logger.getLogger(UserManager.class
			.getName());

	private UserDao userDao;
	
	private TagDao tagDao;

	/**
	 * Creates a new instance of this class and use the defined DAO object
	 * to access data records in the persistent data store.
	 * 
	 * @param gnizrDao an instantiated DAO object
	 */
	public UserManager(GnizrDao gnizrDao) {
		this.userDao = gnizrDao.getUserDao();
		this.tagDao = gnizrDao.getTagDao();
	}

	/**
	 * Checks whether a user exists in the persistent data store. 
	 * 
	 * @param user an instantiated <code>User</code> object 
	 * whose <code>getUsername()</code> returns a valid username string.
	 * @return <code>true</code> if <code>user</code> exists in the persistent store.
	 * Otherwise, return <code>false</code>.
	 */
	public Boolean hasUser(User user){
		logger.debug("hasUser: username="+user.getUsername());
		if(userDao.findUser(user.getUsername()).isEmpty() == true){
			return false;
		}
		return true;
	}
	
	/**
	 * Creates a new <code>User</code> in the persistent store. All properties
	 * <code>User</code> must be instantiated, except <code>User.getId()</code>. 
	 *  
	 * @param user an instantiated <code>User</code> object
	 * @return <code>true</code> if a new user record is successfully created. Otherwise,
	 * returns <code>false</code>
	 * @throws UsernameTakenException an exception is thrown if a user record of the same
	 * username already existed in the persistent store.
	 */
	public boolean createUser(User user) throws UsernameTakenException {
		logger.debug("createUser: user="+user);
		if(user.getAccountStatus() == null){
			throw new NullPointerException("undefined value: accountStatus");
		}
		if(user.getCreatedOn() == null){
			throw new NullPointerException("undefined value: createdOn ");
		}
		int id = userDao.createUser(user);
		if (id > 0) {
			logger.debug("account created");
			return true;
		}else{
			logger.debug("an user of the same username already exists.");
			throw new UsernameTakenException("uesrname="+user.getUsername());
		}
	}	
	
	/**
	 * Returns the account status code of a user account. This method
	 * first uses <code>User.getId()</code> to lookup an account,
	 * if the value is &lt or == 0, then the method uses <code>User.getUsername()</code>.
	 * If no valid username is defined, an exception is thrown.
	 * 
	 * @param user an instantiated <code>User</code> object.
	 * @return the status code of the user account.
	 * @throws NoSuchUserException an exception is thrown if the input <code>User</code> 
	 * doesn't match any user account record in the persistent store.
	 * @see com.gnizr.db.vocab.AccountStatus
	 */
	public int getAccountStatus(User user) throws NoSuchUserException{
		logger.debug("getAccountStatus: username="+user.getUsername());
		User u = null;
		if(user.getId() <= 0 && user.getUsername() != null){
			u = GnizrDaoUtil.getUser(userDao,user.getUsername());
		}else{
			u = userDao.getUser(user.getId());
		}
		if(u == null){
			throw new NoSuchUserException("no such user: "+user.getUsername());
		}
		return u.getAccountStatus();
	}
	
	/**
	 * Activates an existing user account.
	 * 
	 * @param user an instantiated <code>User</code> object of an existing user record in 
	 * the persistent store.
	 * @return <code>true</code> if the user account is successfuly activated. Otherwise, 
	 * returns <code>false</code>
	 * 
	 * @throws NoSuchUserException an exception is thrown if the input <code>User</code> 
	 * doesn't match any user account record in the persistent store.
	 */
	public boolean activateUserAccount(User user) throws NoSuchUserException{
		logger.debug("activateUserAccount: user="+user);
		User u = null;
		if(user.getId() <= 0 && user.getUsername() != null){
			u = GnizrDaoUtil.getUser(userDao, user.getUsername());						
			if(u == null){
				throw new NoSuchUserException("no such user:"+user.getUsername());
			}
		}else{
			u = new User(user);
		}				
		u.setAccountStatus(AccountStatus.ACTIVE);
		return userDao.updateUser(u);
	}
	
	/**
	 * Disables an existing user account. 
	 * 
	 * @param useran instantiated <code>User</code> object of an existing user record in 
	 * the persistent store.
	 * @return <code>true</code> if the user account is successfuly disabled. Otherwise, 
	 * returns <code>false</code>
	 */
	public boolean disableUserAccount(User user) throws NoSuchUserException{
		logger.debug("disableUserAccount: username="+user);
		User u = null;
		if(user.getId() <= 0 && user.getUsername() != null){
			u = GnizrDaoUtil.getUser(userDao,user.getUsername());
			if(u == null){
				throw new NoSuchUserException("no such user: "+user.getUsername());
			}
		}else{
			u = new User(user);
		}
		u.setAccountStatus(AccountStatus.DISABLED);
		return userDao.updateUser(u);
	}
	
	/**
	 * Returns the <code>User</code> object for a given username. 
	 * 
	 * @param username an instantiated username string.
	 * @return the object that describes the properties of the user account.  
	 * @throws NoSuchUserException an exception is thrown if the input <code>username</code>
	 * doesn't match any user record in the persistent store.
	 */
	public User getUser(String username) throws NoSuchUserException{
		logger.debug("getUser: username="+username);		
		User u = GnizrDaoUtil.getUser(userDao, username);
		if(u == null){
			throw new NoSuchUserException("no such user: "+username);
		}
		return u;
	}
	
	/**
	 * Returns the <code>User</code> object for a given username and password pair. 
	 * 
	 * @param username an instantiated username string.
	 * @param password an instantiated password string.
	 * @return the object that describes the properties of the user account.  
	 * @throws NoSuchUserException an exception is thrown if the input <code>username</code> and
	 * <code>password</code> pair doesn't match any user record in the persistent store.
	 */
	public User getUser(String username, String password) {
		logger.debug("getUser: username="+username+",password=[not shown]");
		List<User> users = userDao.findUser(username, password);
		if(users.isEmpty() == false){
			return users.get(0);
		}
		return null;
	}
	
	/**
	 * Checks whether a user's username and password pair is valid. 
	 * 
	 * @param user an instantiated <code>User</code> object
	 * @return <code>true</code> if the user is authorized. Otherwise,
	 * returns <code>false</code>
	 */
	public boolean isAuthorized(User user){
		User u = getUser(user.getUsername(),user.getPassword());
		return (u != null);
	}
	
	/**
	 * Changes the password of a user.
	 * 
	 * @param user an instantiated <code>User</code> object whose <code>User.getPassword()</code>
	 * is the new password.
	 * @return <code>true</code> if password is successfully changed. Otherwise, returns <code>false</code>
	 * 
	 * @throws NoSuchUserException an exception is thrown if the input <code>User</code>
	 * doesn't match any user record in the persistent store.
	 */
	public boolean changePassword(User user) throws NoSuchUserException{
		logger.debug("changePassword: user="+user);		
		User profile = null;
		if(user.getId() > 0){
			profile = userDao.getUser(user.getId());
		}else{
			profile = getUser(user.getUsername());
		}
		profile.setPassword(user.getPassword());				
		return userDao.updateUser(profile);
	}
	
	/**
	 * Changes the profile of a user (i.e., email and fullname).
	 * 
	 * @param user an instantiated <code>User</code> object
	 * @return <code>true</code> if the profile is successfully changed. Otherwise, 
	 * returns <code>false</code>
	 * @throws NoSuchUserException an exception is thrown if the input <code>User</code>
	 * doesn't match any user record in the persistent store.
	 */
	public boolean changeProfile(User user) throws NoSuchUserException {
		logger.debug("changeProfile: user="+user);		
		User profile = null;
		if(user.getId() > 0){
			profile = userDao.getUser(user.getId());
		}else{
			profile = getUser(user.getUsername());
		}
		profile.setEmail(user.getEmail());
		profile.setFullname(user.getFullname());
		if(user.getPassword() != null){
			profile.setPassword(user.getPassword());
		}else{
			// set to NULL, indicating no password change is required.
			profile.setPassword(null);
		}
		return userDao.updateUser(profile);		
	}

	/**
	 * Deletes an user account from the system. The <code>getId</code> 
	 * of the input <code>user</code> must return a valid user account id.
	 * 
	 * @param user an user account to be deleted
	 * @return <code>true</code> if the given user account is deleted successfully. Returns
	 * <code>false</code> otherwise.
	 * @throws NoSuchUserException An exception is thrown if the system has no record
	 * about <code>user</code>. 
	 */
	public boolean deleteUser(User user) throws NoSuchUserException{
		logger.debug("deleteUser: user="+user);
		if(user.getId() > 0){
			return userDao.deleteUser(user.getId());
		}else{
			User u = getUser(user.getUsername()); 
			if(u != null && u.getId() > 0){
				return userDao.deleteUser(u.getId());
			}
		}
		return false;
	}
	
	/**
	 * Returns a list of all user accounts in the system. 
	 * 
	 * @return all users in the system.
	 */
	public List<User> listUsers(){
		logger.debug("listUsers");
		return userDao.listUsers();		
	}
	
	/**
	 * Return the user statistics of all users in the system. 
	 * @return the user statistics of all users. 
	 */
	public List<UserStat> listUserStats(){
		logger.debug("listUserStats");
		return userDao.listUserStats();
	}

	/**
	 * Returns a list of alphabetically sorted tags of a minimal usage-frequency 
	 * created by a given user.
	 * 
	 * @param user the given user who created tags in the list of <code>UserTag</code>
	 * @param minFreq the mininal usage-frequency used for filtering
	 * @return A non-<code>null</code> list of <code>UserTag</code>
	 * @throws NoSuchUserException An exception is thrown if <code>user</code> doesn't
	 * exist in the system. 
	 */
	public List<UserTag> getTagsSortByAlpha(User user, int minFreq) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		return tagDao.findUserTag(user, minFreq, TagDao.SORT_ALPH);
	}
	
	/**
	 * Returns a list of sorted tags of a minimal usage-frequency 
	 * created by a given user. Tags are sorted based on their usage-frequency (i.e., <code>UserTag.getCount</code>).
	 * 
	 * @param user the given user who created tags in the list of <code>UserTag</code>
	 * @param minFreq the mininal usage-frequency used for filtering
	 * @return A non-<code>null</code> list of <code>UserTag</code>
	 * @throws NoSuchUserException An exception is thrown if <code>user</code> doesn't
	 * exist in the system. 
	 */
	public List<UserTag> getTagsSortByFreq(User user, int minFreq) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao, user);
		return tagDao.findUserTag(user, minFreq, TagDao.SORT_FREQ);
	}
	
	/**
	 * Returns tags of a minimal usage-frequency created by a given user in groups, and sort tags in an alphabetical order.  
	 * 
	 * @param user the given user who created those tags
	 * @param minFreq the minimal usage frequency used for filtering
	 * @return a <code>Map</code> of user tag groups. Keys in the <code>Map</code> are the name of the tag group, and
	 * the values in the <code>Map</code> are the list of member tags in that group.
	 * @throws NoSuchUserException
	 */
	public Map<String, List<UserTag>> getTagGroupsSortByAlpha(User user, int minFreq) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao,user);
		return userDao.listTagGroups(user,minFreq, UserDao.SORT_BY_TAG_ALPHA, UserDao.ASCENDING);
	}
	
	
	/**
	 * Returns tags of a minimal usage-frequency created by a given user in groups, and sort tags based on their
	 * usage-frequency.
	 * 
	 * @param user the given user who created those tags
	 * @param minFreq the minimal usage frequency used for filtering
	 * @return a <code>Map</code> of user tag groups. Keys in the <code>Map</code> are the name of the tag group, and
	 * the values in the <code>Map</code> are the list of member tags in that group.
	 * @throws NoSuchUserException
	 */
	public Map<String, List<UserTag>> getTagGroupsSortByFreq(User user, int minFreq) throws NoSuchUserException{
		GnizrDaoUtil.fillId(userDao,user);
		return userDao.listTagGroups(user,minFreq, UserDao.SORT_BY_TAG_USAGE_FREQ, UserDao.DESCENDING);
	}
}

