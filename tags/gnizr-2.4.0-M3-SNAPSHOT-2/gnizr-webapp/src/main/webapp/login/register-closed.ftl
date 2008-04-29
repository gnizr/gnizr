<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="user registration closed" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>User Registration Closed</@pageTitle>
<p>All user registration functions are closed.</p>
<p>Pleae come back later.</p>  
</div>

</div>
<div class="cleardiv"/>
<@pageEnd/>