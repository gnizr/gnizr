<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="forgot password">

<div class="frontPageMessage">
<@pageTitle>Reset Password</@pageTitle>
<p>
To reset your password, provide your username.
</p>
<#if (action.actionErrors)?has_content>
<p>
<ul class="formErrors"> 
    <li class="errorMessage">No email is associated with this username.</li>
</ul>  
</p>
</#if>
<@ww.form namespace="/password" action="requestReset.action" method="post">
<@ww.textfield label="Username" name="username" size="25"/>
<@ww.submit cssClass="btn" value="Submit"/>
</@ww.form>
</div>
</@frontPage>
