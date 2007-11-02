/*
 * gnizr is a trademark of Image Matters LLC in the United States.
 * 
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either expressed or implied. See the License
 * for the specific language governing rights and limitations under the License.
 * 
 * The Initial Contributor of the Original Code is Image Matters LLC.
 * Portions created by the Initial Contributor are Copyright (C) 2007
 * Image Matters LLC. All Rights Reserved.
 */
package com.gnizr.core.web.result;

import java.io.Writer;

import org.apache.log4j.Logger;

import com.opensymphony.webwork.ServletActionContext;
import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.Result;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedOutput;

/*
 * Part of the code adopted from s2-rome's RomeResult class.
 * http://s2-rome.googlecode.com
 */

/**
 * A simple {@link com.opensymphony.xwork2.Result} to output a 
 * <a href="https://rome.dev.java.net/">Rome</a> {@link com.sun.syndication.feed.synd.SyndFeed} 
 * object into a newsfeed.
 * <p/>
 * Has 4 parameters that can be set on the {@link com.opensymphony.xwork2.Result}.
 * <ul>
 * <li><b>feedName</b> (required): the expression to find the {@link com.sun.syndication.feed.synd.SyndFeed} on the value stack (eg. 'feed' will result in a call to 'getFeed()' on your Action.</li>
 * <li><b>mimeType</b> (optional, defaults to 'text/xml'): the preferred mime type.</li>
 * <li><b>encoding</b> (optional, defaults to the {@link com.sun.syndication.feed.synd.SyndFeed}'s encoding or falls back on the system): the preferred encoding (eg. UFT-8)
 * <li><b>feedType</b> (optional): the feed type.
 * <p>
 * Accepted feedType values are:
 * <ul>
 * <li>atom_0.3</li>
 * <li>atom_1.0</li>
 * <li>rss_0.90</li>
 * <li>rss_0.91N (RSS 0.91 Netscape)</li>
 * <li>rss_0.91U (RSS 0.91 Userland)</li>
 * <li>rss_0.92</li>
 * <li>rss_0.93</li>
 * <li>rss_0.94</li>
 * <li>rss_1.0</li>
 * <li>rss_2.0</li>
 * </ul>
 * </p>
 * </li>
 * </ul>
 * 
 * <i>Credit</i>: Code and documentation adopted from <a href="http://s2-rome.googlecode.com">s2-rome</a> 
 */
public class RomeResult implements Result{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3467825900971108014L;
	private static final Logger logger = Logger.getLogger(RomeResult.class);
	
    private String feedName;                // must be set by the parameter
    private String feedType;                // see javadoc for a list of the supported values
    private String mimeType = "text/xml";   // the original default, probably always wrong.
    private String encoding;                // defaults to platform default. Should be set in feed.
	
    /**
     * Implementation of {@link com.opensymphony.xwork2.Result#execute(com.opensymphony.xwork2.ActionInvocation)}
     * @param actionInvocation the ActionInvocation
     * @throws Exception
     */
    public void execute(ActionInvocation actionInvocation) throws Exception {
        if (feedName == null) {
            // ack, we need this to find the feed on the stack, if not, blow up
            final String message = "Required parameter 'feedName' not found. "
                            + "Make sure you have the param tag set and "
                            + "the staticParams interceptor enabled in your interceptor stack.";
            logger.error(message); // log it in case the runtime exception gets swallowed
            throw new RuntimeException(message); // no point in continuing ..
        }

        // don't forget to set the content to the correct mimetype
        ServletActionContext.getResponse().setContentType(mimeType);
        // get the feed from the stack that can be found by the feedName
        SyndFeed feed = (SyndFeed) actionInvocation.getStack().findValue(feedName);

        if (feed != null) {
            if (encoding == null)
                encoding = feed.getEncoding();    // second choice is whatever the feed specifies
            if (encoding != null)
                ServletActionContext.getResponse().setCharacterEncoding(encoding);
            // If neither of the above work, we'll get the platform default.

            if (logger.isDebugEnabled())
                logger.debug("Found object on stack with expression '" + feedName + "': " + feed);

            if (feedType != null) // if the feedType is specified, we'll override the one set in the feed object
                feed.setFeedType(feedType);

            SyndFeedOutput output = new SyndFeedOutput();
            // we'll need the writer since Rome doesn't support writing to an outputStream yet
            Writer out = null;
            try {
                out = ServletActionContext.getResponse().getWriter();
                preprocess(feed);
                output.output(feed, out);
                postprocess(feed);
            } catch (Exception e) {
                // Woops, couldn't write the feed ?
                logger.error("Could not write the feed: " + e.getMessage(), e);
            } finally {
                // close the output writer (will flush automatically)
                if (out != null)
                    out.close();
            }
        } else {
            // woops .. no object found on the stack with that name ?
            final String message = "Did not find object on stack with name '" + feedName + "'";
            logger.error(message);
            throw new RuntimeException(message); // no point in continuing ..
        }
    }
    
    public void setFeedName(String feedName) {
        this.feedName = feedName;
    }

    public void setFeedType(String feedType) {
        this.feedType = feedType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }
    
    /**
     * Process <code>feed</code> before it is written into the output stream.
     * @param feed
     */
	protected void preprocess(SyndFeed feed){
		// no code
	}
	
	/**
	 * Process <code>feed</code> after it has been written into the output stream.
	 * @param feed
	 */
	protected void postprocess(SyndFeed feed){
		// no code
	}
}
