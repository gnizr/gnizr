<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="registration approval failed">

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

</@frontPage>