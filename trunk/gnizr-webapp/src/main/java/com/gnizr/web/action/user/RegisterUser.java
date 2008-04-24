package com.gnizr.web.action.user;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.GnizrDaoUtil;
import com.gnizr.core.util.TokenManager;
import com.gnizr.db.dao.User;
import com.gnizr.db.vocab.AccountStatus;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.error.ActionErrorCode;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * An action implementation for creating new user accounts. After creating 
 * a new user account, it sends an email message to the user to verify the
 * ownership of the provided email address. Accounts created are set to 
 * inactive (i.e., <code>AccountStatus.INACTIVE</code>).
 * 
 * @author Harry Chen
 * @since 2.4
 *
 */
public class RegisterUser extends AbstractAction{

	private static final long serialVersionUID = 2099691743001600308L;

	private static final Logger logger = Logger.getLogger(RegisterUser.class);
	
	private String username;
	private String password;
	private String passwordConfirm;
	private String email;
	
	private UserManager userManager;
	private TokenManager tokenManager;
	private MailSender mailSender;
	private SimpleMailMessage verifyEmailTemplate;
	private Configuration freemarkerEngine;
	
	@Override
	protected String go() throws Exception {
		if(isUsernameTaken(username) == true){
			addActionError(String.valueOf(ActionErrorCode.ERROR_USERNAME_TAKEN));
			logger.debug("RegisterUser: username is taken = " + username);
			return INPUT;
		}
		// assume validators have verified length requirements of the password
		// -- just double checking here.
		if(password != null && passwordConfirm != null && password.length() > 0){
			User user = new User(username,password);
			user.setAccountStatus(AccountStatus.INACTIVE);
			user.setFullname("");
			user.setEmail(email);
			user.setCreatedOn(GnizrDaoUtil.getNow());
			boolean okay = userManager.createUser(user);
			if(logger.isDebugEnabled() == true){
				User copyUser = new User(user);
				copyUser.setPassword("*removed*");
				logger.debug("Create a new account: " + copyUser);
			}
			if(okay == true){
				String token = tokenManager.createResetToken(user);
				logger.debug("Created email verification token: " + token + " for user = " + username);
				if(token != null){
					if(sendEmailVerification(token,user) == false){
						logger.error("Send email verification failed. To user email " + user.getEmail());
						return ERROR;
					}
				}else{
					logger.error("Unable to create token.");
					addActionError(String.valueOf(ActionErrorCode.ERROR_NO_RESET_TOKEN));
					return ERROR;
				}
			}else{
				logger.error("Unable to create new user account.");
				addActionError(String.valueOf(ActionErrorCode.ERROR_CREATE_ACCOUNT));
				return ERROR;
			}
		}
		return SUCCESS;
	}
	
	public String doRenewUserAccount() throws Exception{
		User user = null;
		try{
			user = userManager.getUser(username);
		}catch(NoSuchUserException e){
			addActionError(String.valueOf(ActionErrorCode.ERROR_NO_SUCH_USER));
			return INPUT;
		}
		if(user != null){
			if(user.getAccountStatus() == AccountStatus.INACTIVE){
				String token = tokenManager.createResetToken(user);
				if(token != null){
					if(sendEmailVerification(token, user) == true){
						return SUCCESS;
					}
				}
			}else{
				addActionError(String.valueOf(ActionErrorCode.ERROR_ACCOUNT_ACTIVE));
				logger.debug("Attempt to send email verification for an activate account: " + getUsername());
				return INPUT;
			}
		}
		return ERROR;
	}	
	
	private boolean sendEmailVerification(String token, User user){
		Map<String,Object> model = new HashMap<String, Object>();
		model.put("token", token);
		model.put("username",user.getUsername());
		model.put("gnizrConfiguration", getGnizrConfiguration());
		
		if(getVerifyEmailTemplate() == null){
			logger.error("RegisterUser: templateMessge bean is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_CONFIG));
			return false;
		}
		String toEmail = user.getEmail();
		if(toEmail == null){
			logger.error("RegisterUser: the email of user " + user.getUsername() + " is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_EMAIL_UNDEF));
			return false;
		}
		SimpleMailMessage msg = new SimpleMailMessage(getVerifyEmailTemplate());
		msg.setTo(toEmail);
		
		if(msg.getFrom() == null){
			String contactEmail = getGnizrConfiguration().getSiteContactEmail();
			if(contactEmail != null){
				msg.setFrom(contactEmail);
			}else{
				msg.setFrom("help@localhost");
			}
		}		
				
		Template fmTemplate = null;
		String text = null;
		try{			
			fmTemplate = freemarkerEngine.getTemplate("login/verifyemail-template.ftl");
			text = FreeMarkerTemplateUtils.processTemplateIntoString(fmTemplate,model);
		}catch(Exception e){
			logger.error("RegisterUser: error creating message template from Freemarker engine");
		}

		msg.setText(text);
		
		if(getMailSender() == null){
			logger.error("RegisterUser: mailSender bean is not defined");
			addActionError(String.valueOf(ActionErrorCode.ERROR_CONFIG));
			return false;
		}
		try{
			getMailSender().send(msg);
			return true;
		}catch(Exception e){
			logger.error("RegisterUser: send mail error. " + e);
			addActionError(String.valueOf(ActionErrorCode.ERROR_INTERNAL));
		}		
		return false;
	}

	private boolean isUsernameTaken(String username){
		return userManager.hasUser(new User(username));
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public TokenManager getTokenManager() {
		return tokenManager;
	}

	public void setTokenManager(TokenManager tokenManager) {
		this.tokenManager = tokenManager;
	}

	public MailSender getMailSender() {
		return mailSender;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}

	public SimpleMailMessage getVerifyEmailTemplate() {
		return verifyEmailTemplate;
	}

	public void setVerifyEmailTemplate(SimpleMailMessage templateMessage) {
		this.verifyEmailTemplate = templateMessage;
	}

	public Configuration getFreemarkerEngine() {
		return freemarkerEngine;
	}

	public void setFreemarkerEngine(Configuration freemarketEngine) {
		this.freemarkerEngine = freemarketEngine;
	}
}
