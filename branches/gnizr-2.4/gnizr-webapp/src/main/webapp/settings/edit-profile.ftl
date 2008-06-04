<#-- /settings/edit-profile.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>
<#assign title="change profile"/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
<@headerBlock>
</@headerBlock>
<@pageContent>
<#assign bct = settingsBCT(loggedInUser.username)/>
<#assign bct = bct + [gzBCTPair('change profile', gzUrl("/settings/changeProfile.action"))]/> 
<#if (editUser.username)?exists && (loggedInUser.username != editUser.username)>
  <#assign bct = bct + userhomeBCT(editUser.username)/> 
</#if>
<@infoBlock bct=bct/>	
<@mainBlock>
<@pageTitle>Edit User Profile</@pageTitle>

<#-- print action message if there is any -->
<#if action.actionMessages?has_content>
<div class="system-message">
  <@ww.actionmessage/>
</div>    
</#if>
  <@pageDescription>
  You can change user profile. All fields with '*' are required.
  </@pageDescription>
  
  <@formInput>
  <@ww.actionmessage/>    
<@ww.form action="updateProfile" method="post">
  <@ww.textfield cssClass="text-input-fullname" label="Full name" name="fullname" value="${loggedInUser.fullname}" required="true"/>
  <@ww.textfield cssClass="text-input-email" label="Email" name="email" value="${loggedInUser.email}" required="true"/>  
  <@ww.submit value="Save" cssClass="btn"/>
</@ww.form>
  </@formInput>
</@mainBlock>
</@pageContent>
<@pageEnd/>
