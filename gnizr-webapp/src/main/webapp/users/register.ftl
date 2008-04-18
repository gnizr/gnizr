<#-- users/register.ftl -->

<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to USERS -->
<#include "./macro-lib.ftl"/>

<#-- if a user is already logged in, 
     redirect to the home page of the user -->
<@goHome>
<#assign title="new user registration"/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-frontpage.css")] 
            enableJS=false/>

<div class="frontPage">

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<@formInput id="siteRegister">
<@ww.form action="userRegister.action" method="post">
<@displayActionError action=action/>
<@ww.textfield label="Username" name="user.username" required="true"/>
<@ww.password  label="Password" name="user.password" required="true"/>
<@ww.password label="Confirm Password" name="checkPassword" required="true"/>
<@ww.textfield  label="Full Name" name="user.fullname" required="true"/>
<@ww.textfield label="Email" name="user.email" required="true"/>
<@ww.submit value="submit" cssClass="btn"/>
</@ww.form>
</@formInput>

<div id="siteFeatures">
<@pageTitle>Already have an account?</@pageTitle>
<p><a href="${gzUrl('/login')}" class="large-text system-link">Click to login</a></p>
</div>
</div>
<div class="cleardiv"/>
<@pageEnd/>
</@goHome>
