<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock>{
<#list userStats as us>
<#if (Session.loggedInUser.username)?exists>
  <#if Session.loggedInUser.username != us.username>
   '${us.username}':'${us.numOfTags?c}'
  <#else>
   '':'0'
  </#if>
<#else>  
'${us.username}':'${us.numOfTags?c}'
</#if>
<#if us_has_next>,</#if></#list>}</@callbackBlock>
