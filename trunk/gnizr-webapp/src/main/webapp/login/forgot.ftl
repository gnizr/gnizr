<#include "/lib/web/macro-lib.ftl"/>
<@pageBegin pageTitle="login" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">   
       

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Reset Password</@pageTitle>
<p>
To reset your password, provide your username.
</p>
<#if (action.actionErrors)?has_content>
<p>
<ul class="formErrors"> 
    <li class="errorMessage">No email is associated with this username.</li>
</ul>  
</p>
</#if>
<@ww.form namespace="/password" action="requestReset.action" method="post">
<@ww.textfield label="Username" name="username" size="25"/>
<@ww.submit cssClass="btn" value="Submit"/>
</@ww.form>
</div>

<div class="cleardiv"/>
<@pageEnd/>