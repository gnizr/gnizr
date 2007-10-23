<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock><#if totalCount?exists>${totalCount?c}<#else>0</#if></@callbackBlock>