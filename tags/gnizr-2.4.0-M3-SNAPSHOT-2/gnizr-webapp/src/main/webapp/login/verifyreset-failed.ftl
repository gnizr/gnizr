<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="verification failed" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Verification Failed</@pageTitle>
<p>Sorry! We can't proceed to reset your password.</p>
<p>Your password reset request may be expired or invalid.</p>  
<p><a href="${gzUrl("/password/forgot.action")}" class="system-link">Create a new request</a></p>
</div>
</div>
<div class="cleardiv"/>
<@pageEnd/>