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
package com.gnizr.web.action.user;

import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.TokenManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.validator.DefaultActionValidatorManager;

public class TestResetPassword extends GnizrWebappTestBase {

	private UserManager userManager;
	private TokenManager tokenManager;
	private ResetPassword action;
	
	private String token;
	private String username;
	
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
		tokenManager = new TokenManager();
		tokenManager.setUserManager(userManager);
		tokenManager.init();
		
		action = new ResetPassword();
		action.setTokenManager(tokenManager);
		action.setUserManager(userManager);
		
		username = "hchen1";
		token = tokenManager.createResetToken(userManager.getUser(username));		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testActionValidationMissingAll() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		validator.validate(action, "resetPassword");
		assertTrue(action.hasFieldErrors());
		Map<String,Object> errors = action.getFieldErrors();
		assertEquals(4,errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testActionValidationMissingUsername() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("hchen1");
		validator.validate(action, "resetPassword");
		assertTrue(action.hasFieldErrors());
		Map<String,Object> errors = action.getFieldErrors();
		assertEquals(3,errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testActionValidationMissingToken() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setToken("foobartoken");
		validator.validate(action, "resetPassword");
		assertTrue(action.hasFieldErrors());
		Map<String,Object> errors = action.getFieldErrors();
		assertEquals(3,errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testActionValidationMissingTokenNUsername() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setToken("foobartoken");
		action.setUsername("hchen1");
		validator.validate(action, "resetPassword");
		assertTrue(action.hasFieldErrors());
		Map<String,Object> errors = action.getFieldErrors();
		assertEquals(2,errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testActionValidationPasswdInputNotSame() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setToken("foobartoken");
		action.setUsername("hchen1");
		action.setPassword("abcdeffg");
		action.setPasswordConfirm("1234567");
		validator.validate(action, "resetPassword");
		assertTrue(action.hasFieldErrors());
		Map<String,Object> errors = action.getFieldErrors();
		assertEquals(1,errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testActionValidationPasswdTooShort() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setToken("foobartoken");
		action.setUsername("hchen1");
		action.setPassword("1");
		action.setPasswordConfirm("1");
		validator.validate(action, "resetPassword");
		assertTrue(action.hasFieldErrors());
		Map<String,Object> errors = action.getFieldErrors();
		assertEquals(1,errors.size());
	}
	
	/**
	 * Tests the second stage of password reset. A token
	 * has been created for the user 'hchen1'. The user follows
	 * the verification link in the email received and arrives
	 * at the verification page (handled by <code>doVerify</code>).
	 * 
	 * @throws Exception
	 */
	public void testDoVerify() throws Exception{
		action.setToken(token);
		action.setUsername(username);
		String op = action.doVerify();
		assertEquals(ActionSupport.SUCCESS,op);
		assertFalse(action.hasActionErrors());
		
		action.setToken("jfjdjsjfs");
		action.setUsername(username);
		op = action.doVerify();
		assertEquals(ActionSupport.ERROR,op);
		assertTrue(action.hasActionErrors());
	}
	
	
	/**
	 * Tests the final stage of passwod reset. A token has
	 * already been created for user 'hchen1'. The user 
	 * enters a new password 'poploop'. After executing
	 * the action, verify that the user's password 
	 * is changed in the database.  
	 * 
	 * @throws Exception
	 */
	public void testResetPassword() throws Exception{
		action.setToken(token);
		action.setUsername(username);
		action.setPassword("poploop");
		action.setPasswordConfirm("poploop");
		
		String op = action.execute();
		assertEquals(ActionSupport.SUCCESS,op);
		
		User user = userManager.getUser(username,"poploop");
		assertNotNull(user);
		assertEquals(user.getUsername(),username);
	}
	

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestResetPassword.class.getResourceAsStream("/TestResetPassword-input.xml"));
	}

	
	
}
