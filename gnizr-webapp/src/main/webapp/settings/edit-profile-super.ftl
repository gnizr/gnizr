<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign title="edit user profile"/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
<@headerBlock>
</@headerBlock>
<@pageContent>
<#assign bct = settingsBCT(loggedInUser.username)/>
<#assign bct = bct + [gzBCTPair('edit user profile', gzUrl("/admin/editUser.action"))]/> 
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
  <@ww.form cssClass="userInputForm" action="saveProfile.action" method="post">
    <@ww.textfield label="Username" name="editUser.username" readonly="true"/>
    <@ww.textfield label="Full name" name="editUser.fullname" 
       value="${editUser.fullname}" required="true"/>
    <@ww.textfield label="Email" name="editUser.email" 
       value="${editUser.email}" required="true"/>                                    
    <@ww.submit value="Save" cssClass="btn"/>
  </@ww.form>
  </@formInput>
  <hr width="80%"/>
  <@pageDescription>
  You can change login password. The number of characters in the password must be between 5-20. 
  </@pageDescription>
  
  <@formInput>
  <@ww.form action="changePassword.action" method="post">   
    <@ww.textfield label="Username" name="editUser.username" value="${editUser.username}" readonly="true"/>            
    <@ww.password label="New password" name="editUser.password" value="${editUser.password?if_exists}" required="true"/>       
    <@ww.submit value="Save" cssClass="btn"/>
  </@ww.form>
  </@formInput>
    <hr width="80%"/>
  <@pageDescription>
  You can change user account status.
  <ul>
    <li>ON HOLD: email address is not verified; user can't login</li>
    <li>ACTIVE: email address is verified; user can login</li>
    <li>DISABLED: user login is prohibited</li>
  </ul>
  </@pageDescription>
  <@formInput>
  <@ww.form cssClass="userInputForm" action="changeStatus.action" method="post">
    <@ww.textfield label="Username" name="editUser.username" readonly="true"/>                             
    <@ww.radio label="Change Account Status" name="editUser.accountStatus" list=r'#{0:"ON HOLD", 1:"ACTIVE", 2:"DISABLED"}' 
               value="${editUser.accountStatus}"/>
    <@ww.submit value="Save" cssClass="btn"/>
  </@ww.form>
  </@formInput>
</@mainBlock>
</@pageContent>
<@pageEnd/>
