package com.gnizr.web.result;

import java.io.Writer;

import net.sf.json.JSON;

import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;

/**
 * <p>This class provides an convenient way to produce data in the JSON format.</p>
 * 
 * <h3>How to use Result <code>jsonName</code></h3>
 * <h4>Step 1: Implement an accessor method for getting a <code>JSON</code> object</h4>
 * <pre>
 *   public class MyActionClass extends Action{
 *      public String execute(){
 *       ... 
 *       return SUCCESS;
 *      }
 *       ...
 *      public JSON getMyJsonData(){
 *        ...
 *        return jData;
 *      }
 *   }
 * </pre>
 * <h4>Step 2: Define ResultType in xwork configuration</h4> 
 * In the <code>xwork.xml</code>
 * <pre>
 *   &lt;action name="doWork" class="com.example.MyActionClass"&gt;
 *	  ...		
 *     &lt;result name="success" type="json"&gt;
 *       &lt;param name="jsonName">myJsonData&lt;/param&gt;
 *     &lt;/result&gt;
 *   &lt;/action&gt;	
 * </pre> 
 * 
 * @author Harry Chen
 * @since 2.3
 */
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
