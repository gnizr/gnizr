<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="login">
<@formInput id="siteLogin">
<@pageTitle>User Login</@pageTitle>
<#if (action.actionErrors)?has_content>
<ul class="formErrors">
    <li class="errorMessage">Username and password do not match.</li>
</ul>  
</#if>
<!-- input form starts -->
<@ww.form action="userLogin.action" method="post">
<@ww.textfield label="Username" name="user.username"/>
<@ww.password label="Password" name="user.password"/>
<@ww.checkbox label="Remember me" name="rememberMe" value="aBoolean"/>
<#if redirectToPage?exists>
  <@ww.hidden name="redirectToPage" value="${redirectToPage}"/>
</#if>
<@ww.submit cssClass="btn" value="Login"/>
</@ww.form>
<!-- input form ends -->
<!-- forgot passwd starts -->
  <div id="siteLoginHelp">
    <a href="${gzUrl("/password/forgot.action")}" class="system-link">I forgot my password</a>
  </div>
<!-- forgot passwd ends-->
</@formInput>

<div id="siteFeatures">
<@pageTitle>New user?</@pageTitle>
<#if gzIsUserRegistrationOpen() == true>
<p><a href="${gzUrl('/register')}" class="large-text system-link">Create an account</a></p>
<#elseif (gnizrConfiguration.siteContactEmail)?exists && 
  (gnizrConfiguration.siteContactEmail?length > 0)>
<p><a href="mailto:${gnizrConfiguration.siteContactEmail}" class="large-text system-link">Request an account</a></p>
<#else>
<p class="large-text">Contact your administrator.</p>
</#if>
</div>
</@frontPage>

