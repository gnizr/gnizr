<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock>{<#list commonTags as ltag>"${ltag.tag.label?html}":${ltag.count}<#if ltag_has_next>,</#if></#list>}</@callbackBlock>
