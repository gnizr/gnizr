<#-- /settings/import-delicious.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>
<#assign title="update bookmark search index -- ${loggedInUser.username} -- gnizr"/>
<#assign username=loggedInUser.username/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
<@headerBlock>
</@headerBlock>

<@pageContent>
  <#assign bct = settingsBCT(username) + [gzBCTPair('update search index',gzUrl('/settings/indexBookmark.action'))]/>
  <@infoBlock bct=bct/>
<@mainBlock>  
<#if (status?exists == false)>
<p>To update bookmark, click the button in below.</p>
<@ww.form action="updateIndex.action" namespace="/settings" method="post">
  <@ww.submit cssClass="btn" value="Update Now"/>
</@ww.form>
<#else>
Successfully updated search index.
<ul>
<li>Total number of bookmark index: ${status.bookmarkIndexed}</li>
</ul>
</#if>
</@mainBlock>
</@pageContent>
<@pageEnd/>
</@ensureUserLoggedIn>