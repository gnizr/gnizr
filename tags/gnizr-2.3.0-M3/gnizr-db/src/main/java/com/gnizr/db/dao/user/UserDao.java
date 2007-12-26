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
package com.gnizr.db.dao.user;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.gnizr.db.dao.User;
import com.gnizr.db.dao.UserStat;
import com.gnizr.db.dao.UserTag;

public interface UserDao extends Serializable{
	public static final int SORT_BY_TAG_USAGE_FREQ = 2;
	public static final int SORT_BY_TAG_ALPHA = 1;
	public static final int DESCENDING = 1;
	public static final int ASCENDING = 2;
	
	public int createUser(User user);
	public boolean updateUser(User user);
	public boolean deleteUser(int id);
	public User getUser(int id);
	public List<User> findUser(String username);
	public List<User> findUser(String username, String password);
	public List<User> listUsers();
	public List<UserStat> listUserStats();
	public Map<String, List<UserTag>> listTagGroups(User user, int minFreq, int sortBy, int order);
	
}
