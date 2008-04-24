package com.gnizr.core.util;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.User;

public class TestTokenManager extends GnizrCoreTestBase {

	
	private UserManager userManager;
	private TokenManager tokenManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
		tokenManager = new TokenManager();
		tokenManager.setUserManager(userManager);
		tokenManager.init();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestTokenManager.class.getResourceAsStream("/TestTokenManager-input.xml"));
	}
	
	public void testCreateResetToken() throws Exception{
		User hchen1 = userManager.getUser("hchen1");
		assertNotNull(hchen1);
		
		String token = tokenManager.createResetToken(hchen1);
		assertNotNull(token);
		
		List<TokenManager.TokenTicket> tickets = tokenManager.listOpenTickets();
		assertEquals(1,tickets.size());
		
		assertEquals(token,tickets.get(0).getToken());
	}
	
	public void testIsValidResetToken() throws Exception{		
		User hchen1 = userManager.getUser("hchen1");
		assertNotNull(hchen1);
		
		String token1 = tokenManager.createResetToken(hchen1);
		assertNotNull(token1);
		
		User gnizr = userManager.getUser("gnizr");
		assertNotNull(gnizr);
		
		String token2 = tokenManager.createResetToken(gnizr);
		assertNotNull(token2);
		
		assertTrue(tokenManager.isValidResetToken(token1, hchen1));
		assertFalse(tokenManager.isValidResetToken(token1,gnizr));
		assertTrue(tokenManager.isValidResetToken(token2, gnizr));
	
		String token3 = tokenManager.createResetToken(hchen1);
		assertTrue(tokenManager.isValidResetToken(token3, hchen1));
		assertFalse(tokenManager.isValidResetToken(token3,gnizr));
		assertTrue(tokenManager.isValidResetToken(token2, gnizr));
		assertFalse(token3.equals(token1));
		
	}

}
