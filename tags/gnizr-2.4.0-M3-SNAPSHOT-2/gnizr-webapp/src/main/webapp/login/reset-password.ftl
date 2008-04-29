<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="reset password" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">   
       

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Reset Password</@pageTitle>
<@displayActionError action=action/>
<@ww.form namespace="/password" action="resetPassword.action" method="post">
<@ww.password label="New Password" name="password" size="25"/>
<@ww.password label="Confirm New Password" name="passwordConfirm" size="25"/>
<@ww.hidden name="username" value="${username}"/>
<@ww.hidden name="token" value="${token}"/>
<@ww.submit cssClass="btn" value="Save"/>
</@ww.form>
</div>

<div class="cleardiv"/>
<@pageEnd/>