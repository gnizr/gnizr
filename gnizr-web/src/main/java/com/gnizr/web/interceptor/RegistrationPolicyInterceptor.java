package com.gnizr.web.interceptor;

import com.gnizr.web.util.GnizrConfiguration;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

public class RegistrationPolicyInterceptor implements Interceptor{

/**
	 * 
	 */
	private static final long serialVersionUID = 8293799014946243968L;

	private GnizrConfiguration gnizrConfiguration;
	
	public GnizrConfiguration getGnizrConfiguration() {
		return gnizrConfiguration;
	}

	public void setGnizrConfiguration(GnizrConfiguration gnizrConfiguration) {
		this.gnizrConfiguration = gnizrConfiguration;
	}
	
	public void destroy() {
		// no code;		
	}

	public void init() {
		// no code;
	}

	public String intercept(ActionInvocation actionInvocation) throws Exception {
		if(gnizrConfiguration != null){
			String policy = gnizrConfiguration.getRegistrationPolicy();
			if(policy != null && policy.equalsIgnoreCase("open")){
				return actionInvocation.invoke();
			}
			return "close";
		}
		return actionInvocation.invoke();
	}

}
