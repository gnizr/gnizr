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
               [gzBCTPair("RSS subscriptions",gzUserFeedUrl(username,""))]/>
<@infoBlock bct=bct/>

  <div id="feeds">  

  <div id="create-feed-form">    
    <@ww.form action="create" namespace="/settings/feeds">
     <@ww.textfield id="new-feed-url" label="feed url" name="feedUrl" value="http://"/>
     <@ww.submit cssClass="btn" value="subscribe"/>
    </@ww.form>
    
   <#if action.actionMessages?has_content>
   <ul>
   <#list action.actionMessages as msg>
     <li>${getActionMsg(msg)}</li>
   </#list>
   </ul>
  </#if>
  </div> 
   
  </div>  
</@pageContent>
<@pageEnd/>