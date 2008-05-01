package com.gnizr.web.action.user;

import java.util.Iterator;

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

import freemarker.template.Configuration;

public class TestApproveUserAccount extends GnizrWebappTestBase {

	private UserManager userManager;
	private TokenManager tokenManager;
	private ApproveUserAccount action;
	private SimpleMailMessage welcomeMessage;
	private Configuration freemarkerEngine;	
	private GnizrConfiguration gnizrConfiguration;
	private JavaMailSenderImpl mailSender;
	
	private String username;
	private String token;
	
	protected void setUp() throws Exception {
		super.setUp();
		
		mailSender = new JavaMailSenderImpl();
		mailSender.setHost("localhost");
		
		gnizrConfiguration = new GnizrConfiguration();
		gnizrConfiguration.setSiteContactEmail("admin@mysite.com");
		gnizrConfiguration.setWebApplicationUrl("http://foo.com/gnizr");
		
		FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();
		factory.setTemplateLoaderPath("/templates");
		freemarkerEngine = factory.createConfiguration();
		
		welcomeMessage = new SimpleMailMessage();
		welcomeMessage.setSubject("Welcome");
		
		userManager = new UserManager(getGnizrDao());
		tokenManager = new TokenManager();
		tokenManager.setUserManager(userManager);
		tokenManager.init();
		
		action = new ApproveUserAccount();
		action.setUserManager(userManager);
		action.setTokenManager(tokenManager);
		action.setGnizrConfiguration(gnizrConfiguration);
		action.setWelcomeEmailTemplate(welcomeMessage);
		action.setFreemarkerEngine(freemarkerEngine);
		action.setMailSender(mailSender);
		
		username = "hchen1";
		token = tokenManager.createResetToken(new User(username));
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@Override
	protected IDataSet getDataSet() throws Exception {
		return new FlatXmlDataSet(TestApproveUserAccount.class
				.getResourceAsStream("/TestApproveUserAccount-input.xml"));
	}
	
	@SuppressWarnings("unchecked")
	public void testDoApproveActivationRequestion() throws Exception{
		SimpleSmtpServer mailServer = SimpleSmtpServer.start();
		
		User user = userManager.getUser(username);
		assertEquals(AccountStatus.INACTIVE,user.getAccountStatus().intValue());
		try{
			action.setUsername(username);
			action.setToken(token);
		
			String op = action.doApproveActivationRequest();
			assertEquals(ActionSupport.SUCCESS,op);
		}finally{
			mailServer.stop();
		}
		
		user = userManager.getUser(username);
		assertEquals(AccountStatus.ACTIVE,user.getAccountStatus().intValue());		
		assertFalse(tokenManager.isValidResetToken(token, user));
		
		assertTrue(mailServer.getReceivedEmailSize() == 1);
		Iterator<SmtpMessage> emailIter = mailServer.getReceivedEmail();
		SmtpMessage email = (SmtpMessage) emailIter.next();
		assertTrue(email.getHeaderValue("Subject").equals("Welcome"));
		assertNotNull(email.getBody());
		assertTrue(email.getBody().contains("WelcomeEmail"));
		assertTrue(email.getBody().contains("Username(hchen1)"));
		assertTrue(email.getBody().contains("URL(http://foo.com/gnizr/register/verifyEmail.action?username=hchen1"));
	}
	
	public void testDoDenyActivationRequest() throws Exception{
		User user = userManager.getUser(username);
		assertEquals(AccountStatus.INACTIVE,user.getAccountStatus().intValue());
		
		action.setUsername(username);
		action.setToken(token);
		
		String op = action.doDenyActivationRequest();
		assertEquals(ActionSupport.SUCCESS,op);
		
		user = userManager.getUser(username);
		assertEquals(AccountStatus.INACTIVE,user.getAccountStatus().intValue());
		
		assertFalse(tokenManager.isValidResetToken(token, user));
	}

}
