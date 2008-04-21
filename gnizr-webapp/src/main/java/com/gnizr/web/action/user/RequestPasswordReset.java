package com.gnizr.web.action.user;

import org.apache.log4j.Logger;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.user.PasswordManager;
import com.gnizr.core.user.UserManager;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.error.ActionErrorCode;

public class RequestPasswordReset extends AbstractAction{


	/**
	 * 
	 */
	private static final long serialVersionUID = 2177445666023620630L;

	private static final Logger logger = Logger.getLogger(RequestPasswordReset.class);
	
	private String username;
	
	private UserManager userManager;
	private PasswordManager passwordManager;
	private MailSender mailSender;
	private SimpleMailMessage templateMessage;
	
	public SimpleMailMessage getTemplateMessage() {
		return templateMessage;
	}

	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public PasswordManager getPasswordManager() {
		return passwordManager;
	}

	public void setPasswordManager(PasswordManager passwordManager) {
		this.passwordManager = passwordManager;
	}

	@Override
	protected String go() throws Exception {
		User user = null;
		try{
			user = userManager.getUser(username);
		}catch(NoSuchUserException e){
			logger.debug("RequestPasswordReset: No such user in the system. Username = " + username);
			addActionError(String.valueOf(ActionErrorCode.ERROR_NO_SUCH_USER));
			return ERROR;
		}
		
		String token = passwordManager.createResetToken(user);
		if(token != null){
			if(sendPasswordResetEmail(token, user) == false){
				return ERROR;
			}
		}else{
			addActionError(String.valueOf(ActionErrorCode.ERROR_NO_RESET_TOKEN));
		}		
		return SUCCESS;
	}

	private boolean sendPasswordResetEmail(String token, User user){
		if(getTemplateMessage() == null){
			logger.error("RequestPasswordReset: templateMessge bean is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_CONFIG));
			return false;
		}
		String toEmail = user.getEmail();
		if(toEmail == null){
			logger.error("RequestPasswordReset: the email of user " + user.getUsername() + " is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_EMAIL_UNDEF));
			return false;
		}
		SimpleMailMessage msg = new SimpleMailMessage(getTemplateMessage());
		msg.setTo(toEmail);
		msg.setText("Token = " + token);
		
		if(getMailSender() == null){
			logger.error("RequestPasswordReset: mailSender bean is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_CONFIG));
			return false;
		}
		try{
			getMailSender().send(msg);
			return true;
		}catch(Exception e){
			logger.error("RequestPasswordReset: send mail error. " + e);
			addActionError(String.valueOf(ActionErrorCode.ERROR_INTERNAL));
		}		
		return false;
	}
}
