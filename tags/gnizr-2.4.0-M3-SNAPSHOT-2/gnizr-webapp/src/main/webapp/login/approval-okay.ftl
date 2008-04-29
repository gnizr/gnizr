<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="approved registration" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Approved User Registration: ${username}</@pageTitle>
<p>New user account has been approved. A notification message has 
been sent to the user's email address.</p>
<p>No additional actions are required</p>  
</div>
</div>
<div class="cleardiv"/>
<@pageEnd/>