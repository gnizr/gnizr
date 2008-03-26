/**
 * <p>Provide classes for managing <code>Bookmark</code> objects and monitoring <code>Bookmark</code> property
 * changes.</p>
 * <h3>How to register <code>BookmarkListener</code></h3>
 * <p>
 * {@link com.gnizr.core.bookmark.BookmarkManager} can be configured to notify
 * {@link com.gnizr.core.bookmark.BookmarkListener} of bookmark changed (added, deleted or updated). 
 * In gnizr, the registration of
 * <code>BookmarkListener</code> is configured via Spring IoC -- via. <code>spring/core-bookmark.xml</code>. 
 * </p>
 * <p><b>Developers are discouraged to register listeners in Java code implementation.</b></p>
 * <h4>Code example on how to configure listener registration via Spring IoC:</h4>
 * <pre>
 * &lt;bean id="bookmarkManager" 
 *          class="com.gnizr.core.bookmark.BookmarkManager" 
 *          destroy-method="shutdown" 
 *          singleton="true"&gt;		
 *		
 *		&lt;!-- a list of BookmarkListeners --&gt;
 *		&lt;constructor-arg index="1"&gt;
 *			&lt;list&gt;
 *				&lt;ref bean="forUserListener"/&gt;
 *				&lt;ref bean="feedSubscribeListener"/&gt;
 *				&lt;ref bean="geonamesTagListener"/&gt;
 *				&lt;ref bean="updateMIMETypeListener"/&gt;
 *				&lt;ref bean="folderTagListener"/&gt;
 *          	&lt;!-- append your listener definition here --&gt;
 *			&lt;/list&gt;
 * &lt;/constructor-arg&gt;
 *		
 *		&lt;!-- other config --&gt;
 *       ...	
 *	&lt;/bean&gt;
 *
 * </pre>
 * 
 * @since 2.3
 */
package com.gnizr.core.bookmark;