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
  <#if loggedInUser.username == "gnizr" && editUser?exists>
  <@ww.form cssClass="userInputForm" action="saveProfile.action" method="post">
    <@ww.textfield label="Username" name="editUser.username" readonly="true"/>
    <@ww.textfield label="Full name" name="editUser.fullname" 
       value="${editUser.fullname}"/>
    <@ww.textfield label="Email" name="editUser.email" 
       value="${editUser.email}" />                    
    <@ww.submit value="change" cssClass="btn"/>
  </@ww.form>
  
  <@ww.form cssClass="userInputForm" action="changePassword.action" method="post">   
    <@ww.textfield label="Username" name="editUser.username" value="${editUser.username}" readonly="true"/>            
    <@ww.password label="New password" name="editUser.password" value="${editUser.password?if_exists}"/>       
    <@ww.submit value="reset" cssClass="btn"/>
  </@ww.form>
  <@ww.actionmessage cssClass="errorMessage"/>
  <#else>  
    <@changeProfileForm email=email?default(loggedInUser.email) fullname=fullname?default(loggedInUser.fullname)/>
  </#if>
  

</@mainBlock>
</@pageContent>

<@pageEnd/>

</@ensureUserLoggedIn>