package com.gnizr.web.interceptor;

import com.gnizr.web.util.GnizrConfiguration;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.interceptor.Interceptor;

/**
 * An {@link com.opensymphony.xwork.interceptor.Interceptor} implementation for
 * guarding the user registration functions. When this <code>Interceptor</code>
 * is used on a WebWork Action, it will permit an incoming request to pass
 * if and only if the <code>registrationPolicy</code> of 
 * <code>GnizrConfiguration</code> is either <code>open</code> or <code>approval</code>.
 * If <code>registrationPolicy</code> is <code>close</code>, then this interceptor
 * immediately returns <code>close</code> and skips the invocation of the action.
 * 
 * @author Harry Chen
 * @since 2.4
 *
 */
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
			if(policy != null){
				if(policy.equalsIgnoreCase("open") || policy.equalsIgnoreCase("approval")){
					return actionInvocation.invoke();
				}
			}
			return "close";
		}
		return actionInvocation.invoke();
	}

}
