<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="reset password">

<div class="frontPageMessage">
<@pageTitle>Reset Password</@pageTitle>
<@displayActionError action=action/>
<@ww.form namespace="/password" action="resetPassword.action" method="post">
<@ww.password label="New Password" name="password" size="25"/>
<@ww.password label="Confirm New Password" name="passwordConfirm" size="25"/>
<@ww.hidden name="username" value="${username}"/>
<@ww.hidden name="token" value="${token}"/>
<@ww.submit cssClass="btn" value="Save"/>
</@ww.form>
</div>

</@frontPage>