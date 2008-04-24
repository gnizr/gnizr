package com.gnizr.web.action.user;

import java.util.Iterator;
import java.util.Map;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;

import com.dumbster.smtp.SimpleSmtpServer;
import com.dumbster.smtp.SmtpMessage;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.TokenManager;
import com.gnizr.core.web.junit.GnizrWebappTestBase;
import com.gnizr.db.dao.User;
import com.gnizr.db.vocab.AccountStatus;
import com.gnizr.web.util.GnizrConfiguration;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.validator.DefaultActionValidatorManager;

import freemarker.template.Configuration;

public class TestRegisterUser extends GnizrWebappTestBase {

	private UserManager userManager;
	private TokenManager tokenManager;
	private SimpleMailMessage templateMessage;
	private JavaMailSenderImpl mailSender;
	private GnizrConfiguration gnizrConfiguration;
	private Configuration freemarkerEngine;	
	
	private RegisterUser action;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		gnizrConfiguration = new GnizrConfiguration();
		gnizrConfiguration.setSiteContactEmail("admin@mysite.com");
		gnizrConfiguration.setWebApplicationUrl("http://foo.com/gnizr");
		
		FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
		factory.setTemplateLoaderPath("/templates");
		freemarkerEngine = factory.createConfiguration();
		
		userManager = new UserManager(getGnizrDao());
		tokenManager = new TokenManager();
		tokenManager.setUserManager(userManager);
		tokenManager.init();
		
		templateMessage = new SimpleMailMessage();
		templateMessage.setSubject("Email Verification");

		mailSender = new JavaMailSenderImpl();
		mailSender.setHost("localhost");
		
		action = new RegisterUser();
		action.setUserManager(userManager);
		action.setTokenManager(tokenManager);
		action.setVerifyEmailTemplate(templateMessage);
		action.setMailSender(mailSender);
		action.setFreemarkerEngine(freemarkerEngine);
		action.setGnizrConfiguration(gnizrConfiguration);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@SuppressWarnings("unchecked")
	public void testValidationMissingUsername() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(4, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationUsernameTooShort() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("ab");
		action.setPassword("poploop");
		action.setPasswordConfirm("poploop");
		action.setEmail("foo@foo.com");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationUsernameRegEx() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("123abc");
		action.setPassword("poploop");
		action.setPasswordConfirm("poploop");
		action.setEmail("foo@foo.com");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationUsernameRegEx2() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("abc_30");
		action.setPassword("poploop");
		action.setPasswordConfirm("poploop");
		action.setEmail("foo@foo.com");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationUsernameRegEx3() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("abc30");
		action.setPassword("poploop");
		action.setPasswordConfirm("poploop");
		action.setEmail("foo@foo.com");
		validator.validate(action, "createUser");
		assertFalse(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(0, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationUsernameTooLong() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("abc3000000000000000000000000000000000000");
		action.setPassword("poploop");
		action.setPasswordConfirm("poploop");
		action.setEmail("foo@foo.com");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationMissingEmail() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("abc30");
		action.setPassword("poploop");
		action.setPasswordConfirm("poploop");
		action.setEmail("");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationInvalidEmail() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("abc30");
		action.setPassword("popoop");
		action.setPasswordConfirm("popoop");
		action.setEmail("233@");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationPasswdDontMatch() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("abc30");
		action.setPassword("poploop1");
		action.setPasswordConfirm("poploop");
		action.setEmail("233@foo.com");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationPasswdTooShort() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("abc30");
		action.setPassword("pop");
		action.setPasswordConfirm("pop");
		action.setEmail("233@foo.com");
		validator.validate(action, "createUser");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testGo() throws Exception{
		SimpleSmtpServer mailServer = SimpleSmtpServer.start();
		try {
			action.setUsername("johnp");
			action.setPassword("poploop");
			action.setPasswordConfirm("poploop");
			action.setEmail("johnp@example.com");
			String op = action.execute();
			assertEquals(ActionSupport.SUCCESS, op);
		} finally {
			mailServer.stop();
		}
		assertTrue(mailServer.getReceivedEmailSize() == 1);
		Iterator<SmtpMessage> emailIter = mailServer.getReceivedEmail();
		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue(email.getHeaderValue("Subject").equals("Email Verification"));
		assertNotNull(email.getBody());
		assertTrue(email.getBody().contains("Username(johnp)"));
		assertTrue(email.getBody().contains("URL(http://foo.com/gnizr/register/verifyEmail.action?username=johnp"));
		
		User user = userManager.getUser("johnp","poploop");
		assertEquals(AccountStatus.INACTIVE,user.getAccountStatus().intValue());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationRenewUserMissingUsername() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();		
		validator.validate(action, "requestRenew");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationRenewUserUsernameTooShort() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();		
		action.setUsername("hc");
		validator.validate(action, "requestRenew");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testValidationRenewUserUsernameInvalidChar() throws Exception{
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();		
		action.setUsername("12hcd");
		validator.validate(action, "requestRenew");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}
	
	@SuppressWarnings("unchecked")
	public void testRenewUser() throws Exception{
		SimpleSmtpServer mailServer = SimpleSmtpServer.start();
		try {
			action.setUsername("hchen1");
			String op = action.doRenewUserAccount();
			assertEquals(ActionSupport.SUCCESS, op);
		} finally {
			mailServer.stop();
		}
		assertTrue(mailServer.getReceivedEmailSize() == 1);
		Iterator<SmtpMessage> emailIter = mailServer.getReceivedEmail();
		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue(email.getHeaderValue("Subject").equals("Email Verification"));
		assertNotNull(email.getBody());
		assertTrue(email.getBody().contains("Username(hchen1)"));
		assertTrue(email.getBody().contains("URL(http://foo.com/gnizr/register/verifyEmail.action?username=hchen1"));
		
		User user = userManager.getUser("hchen1");
		assertEquals(AccountStatus.INACTIVE,user.getAccountStatus().intValue());
	}
	
	@SuppressWarnings("unchecked")
	public void testRenewUser2() throws Exception{
		SimpleSmtpServer mailServer = SimpleSmtpServer.start();
		try {
			action.setUsername("gnizr");
			String op = action.doRenewUserAccount();
			assertEquals(ActionSupport.INPUT, op);
		} finally {
			mailServer.stop();
		}
		assertTrue(mailServer.getReceivedEmailSize() == 0);
		
		User user = userManager.getUser("gnizr");
		assertEquals(AccountStatus.ACTIVE,user.getAccountStatus().intValue());
	}
	
	@SuppressWarnings("unchecked")
	public void testRenewUser3() throws Exception{
		SimpleSmtpServer mailServer = SimpleSmtpServer.start();
		try {
			action.setUsername("poopppp");
			String op = action.doRenewUserAccount();
			assertEquals(ActionSupport.INPUT, op);
		} finally {
			mailServer.stop();
		}
		assertTrue(mailServer.getReceivedEmailSize() == 0);
	}
	
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestRegisterUser.class
				.getResourceAsStream("/TestRegisterUser-input.xml"));
	}

}
