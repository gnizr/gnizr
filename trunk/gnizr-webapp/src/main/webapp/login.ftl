<#include "/lib/web/macro-lib.ftl"/>
<#-- if logged in, go to the home page of the user -->
<@goHome>
<@pageBegin pageTitle="login" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]/>          
<div id="headline">
<#if (gnizrConfiguration.siteName)?exists>
<h1>${gnizrConfiguration.siteName?html}</h1>
<#else>
<h1>gnizr<span class="version">${gnizrVersion()}</span></h1>
</#if>
<br></br>
<#if (gnizrConfiguration.siteDescription)?exists>
<h2>${gnizrConfiguration.siteDescription?html}</h2>
<#else>
<h2>organize</h2>
</#if>
</div>
<div id="login">
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
<#if gzIsUserRegistrationOpen() == true>
<p>
Not yet a user? <a href="${gzUrl("/register")}">Click here</a>
</p>
</#if>
</div>
<@pageEnd/>
</@goHome>


