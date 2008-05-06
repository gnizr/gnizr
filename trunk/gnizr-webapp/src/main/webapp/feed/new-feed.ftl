<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign title="create link collect"/>
<#assign username = loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-feed.css")]/>
<@headerBlock/>
<@pageContent>  
<#assign bct = settingsBCT(username) +
               [gzBCTPair("create link collect",gzUserFeedUrl(username,""))]/>
<@infoBlock bct=bct/>

<@pageTitle>Create Link Collect</@pageTitle>
<@pageDescription>
<p>You can collect links from <a href="http://en.wikipedia.org/wiki/Web_feed">Web feeds</a>.  
By subscribing to a Web feed, a robot will be created to monitor new entries published by the feed.
For each entry in the feed, the URL of which will be collected and saved as bookmarks.</p>
<#if serviceEnabled == false>
<div class="system-message"><b>Annoucement</b>: Sorry! The Link Collect service is currently unavaible. The administrator
asked all robots to a break. But, you can still create or edit 
your Link Collect subscriptions.</div>
</#if>
</@pageDescription>
<#assign fval = "http://"/>
<#if feedUrl?exists>
  <#assign fval = feedUrl/>
</#if>
<@formInput>  
 <#if action.actionMessages?has_content>
   <ul class="formErrors">
   <#list action.actionMessages as msg>
     <li class="errorMessage">${getActionMsg(msg)}</li>
   </#list>
   </ul>
  </#if> 
    <@ww.form action="create" namespace="/settings/feeds">
     <@ww.textfield id="new-feed-url" label="feed url" name="feedUrl" value=fval/>
     <@ww.submit cssClass="btn" value="subscribe"/>
    </@ww.form>
</@formInput>
 
</@pageContent>
<@pageEnd/>