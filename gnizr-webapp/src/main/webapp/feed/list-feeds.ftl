<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#assign title="${user.username}'s link collect"/>
<#assign thisPageHref = gzUserFeedUrl(user.username,"")/>
<#if session?exists>
  ${session.setAttribute("thisPageHref",thisPageHref)}
</#if>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-feed.css")]/>
<@headerBlock/>
<@pageContent>  
<#assign bct = [gzBCTPair(username,gzUserUrl(username)),
                gzBCTPair("link collect",gzUserFeedUrl(username,""))]/>
<@infoBlock bct=bct/>
<@pageTitle>Link Collect</@pageTitle>
<@pageDescription>
<p>You can collect links from <a href="http://en.wikipedia.org/wiki/Web_feed">Web feeds</a>.  
By subscribing to a Web feed, a robot will be created to monitor new entries published by the feed.
For each entry in the feed, the URL of which will be collected and saved as bookmarks.</p>
<p>Robots will only process those feed subscriptions that have <b>Auto Import</b> enabled.</p>

<p><b>Tips</b>: Ask your administrator if this feature is enabled.</p> 
</@pageDescription>
     
<#if subscriptions?has_content>  
  <table id="feedTable">
  <tr><th>Description</th><th>Auto Import</th><th></th></tr>
  <#list subscriptions as feed>  
  <#assign feedHref = feed.bookmark.link.url/>
  <#assign edtFeedHref = gzUrl("/settings/feeds/edit.action?feedUrl="+feed.bookmark.link.url?url)/>
  <#assign delFeedHref = gzUrl("/settings/feeds/unsub.action?feedUrl="+feed.bookmark.link.url?url)/>
  <tr class="feedRow">
  <td class="feedname">  
  <#assign bmId = feed.bookmark.id/>
  <a href="${gzBookmarkUrl(bmId?c)}" target="_blank" title="feed: ${feed.bookmark.title}">${feed.bookmark.title}</a>
  <div class="feed-notes">
   <a href="${feedHref}" class="bmark-link web-link" target="_blank">${prettyFormatUrl(feedHref)}</a><br/>
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
  <td class="settings"><a href="${edtFeedHref}">edit</a> | <a href="${delFeedHref}">unsubscribe</a></td>
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
     <@ww.textfield id="new-feed-url" label="feed url" name="feedUrl" value="http://"/>
     <@ww.submit cssClass="btn" value="subscribe"/>
    </@ww.form>
    <@displayActionMessage action=action/>
  </div> 
</#if>    
</@pageContent>
<@pageEnd/>
