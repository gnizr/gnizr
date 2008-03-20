<#-- /settings/import-delicious.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>
<#assign title="rebuild bookmark search index -- ${loggedInUser.username}"/>
<#assign username=loggedInUser.username/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
<@headerBlock>
</@headerBlock>

<@pageContent>
  <#assign bct = settingsBCT(username) + [gzBCTPair('rebuild search index',gzUrl('/settings/indexBookmark.action'))]/>
  <@infoBlock bct=bct/>
<@mainBlock>  
<#if (status?exists == false)>
<p>To rebuild search index database, click the button in below.</p>
<@ww.form action="updateIndex.action" namespace="/settings" method="post">
  <@ww.submit cssClass="btn" value="Rebuild Index Now"/>
</@ww.form>
<#else>
Search index created sucessfully!
<ul>
<li>Total number of bookmark indexed: ${status.bookmarkIndexed?c}</li>
</ul>
</#if>
</@mainBlock>
</@pageContent>
<@pageEnd/>
</@ensureUserLoggedIn>