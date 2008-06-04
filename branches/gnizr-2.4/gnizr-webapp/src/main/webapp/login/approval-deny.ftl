<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="denied registration">
<div class="frontPageMessage">
<@pageTitle>Denied User Registration: ${username}</@pageTitle>
<p>New user account has been denied. 
The user account will remain <b>inactive</b> in the system.</p>
<p>No additional actions are required</p>  
</div>
</@frontPage>