<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="send verification request" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Sent Verification Request</@pageTitle>
<p>A verification request message has been sent to 
the email address that is associated with your account.</p>
<p>To reset your password, follow the instructions in
the message.</p>  
</div>

</div>
<div class="cleardiv"/>
<@pageEnd/>