package com.gnizr.web.result;

import java.io.Writer;

import net.sf.json.JSON;

import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;

public class JSONResult implements Result{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6662247866546571184L;
	private static final Logger logger = Logger.getLogger(JSONResult.class);
	
	private String jsonName;
	
	public void execute(ActionInvocation actionInvocation) throws Exception {
		if(jsonName == null){
			final String message = "Required parameter 'jsonName' is not found.";
			throw new RuntimeException(message);
		}
		ServletActionContext.getResponse().setContentType("text/javascript; charset=UTF-8");
		JSON json = (JSON)actionInvocation.getStack().findValue(jsonName);
		if(json != null){
			Writer out = null;
			try{
				out = ServletActionContext.getResponse().getWriter();
				out.append(json.toString());
			}catch(Exception e){
				logger.error("Error write json object: " + e.getMessage(), e);
			}finally{
				if(out != null){
					out.close();
				}
			}                    
		}else{
			logger.error("Can't find json object on stack with name '" + jsonName +"'");
		}
	}

	public String getJsonName() {
		return jsonName;
	}

	public void setJsonName(String jsonName) {
		this.jsonName = jsonName;
	}
}
