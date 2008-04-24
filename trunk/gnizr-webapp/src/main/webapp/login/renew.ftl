<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="renew account activation" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">   
       

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Renew Account Activation</@pageTitle>
<p>
To initiate the activation of your account, provide your username.
</p>
<#if (action.actionErrors)?has_content>
<p>
<ul class="formErrors"> 
    <li class="errorMessage">Invalid username or this account is already active.</li>
</ul>  
</p>
</#if>
<@ww.form namespace="/register" action="requestRenew.action" method="post">
<@ww.textfield label="Username" name="username" size="25"/>
<@ww.submit cssClass="btn" value="Submit"/>
</@ww.form>
</div>

<div class="cleardiv"/>
<@pageEnd/>