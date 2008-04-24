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
import com.gnizr.web.util.GnizrConfiguration;
import com.opensymphony.xwork.ActionSupport;
import com.opensymphony.xwork.validator.DefaultActionValidatorManager;

import freemarker.template.Configuration;

public class TestRequestPasswordReset extends GnizrWebappTestBase {

	private UserManager userManager;
	private TokenManager tokenManager;
	private RequestPasswordReset action;
	private SimpleMailMessage templateMessage;
	private JavaMailSenderImpl mailSender;
	private Configuration freemarkerEngine;	
	private GnizrConfiguration gnizrConfiguration;
	
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
		templateMessage.setSubject("Reset Password");

		mailSender = new JavaMailSenderImpl();
		mailSender.setHost("localhost");
		
		action = new RequestPasswordReset();
		action.setUserManager(userManager);
		action.setTokenManager(tokenManager);
		action.setVerifyResetTemplate(templateMessage);
		action.setMailSender(mailSender);
		action.setFreemarkerEngine(freemarkerEngine);
		action.setGnizrConfiguration(gnizrConfiguration);
	}

	protected void tearDown() throws Exception {
		super.tearDown();

	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestRequestPasswordReset.class
				.getResourceAsStream("/TestRequestPasswordReset-input.xml"));
	}

	@SuppressWarnings("unchecked")
	public void testValidationMissingUsername() throws Exception {
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		validator.validate(action, "requestReset");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}

	@SuppressWarnings("unchecked")
	public void testValidationUsernameTooShort() throws Exception {
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("ab");
		validator.validate(action, "requestReset");
		assertTrue(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(1, errors.size());
	}

	@SuppressWarnings("unchecked")
	public void testValidationUsernameFilled() throws Exception {
		DefaultActionValidatorManager validator = new DefaultActionValidatorManager();
		action.setUsername("hchen1");
		validator.validate(action, "requestReset");
		assertFalse(action.hasFieldErrors());
		Map<String, Object> errors = action.getFieldErrors();
		assertEquals(0, errors.size());
	}

	@SuppressWarnings("unchecked")
	public void testGo() throws Exception {
		SimpleSmtpServer mailServer = SimpleSmtpServer.start();
		try {
			action.setUsername("hchen1");
			String op = action.execute();
			assertEquals(ActionSupport.SUCCESS, op);
		} finally {
			mailServer.stop();
		}
		assertTrue(mailServer.getReceivedEmailSize() == 1);
		Iterator<SmtpMessage> emailIter = mailServer.getReceivedEmail();
		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue(email.getHeaderValue("Subject").equals("Reset Password"));
		assertNotNull(email.getBody());
		assertTrue(email.getBody().contains("Follow this URL to reset your password"));
	}

}
