<#-- /settings/import-delicious.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<@ww.url id="thisPageHref" method="post" includeParams="none"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>
<#assign title="update bookmark ownership"/>
<#assign username=loggedInUser.username/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]>
<meta http-equiv="refresh" content="5;url=${thisPageHref}"/>
</@pageBegin>
<@headerBlock>
</@headerBlock>

<@pageContent>
<p class="notice-block">
<span class="large-text">Please wait... Updating user bookmark ownership...</span> 
<br/><br/>
If this page does not refresh, <a href="${thisPageHref}">click here</a>.
</p>
<p class="notice-block">
<img src="${gzUrl("/images/progress-bar.gif")}"/>
</p>
<ul>
<#list userInProcess as user>
<li>processing bookmarks of ${user.username} .... </li>
</#list>
</ul>
</@pageContent>
<@pageEnd/>
</@ensureUserLoggedIn>