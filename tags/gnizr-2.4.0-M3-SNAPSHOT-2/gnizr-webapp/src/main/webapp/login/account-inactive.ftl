<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="inactive user account" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<#if gzIsRegistrationApprovalRequired() == true>
<div class="frontPageMessage">
<@pageTitle>Registration Approval Required</@pageTitle>
<p>This account requires registration approval by the system administration. Once your 
account is approved, you will receive a notification via email.</p>
<#if (gnizrConfiguration.siteContactEmail)?exists && 
  (gnizrConfiguration.siteContactEmail?length > 0)>
<p>Questions? Contact the <a href="mailto:${gnizrConfiguration.siteContactEmail}" class="system-link">administrator</a></p>
<#else>
<p>Questions? Contact the administrator.</p>
</#if>  
</div>
<#else>
<div class="frontPageMessage">
<@pageTitle>Email Verification Required</@pageTitle>
<p>The account you tried to login requires email verification. Follow the link
below to verify this information.</p>
<#if (gnizrConfiguration.siteContactEmail)?exists && 
  (gnizrConfiguration.siteContactEmail?length > 0)>
<p>Questions? Contact the <a href="mailto:${gnizrConfiguration.siteContactEmail}" class="system-link">administrator</a></p>
<#else>
<p>Questions? Contact the administrator.</p>
</#if>  
<br/>
<p><a href="${gzUrl("/register/renewUser.action")}" class="large-text system-link">Verify email</a></p>
</div>
</#if>

</div>
<div class="cleardiv"/>
<@pageEnd/>