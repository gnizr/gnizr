<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="registration approval failed" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Registration Approval Failed</@pageTitle>
<p>Unable to process registration approval/denial request</p>
<p>The request may be expired or invalid.</p>  
<#if username?exists>
<ul>
<li>Username: ${username}</li>
</ul>
</#if>
<p><a href="${gzUrl("/register/renewUser.action")}" class="system-link">Create a new request</a></p>
</div>
</div>
<div class="cleardiv"/>
<@pageEnd/>