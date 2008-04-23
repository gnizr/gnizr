<#include "/lib/web/macro-lib.ftl"/>
<#-- if logged in, go to the home page of the user -->
<@goHome>
<@pageBegin pageTitle="login" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          


<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<@formInput id="siteLogin">
<@pageTitle>User Login</@pageTitle>
<@displayActionError action=action/>
<@ww.form action="userLogin.action" method="post">
<@ww.textfield label="Username" name="user.username"/>
<@ww.password label="Password" name="user.password"/>
<@ww.checkbox label="Remember me" name="rememberMe" value="aBoolean"/>
<#if redirectToPage?exists>
  <@ww.hidden name="redirectToPage" value="${redirectToPage}"/>
</#if>
<@ww.submit cssClass="btn" value="Login"/>
</@ww.form>
<div id="siteLoginHelp">
<a href="${gzUrl("/password/forgot.action")}" class="system-link">I forgot my password</a>
</div>
</@formInput>
</div>

<div id="siteFeatures">
<@pageTitle>New user?</@pageTitle>
<#if gzIsUserRegistrationOpen() == true>
<p><a href="${gzUrl('/register')}" class="large-text system-link">Create an account</a></p>
<#elseif (gnizrConfiguration.siteContactEmail)?exists && 
  (gnizrConfiguration.siteContactEmail?length > 0)>
<p><a href="mailto:${gnizrConfiguration.siteContactEmail}" class="large-text system-link">Request for an account</a></p>
<#else>
<p class="large-text">Contact your administrator.</p>
</#if>
</div>
<div class="cleardiv"/>
<@pageEnd/>
</@goHome>


