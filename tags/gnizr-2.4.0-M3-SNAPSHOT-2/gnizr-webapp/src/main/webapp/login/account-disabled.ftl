<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="user account disabled" 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      

<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>User Account Disabled</@pageTitle>
<p>The account you tried to login has been disabled by the administrator.</p>
<#if (gnizrConfiguration.siteContactEmail)?exists && 
  (gnizrConfiguration.siteContactEmail?length > 0)>
<p>Contact the <a href="mailto:${gnizrConfiguration.siteContactEmail}" class="system-link">administrator</a></p>
<#else>
<p>Contact the administrator.</p>
</#if>  
</div>

</div>
<div class="cleardiv"/>
<@pageEnd/>