package com.gnizr.web.action.user;

import org.apache.log4j.Logger;

import com.gnizr.core.exceptions.NoSuchUserException;
import com.gnizr.core.user.UserManager;
import com.gnizr.core.util.TokenManager;
import com.gnizr.db.dao.User;
import com.gnizr.web.action.AbstractAction;
import com.gnizr.web.action.error.ActionErrorCode;
import com.opensymphony.xwork.ActionSupport;

public class ResetPassword extends AbstractAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 124169515575093431L;
	private static final Logger logger = Logger.getLogger(ResetPassword.class);
	
	private String username;
	private String token;
	private String password;
	private String passwordConfirm;
	
	private UserManager userManager;
	private TokenManager tokenManager;
	
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

	public String doVerify() throws Exception {
		logger.debug("ResetPassword.doVerify(): username="+username + ",token="+token);
		User user = null;
		try{
			user = userManager.getUser(username);
			boolean isValid = tokenManager.isValidResetToken(token, user);
			if(isValid == true){
				return SUCCESS;
			}else{
				addActionError(String.valueOf(ActionErrorCode.ERROR_VERIFY_TOKEN));
			}
		}catch(NoSuchUserException e){
			logger.debug("ResetPassword: no such user=" + username);
			addActionError(String.valueOf(ActionErrorCode.ERROR_VERIFY_TOKEN));
		}catch(Exception e){
			logger.debug("ResetPassword: error="+e);
			addActionError(String.valueOf(ActionErrorCode.ERROR_VERIFY_TOKEN));
		}
		return ERROR;
	}

	@Override
	protected String go() throws Exception {
		logger.debug("ResetPassword.doVerify(): username="+username + ",token="+token);
		try{
			if(doVerify() == ActionSupport.SUCCESS){
				User user = userManager.getUser(username);
				// assume that validator has checked the password == passwordConfirm
				if(password != null && passwordConfirm != null){
					user.setPassword(password);
					if(tokenManager.deleteToken(token, user)){
						boolean isOkay = userManager.changePassword(user);
						if(isOkay == true){
							return SUCCESS;
						}
					}
					addActionError(String.valueOf(ActionErrorCode.ERROR_PASSWORD_RESET));	
				}				
			}
		}catch(Exception e){
			addActionError(String.valueOf(ActionErrorCode.ERROR_VERIFY_TOKEN));
		}
		return ERROR;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getPassword() {
		return password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

}
