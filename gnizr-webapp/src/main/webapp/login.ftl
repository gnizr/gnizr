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
<@ww.form action="userLogin.action" method="post">
<@ww.textfield label="Username" name="user.username"/>
<@ww.password label="Password" name="user.password"/>
<@ww.checkbox label="Remember me" name="rememberMe" value="aBoolean"/>
<#if redirectToPage?exists>
  <@ww.hidden name="redirectToPage" value="${redirectToPage}"/>
</#if>
<@ww.submit cssClass="btn" value="login"/>
<@ww.actionerror/>
</@ww.form>
</@formInput>
</div>

<div id="siteFeatures">
<#if gzIsUserRegistrationOpen() == true>
<@pageTitle>New User?</@pageTitle>
<p><a href="${gzUrl('/register')}" class="large-text system-link">Create an account</a></p>
</#if>
</div>
<div class="cleardiv"/>
<@pageEnd/>
</@goHome>


