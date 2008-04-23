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
	 * Error creating token for reseting user password. Code:12.
	 */
	public static final int ERROR_NO_RESET_TOKEN = 12;
	
	/**
	 * Can't verify user password change token. Code 13.
	 */
	public static final int ERROR_VERIFY_TOKEN = 13;
	
	
	/**
	 * Can't reset user's password. Code 14.
	 */
	public static final int ERROR_PASSWORD_RESET = 14;

	/**
	 * No such user in the system. Code 1000.
	 */
	public static final int ERROR_NO_SUCH_USER = 1000;
	
	/**
	 * User's email is not defined. Code 1001.
	 */
	public static final int ERROR_EMAIL_UNDEF = 1001;

	
}
