package com.gnizr.core.user;

import java.util.List;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.gnizr.core.GnizrCoreTestBase;
import com.gnizr.db.dao.User;

public class TestPasswordManager extends GnizrCoreTestBase {

	
	private UserManager userManager;
	private PasswordManager passwordManager;
	
	protected void setUp() throws Exception {
		super.setUp();
		userManager = new UserManager(getGnizrDao());
		passwordManager = new PasswordManager();
		passwordManager.setUserManager(userManager);
		passwordManager.init();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestPasswordManager.class.getResourceAsStream("/TestPasswordManager-input.xml"));
	}
	
	public void testCreateResetToken() throws Exception{
		User hchen1 = userManager.getUser("hchen1");
		assertNotNull(hchen1);
		
		String token = passwordManager.createResetToken(hchen1);
		assertNotNull(token);
		
		List<PasswordManager.ResetPasswordTicket> tickets = passwordManager.listOpenTickets();
		assertEquals(1,tickets.size());
		
		assertEquals(token,tickets.get(0).getToken());
	}
	
	public void testIsValidResetToken() throws Exception{		
		User hchen1 = userManager.getUser("hchen1");
		assertNotNull(hchen1);
		
		String token1 = passwordManager.createResetToken(hchen1);
		assertNotNull(token1);
		
		User gnizr = userManager.getUser("gnizr");
		assertNotNull(gnizr);
		
		String token2 = passwordManager.createResetToken(gnizr);
		assertNotNull(token2);
		
		assertTrue(passwordManager.isValidResetToken(token1, hchen1));
		assertFalse(passwordManager.isValidResetToken(token1,gnizr));
		assertTrue(passwordManager.isValidResetToken(token2, gnizr));
	
		String token3 = passwordManager.createResetToken(hchen1);
		assertTrue(passwordManager.isValidResetToken(token3, hchen1));
		assertFalse(passwordManager.isValidResetToken(token3,gnizr));
		assertTrue(passwordManager.isValidResetToken(token2, gnizr));
		assertFalse(token3.equals(token1));
		
	}
	
	public void testResetPassword() throws Exception{
		User hchen1 = userManager.getUser("hchen1");
		assertNotNull(hchen1);
		String oldPassword = hchen1.getPassword();
		
		String token = passwordManager.createResetToken(hchen1);
		
		hchen1.setPassword("javarocks");
		boolean okay = passwordManager.resetPassword(token,hchen1);
		assertTrue(okay);
		hchen1 = userManager.getUser("hchen1");
		assertFalse(oldPassword.equals(hchen1.getPassword()));
		
		User hchen1_copy = userManager.getUser("hchen1", "javarocks");
		assertNotNull(hchen1_copy);
		assertEquals(hchen1.getId(),hchen1_copy.getId());
	}

}
