<#-- /webapp/users/home.ftl -->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- 
hack: because different actions have different naming of a user variable.
needs to refactor actions to have a uniformed naming of user variable.
-->
<#if username?exists == false && (user.username)?exists == true>
  <#assign username=user.username/>
</#if>

<#assign title="${username}'s bookmarks"/>
<#assign thisPageHref = gzUserUrl(username)/>
${session.setAttribute("thisPageHref",thisPageHref)}
<#if tag?exists>
  <#assign title=title+" tagged ${tag}"/>
  <#assign thisPageHref = gzUserTagUrl(username,tag)/>
</#if>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-userpage.css")]/>

<@headerBlock/>

<@pageContent>
<@infoBlock>
saved bookmarks of <a href="${gzUserUrl(username)}">${username}</a>
<#if tag?exists>
tagged <a href="${gzTagUrl(tag)}">${tag}</a>
</#if>
</@infoBlock>
  <@mainBlock>
  <ul>
    <li>No bookmarks here!</li>
  </ul>
  </@mainBlock>
</@pageContent>

<@pageEnd/>