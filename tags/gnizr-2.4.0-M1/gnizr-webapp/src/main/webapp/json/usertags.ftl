<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock>{<#list userTags as utag>"${utag.tag.label?html}":${utag.count?c}<#if utag_has_next>,</#if></#list>}</@callbackBlock>
