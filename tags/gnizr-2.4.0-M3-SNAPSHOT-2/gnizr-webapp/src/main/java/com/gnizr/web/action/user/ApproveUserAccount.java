package com.gnizr.web.action.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.TokenManager;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.error.ActionErrorCode;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class ApproveUserAccount extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5750382322536873612L;
	private static final Logger logger = Logger.getLogger(ApproveUserAccount.class);
	
	private TokenManager tokenManager;
	private UserManager userManager;
	
	private String username;
	private String token;
	
	private MailSender mailSender;
	private SimpleMailMessage welcomeEmailTemplate;
	private Configuration freemarkerEngine;
	
	public TokenManager getTokenManager() {
		return tokenManager;
	}

	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String doDenyActivationRequest() throws Exception{
		logger.debug("ActiveUserAccount: username = " + username + ", token = " + token);
		if(token == null || username == null){
			return INPUT;
		}
		User user = null;
		try{
			user = userManager.getUser(username);			
		}catch(NoSuchUserException e){
			logger.debug("ActivateUserAccount: doDeleteActivationRequest: " + e);
			return INPUT;
		}
		if(tokenManager.isValidResetToken(token, user) == true){
			if(tokenManager.deleteToken(token, user) == true){
				return SUCCESS;
			}else{
				logger.error("Unable to delete token: " + token + " user=" + user.getUsername());
			}
		}else{
			logger.debug("No such token: " + token + " for user " + user.getUsername());
		}
		return SUCCESS;
	}

	public String doApproveActivationRequest() throws Exception{
		logger.debug("ActiveUserAccount: username = " + username + ", token = " + token);
		if(token == null || username == null){
			return INPUT;
		}
		
		User user = null;
		try{
			user = userManager.getUser(username);			
		}catch(NoSuchUserException e){
			logger.debug("ActivateUserAccount: " + e);
			return INPUT;
		}
		try{
			if(tokenManager.isValidResetToken(token, user) == true){
				if(tokenManager.deleteToken(token, user) == true){
					if(userManager.activateUserAccount(user) == true){
						logger.debug("Activated account for " + user.getUsername());
						if(sendNotificationEmail(user) == false){
							logger.error("Unable to send new account approval email to " + user.getEmail());
							return ERROR;
						}
						return SUCCESS;
					}else{
						logger.error("Unable activate user account!");
					}
				}else{
					logger.error("Unable delete token");
				}
			}else{
				if(logger.isDebugEnabled()){
					logger.debug("Invalid token: " + token);
					logger.debug("List of tokens: " + tokenManager.listOpenTickets().toString());
				}
			}
		}catch(Exception e){
			logger.debug("ActivateUserAccount: username = " 
					+ username + ", token = " + token + e);
			return ERROR;
		}		
		return INPUT;
	}
	
	@Override
	protected String go() throws Exception {
		return ERROR;
	}
	
	private boolean sendNotificationEmail(User user){
		Map<String,Object> model = new HashMap<String, Object>();
		model.put("token", token);
		model.put("username",user.getUsername());
		model.put("email",user.getEmail());
		model.put("createdOn", user.getCreatedOn());
		model.put("gnizrConfiguration", getGnizrConfiguration());

		if(getWelcomeEmailTemplate() == null){
			logger.error("ApproveUserAccount: welcomeEmailTemplate bean is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_CONFIG));
			return false;
		}
		
		String toUserEmail = user.getEmail();
		if(toUserEmail == null){
			logger.error("ApproveUserAccount: the email of user " + user.getUsername() + " is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_EMAIL_UNDEF));
			return false;
		}
		
		SimpleMailMessage notifyMsg = new SimpleMailMessage(getWelcomeEmailTemplate());
		notifyMsg.setTo(toUserEmail);
		
		if(notifyMsg.getFrom() == null){
			String contactEmail = getGnizrConfiguration().getSiteContactEmail();
			if(contactEmail != null){
				notifyMsg.setFrom(contactEmail);
			}else{
				notifyMsg.setFrom("no-reply@localhost");
			}
		}	
		
		Template fmTemplate1 = null;
		String text1 = null;
		try{			
			fmTemplate1 = freemarkerEngine.getTemplate("login/welcome-template.ftl");			
			text1 = FreeMarkerTemplateUtils.processTemplateIntoString(fmTemplate1,model);
		}catch(Exception e){
			logger.error("ApproveUserAccount: error creating message template from Freemarker engine");
		}
		notifyMsg.setText(text1);
		
		if(getMailSender() == null){
			logger.error("ApproveUserAccount: mailSender bean is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_CONFIG));
			return false;
		}
		try{
			getMailSender().send(notifyMsg);		
			return true;
		}catch(Exception e){
			logger.error("ApproveUserAccount: send mail error. " + e);
			addActionError(String.valueOf(ActionErrorCode.ERROR_INTERNAL));
		}		
		
		return false;
	}

	public SimpleMailMessage getWelcomeEmailTemplate() {
		return welcomeEmailTemplate;
	}

	public void setWelcomeEmailTemplate(SimpleMailMessage welcomeEmailTemplate) {
		this.welcomeEmailTemplate = welcomeEmailTemplate;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public Configuration getFreemarkerEngine() {
		return freemarkerEngine;
	}

	public void setFreemarkerEngine(Configuration freemarkerEngine) {
		this.freemarkerEngine = freemarkerEngine;
	}
	
	

}
