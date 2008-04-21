package com.gnizr.web.action.error;

public interface ActionErrorCode {
	
	/**
	 * Internal system error.
	 */
	public static final int ERROR_INTERNAL = 10;
	
	/**
	 * Configuration error;
	 */
	public static final int ERROR_CONFIG = 11;
	
	/**
	 * Error creating token for reseting user password
	 */
	public static final int ERROR_NO_RESET_TOKEN = 11;

	/**
	 * No such user in the system.
	 */
	public static final int ERROR_NO_SUCH_USER = 1000;
	
	/**
	 * User's email is not defined.
	 */
	public static final int ERROR_EMAIL_UNDEF = 1001;
}
