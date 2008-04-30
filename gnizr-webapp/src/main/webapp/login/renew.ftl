<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="renew account activation">

<div class="frontPageMessage">
<@pageTitle>Renew Account Activation</@pageTitle>
<p>
To initiate the activation of your account, provide your username.
</p>
<#if (action.actionErrors)?has_content>
<p>
<ul class="formErrors"> 
    <li class="errorMessage">Invalid username or this account is already active.</li>
</ul>  
</p>
</#if>
<@ww.form namespace="/register" action="requestRenew.action" method="post">
<@ww.textfield label="Username" name="username" size="25"/>
<@ww.submit cssClass="btn" value="Submit"/>
</@ww.form>
</div>
</@frontPage>
