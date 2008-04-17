<#-- /settings/edit-profile.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>

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
  <#if loggedInUser.username == "gnizr" && editUser?exists>
  <@pageDescription>
  You can change user profile. All fields with '*' are required.
  </@pageDescription>
  <@formInput>
  <@ww.form cssClass="userInputForm" action="saveProfile.action" method="post">
    <@ww.textfield label="Username" name="editUser.username" readonly="true"/>
    <@ww.textfield label="Full name" name="editUser.fullname" 
       value="${editUser.fullname}" required="true"/>
    <@ww.textfield label="Email" name="editUser.email" 
       value="${editUser.email}" required="true"/>                    
    <@ww.submit value="Save" cssClass="btn"/>
  </@ww.form>
  </@formInput>
    
  <@pageDescription>
  You can change login password. The number of characters in your password must be between 5-20. 
  </@pageDescription>
  
  <@formInput>
  <@ww.form action="changePassword.action" method="post">   
    <@ww.textfield label="Username" name="editUser.username" value="${editUser.username}" readonly="true"/>            
    <@ww.password label="New password" name="editUser.password" value="${editUser.password?if_exists}" required="true"/>       
    <@ww.submit value="Save" cssClass="btn"/>
  </@ww.form>
  <@ww.actionmessage cssClass="errorMessage"/>
  </@formInput>
  <#else>  
  
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
  
  </#if>
</@mainBlock>
</@pageContent>

<@pageEnd/>

</@ensureUserLoggedIn>