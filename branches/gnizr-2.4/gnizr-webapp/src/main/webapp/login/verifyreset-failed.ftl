<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="verification failed">

<div class="frontPageMessage">
<@pageTitle>Verification Failed</@pageTitle>
<p>Sorry! We can't proceed to reset your password.</p>
<p>Your password reset request may be expired or invalid.</p>  
<p><a href="${gzUrl("/password/forgot.action")}" class="system-link">Create a new request</a></p>
</div>

</@frontPage>
