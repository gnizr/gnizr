<#-- /settings/edit-password.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>

<#assign title="${loggedInUser.username}'s settings -- gnizr"/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]>
</@pageBegin>

<@headerBlock>
</@headerBlock>

<@pageContent>
<#assign bct = settingsBCT(username)/>
<#assign bct = bct + [gzBCTPair('change password', gzUrl("/settings/changepassword"))]/> 
<@infoBlock bct=bct/>	
  <@changePasswordForm/>
</@pageContent>

<@pageEnd/>

</@ensureUserLoggedIn>