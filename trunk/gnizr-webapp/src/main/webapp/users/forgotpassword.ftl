<#-- /settings/edit-profile.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#assign title="change profile"/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>

<@headerBlock>
</@headerBlock>
<@pageContent>
<@mainBlock>
hello world.
</@mainBlock>
</@pageContent>

<@pageEnd/>