package com.gnizr.web.action.export;

import com.gnizr.web.action.AbstractLoggedInUserAction;

public class ExportDispatcher extends AbstractLoggedInUserAction{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2199726444162343658L;

	private String format;
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	@Override
	protected boolean isStrictLoggedInUserMode() {
		return true;
	}

	@Override
	protected String go() throws Exception {
		super.resolveUser();
		return SUCCESS;
	}
	
	

}
