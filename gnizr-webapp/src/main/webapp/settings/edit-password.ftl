<#-- /settings/edit-password.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>

<#assign title="${loggedInUser.username}'s settings"/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]>
</@pageBegin>

<@headerBlock>
</@headerBlock>

<@pageContent>
<#assign bct = settingsBCT(username)/>
<#assign bct = bct + [gzBCTPair('change password', gzUrl("/settings/changePassword.action"))]/> 
<@infoBlock bct=bct/>	
<@pageTitle>Chanage Password</@pageTitle>

<@pageDescription>
You can change your login password. The number of characters in your password must be between 5-20. 
</@pageDescription>

<@formInput>
<@ww.actionmessage/>
<@ww.form action="updatePassword" method="post">
  <@ww.password cssClass="text-input-password" label="New Password" name="password"/>
  <@ww.password cssClass="text-input-password" label="Confirm Password" name="passwordConfirm"/>  
  <@ww.submit value="Save" cssClass="btn"/>
</@ww.form>
</@formInput>
</@pageContent>

<@pageEnd/>

</@ensureUserLoggedIn>