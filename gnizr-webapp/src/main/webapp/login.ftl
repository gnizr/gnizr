<#include "/lib/web/macro-lib.ftl"/>
<#-- if logged in, go to the home page of the user -->
<@goHome>
<@pageBegin pageTitle="gnizr -- organize." 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]/>          
<div id="headline">
<h1>gnizr<span class="version">${gnizrVersion()}</span></h1>
<br></br>
<h2>organize</h2>
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


