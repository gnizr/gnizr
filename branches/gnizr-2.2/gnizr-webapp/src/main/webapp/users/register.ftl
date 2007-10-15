<#-- users/register.ftl -->

<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to USERS -->
<#include "./macro-lib.ftl"/>

<#-- if a user is already logged in, 
     redirect to the home page of the user -->
<@goHome>
<#assign title="new user registration -- gnizr"/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-frontpage.css")]/>

<div id="headline">
<h1>gnizr</h1>
<h2>organize</h2>
</div>
<div id="register">
<h2>User Registration</h2>
<@ww.form action="/register.action" method="post">
<@ww.actionerror/>
<@ww.textfield label="Username" name="user.username" required="true"/>
<@ww.password  label="Password" name="user.password" required="true"/>
<@ww.password label="Confirm Password" name="checkPassword" required="true"/>
<@ww.textfield  label="Full Name" name="user.fullname" required="true"/>
<@ww.textfield label="Email" name="user.email" required="true"/>
<@ww.submit value="submit" cssClass="btn"/>
</@ww.form>
</div>
<@pageEnd/>
</@goHome>