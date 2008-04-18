<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign title="new feed subscription"/>
<#assign username = loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-feed.css")]/>
<@headerBlock/>
<@pageContent>  
<#assign bct = settingsBCT(username) +
               [gzBCTPair("create bookmarks from feeds",gzUserFeedUrl(username,""))]/>
<@infoBlock bct=bct/>

<@pageTitle>Create Bookmarks from Feeds</@pageTitle>
<@pageDescription>
<p>You can create new bookmarks from <a href="http://en.wikipedia.org/wiki/Web_feed">Web feeds</a>. 
By subscribing to a Web feed, a robot will be created to monitor updates published by the feed. 
New updates will be imported into your account as bookmarks.</p>
<p><b>Tips</b>: Ask your administrator if this feature is enabled.</p> 
</@pageDescription>
<@formInput>     
    <@ww.form action="create" namespace="/settings/feeds">
     <@ww.textfield id="new-feed-url" label="feed url" name="feedUrl" value="http://"/>
     <@ww.submit cssClass="btn" value="subscribe"/>
    </@ww.form>
</@formInput>

   <#if action.actionMessages?has_content>
   <div class="errorMessage">
   <p><ul>
   <#list action.actionMessages as msg>
     <li>${getActionMsg(msg)}</li>
   </#list>
   </ul>
    </p>
    </div>
  </#if>
  
</@pageContent>
<@pageEnd/>