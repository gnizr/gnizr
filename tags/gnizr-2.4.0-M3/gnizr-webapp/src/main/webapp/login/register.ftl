<#include "/lib/web/macro-lib.ftl"/>
<#include "/login/macro-lib.ftl"/>
<@frontPage title="new user registration">

<div class="frontPageMessage">
<@pageTitle>Create an Account</@pageTitle>
<p>Create a username and password. Your password must be at least 6 characters in length, 
and should not be easily guessed by others. We will send you a verification message 
to confirm that you own this address.</p>
<p>
If you already have an account, to login <a href="${gzUrl('/login')}" class="system-link">click here</a>.
</p>
<@formInput>
<@ww.form namespace="/register" action="createUser" method="post">
<#if (action.actionErrors)?has_content>
<p>
<ul class="formErrors"> 
    <li class="errorMessage">Username <b>${username}</b> is not available. Try a different one.</li>
</ul>  
</p>
</#if>
<@ww.textfield label="Username" name="username" required="true" size="25"/>
<@ww.password  label="Password" name="password" required="true" size="25"/>
<@ww.password label="Confirm Password" name="passwordConfirm" required="true" size="25"/>
<@ww.textfield label="Email" name="email" required="true" size="25"/>
<@ww.submit value="Submit" cssClass="btn"/>
</@ww.form>
</@formInput>
<p>
By submittting your registration information, you indicate that you 
accept the <a href="#">Terms of Service</a> and have read and understand the 
<a href="#">Our Privacy Policy</a>, and agree to be bound by both.
</p>
</div>
</@frontPage>