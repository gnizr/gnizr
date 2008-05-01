<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="password changed">

<div class="frontPageMessage">
<@pageTitle>Successfully Changed Password</@pageTitle>
<p>Your password has been changed!</p>
<p>Click <a href="${gzUrl("/login")}" class="system-link">here</a> to login</p>
</div>

</@frontPage>