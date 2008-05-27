<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="user account disabled">

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

</@frontPage>