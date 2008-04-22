<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="login" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<@formInput id="forgotPassword">
<@pageTitle>Request Password Reset</@pageTitle>
<@ww.form namespace="/password" action="requestReset.action" method="post">
<@ww.textfield label="Username" name="username"/>
<@ww.submit cssClass="btn" value="Submit"/>
</@ww.form>
</@formInput>

<div class="cleardiv"/>
<@pageEnd/>