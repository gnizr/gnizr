package com.gnizr.web.action.error;

public interface ActionErrorCode {
	
	/**
	 * Internal system error. Code 10.
	 */
	public static final int ERROR_INTERNAL = 10;
	
	/**
	 * Configuration error. Code 11.
	 */
	public static final int ERROR_CONFIG = 11;
	
	/**
	 * No such user in the system. Code 1000.
	 */
	public static final int ERROR_NO_SUCH_USER = 1000;
	
	/**
	 * User's email is not defined. Code 1001.
	 */
	public static final int ERROR_EMAIL_UNDEF = 1001;
	
	/**
	 * Input username is taken by an existing user. Code 1002 
	 */
	public static final int ERROR_USERNAME_TAKEN = 1002;
	
	/**
	 * Failed to create a new user account. Code 1003 
	 */
	public static final int ERROR_CREATE_ACCOUNT = 1003;
	
	/**
	 * Unable to send email. Code 1004.
	 */
	public static final int ERROR_SEND_EMAIL = 1004;
	
	/**
	 * Error creating token for reseting user password. Code 1005.
	 */
	public static final int ERROR_NO_RESET_TOKEN = 1005;
	
	/**
	 * Can't verify user password change token. Code 1006.
	 */
	public static final int ERROR_VERIFY_TOKEN = 1006;
	
	/**
	 * Can't reset user's password. Code 1007.
	 */
	public static final int ERROR_PASSWORD_RESET = 1007;
	
	
	/**
	 * User account status is ACTIVE. Code 1008.
	 */
	public static final int ERROR_ACCOUNT_ACTIVE = 1008;
	
	/**
	 * User account status is INACTIVE. Code 1009.
	 */
	public static final int ERROR_ACCOUNT_INACTIVE = 1009;
	
	/**
	 * User account status is DISABLED. Code 1010.
	 */
	public static final int ERROR_ACCOUNT_DISABLED = 1010;
	
	/**
	 * Login failed. Code 1011.
	 */
	public static final int ERROR_LOGIN_FAILED = 1011;

	
}
