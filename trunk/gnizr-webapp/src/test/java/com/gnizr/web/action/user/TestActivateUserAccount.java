package com.gnizr.web.action.user;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.TokenManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.gnizr.db.vocab.AccountStatus;
import com.opensymphony.xwork.ActionSupport;

public class TestActivateUserAccount extends GnizrWebappTestBase {

	private UserManager userManager;
	private TokenManager tokenManager;
	private ActivateUserAccount action;
	
	private String username;
	private String token;
	
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
		tokenManager = new TokenManager();
		tokenManager.setUserManager(userManager);
		tokenManager.init();
		
		action = new ActivateUserAccount();
		action.setUserManager(userManager);
		action.setTokenManager(tokenManager);	
		
		username = "hchen1";
		token = tokenManager.createResetToken(new User(username));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestActivateUserAccount.class
				.getResourceAsStream("/TestActivateUserAccount-input.xml"));
	}

	public void testGo() throws Exception{
		User user = userManager.getUser(username);
		assertEquals(AccountStatus.INACTIVE,user.getAccountStatus().intValue());
		
		action.setUsername(username);
		action.setToken(token);
		
		String op = action.execute();
		assertEquals(ActionSupport.SUCCESS,op);
		
		user = userManager.getUser(username);
		assertEquals(AccountStatus.ACTIVE,user.getAccountStatus().intValue());
		
		assertFalse(tokenManager.isValidResetToken(token, user));
	}
	
	
	
}
