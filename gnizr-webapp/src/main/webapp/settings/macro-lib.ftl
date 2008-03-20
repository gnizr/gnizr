<#-- settings/macro-lib.ftl -->
<#-- assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#--
MACRO: settingsBlock
INPUT: title: string // title of this settings block
-->
<#macro settingsBlock title>
<p class="settings">
<h4>${title}</h4>
  <#nested/>
</p>
</#macro>

<#--
MACRO: changePasswordForm
INPUT: NONE
-->
<#macro changePasswordForm>
<@ww.actionmessage/>
<@ww.form cssClass="userInputForm" action="updatePassword" method="post">
  <@ww.password cssClass="text-input-password" label="New Password" name="password"/>
  <@ww.password cssClass="text-input-password" label="Confirm Password" name="passwordConfirm"/>  
  <@ww.submit value="change" cssClass="btn"/>
</@ww.form>
</#macro>

<#--
MACRO: updateProfileForm
INPUT: NONE
-->
<#macro changeProfileForm fullname="" email="">
<@ww.actionmessage/>
<@ww.form cssClass="userInputForm" action="updateProfile" method="post">
  <@ww.textfield cssClass="text-input-fullname" label="Full name" name="fullname" value="${fullname}"/>
  <@ww.textfield cssClass="text-input-email" label="Email" name="email" value="${email}"/>  
  <@ww.submit value="change" cssClass="btn"/>
</@ww.form>
</#macro>

<#--
MACRO: importDeliciousForm
INPUT: NONE
-->
<#macro importDeliciousForm>
<@ww.form cssClass="userInputForm" action="importPosts.action" method="post">
  <@ww.textfield cssClass="text-input-username" label="del.icio.us username" name="deliciousUsername"/>
  <@ww.password cssClass="text-input-password" label="del.icio.us password" name="deliciousPassword"/>
  <@ww.submit value="import" cssClass="btn"/>
</@ww.form>
</#macro>
