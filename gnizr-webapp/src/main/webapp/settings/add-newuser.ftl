<#-- /settings/edit-profile.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>
<#assign title="add new users"/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>

<@headerBlock/>

<@pageContent>
<#assign bct = settingsBCT(loggedInUser.username)/> 
<@infoBlock bct=bct/>
<@mainBlock>  
  <h3>Change Password</h3>
  <@ww.form cssClass="userInputForm" action="addNewUser.action" method="post">
    <@ww.textfield label="Username" name="editUser.username" 
       value="${(editUser.username)?if_exists}" required="true"/>
    <@ww.password label="Password" name="editUser.password" 
       value="${(editUser.password)?if_exists}" required="true"/>    
    <@ww.textfield label="Full name" name="editUser.fullname" 
       value="${(editUser.fullname)?if_exists}" required="true"/>
    <@ww.textfield label="Email" name="editUser.email" 
       value="${(editUser.email)?if_exists}" required="true"/>                    
    <@ww.submit value="add user" cssClass="btn"/>
  </@ww.form>
  <@ww.actionmessage/>
  <p>
  <@ww.url id="editUserHref" namespace="/admin" action="editUser.action" includeParams="none"/>
  Back to <a href="${editUserHref}">manage user account</a>
  </p>
</@mainBlock>

</@pageContent>

<@pageEnd/>

</@ensureUserLoggedIn>