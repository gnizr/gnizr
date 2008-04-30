<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="verification failed">

<div class="frontPageMessage">
<@pageTitle>Verification Failed</@pageTitle>
<p>Sorry! We can't proceed to activate your account.</p>
<p>The account activation request may be expired or invalid.</p>  
<p><a href="${gzUrl("/register/renewUser.action")}" class="system-link">Create a new request</a></p>
</div>

</@frontPage>