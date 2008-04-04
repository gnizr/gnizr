<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#assign title="${user.username}'s feed subscriptions"/>
<#assign thisPageHref = gzUserFeedUrl(user.username,"")/>
${session.setAttribute("thisPageHref",thisPageHref)}
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-feed.css")]/>
<@headerBlock/>
<@pageContent>  
<#assign bct = [gzBCTPair(username,gzUserUrl(username)),
                gzBCTPair("RSS subscriptions",gzUserFeedUrl(username,""))]/>
<@infoBlock bct=bct/>
  <div id="feeds">      
<#if subscriptions?has_content>  
  <table id="feedTable">
  <tr><th>description</th><th>auto import</th></tr>
  <#list subscriptions as feed>  
  <#assign feedHref = feed.bookmark.link.url/>
  <#assign edtFeedHref = gzUrl("/settings/feeds/edit.action?feedUrl="+feed.bookmark.link.url?url)/>
  <#assign delFeedHref = gzUrl("/settings/feeds/unsub.action?feedUrl="+feed.bookmark.link.url?url)/>
  <tr class="feedRow">
  <td class="feedname">  
  <a href="${feedHref}" target="_blank" title="feed: ${feed.bookmark.title}">${feed.bookmark.title}</a>
  <div class="feed-notes">
   <span class="feedurl">${prettyFormatUrl(feedHref)}</span>
   save feed items to
   <#if feed.importFolders?has_content>
    <#list feed.importFolders as fname>
    <#assign fldrUrl = gzUserFolderUrl(feed.bookmark.user.username,fname)/>
    <a href="${fldrUrl}" class="folder">${gzFormatFolderName(fname)}</a><#if fname_has_next>, </#if>
    </#list>
   <#else>    
     <a href="${gzUserImportedFolderUrl(feed.bookmark.user.username)}" class="folder">My RSS Imported</a>
   </#if>
   <br/>
   last updated: 
   <#if feed.lastSync?exists>
     <span class="last-sync">${feed.lastSync}</span>
   <#else>
     <span class="last-sync pending">pending</span>     
   </#if>
  </div>
  </td>
  <td class="autoimport">${mapToYesNo(feed.autoImport)}</td>  
  <#if loggedInUser?exists && isUserAuth(loggedInUser,user) == true>
  <td><a href="${edtFeedHref}">edit</a> | <a href="${delFeedHref}">unsubscribe</a></td>
  </#if>
  </tr>      
  </#list>
  </table>
<#else>
  No subscribed feeds here!
</#if>  
<#if loggedInUser?exists && isUserAuth(loggedInUser,user)>
  <div id="create-feed-form">    
    <@ww.form action="create" namespace="/settings/feeds">
     <@ww.textfield id="new-feed-url" label="feed url" name="feedUrl"/>
     <@ww.submit cssClass="btn" value="subscribe"/>
    </@ww.form>
    
   <#if action.actionMessages?has_content>
   <ul>
   <#list action.actionMessages as msg>
     <li>${msg}</li>
   </#list>
   </ul>
  </#if>
  </div> 
</#if>   
  </div>  
</@pageContent>
<@pageEnd/>
