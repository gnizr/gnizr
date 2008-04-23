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
<@pageTitle>Successfully Changed Password</@pageTitle>
<p>Your password has been changed!</p>
<p>Click <a href="${gzUrl("/login")}" class="system-link">here</a> to login</p>
</div>

</div>
<div class="cleardiv"/>
<@pageEnd/>