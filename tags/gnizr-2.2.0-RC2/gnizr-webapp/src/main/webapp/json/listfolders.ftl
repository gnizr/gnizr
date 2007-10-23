<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock>{<#list folders as folder>"${gzFormatFolderName(folder.name)?html}":${folder.size?c}<#if folder_has_next>,</#if></#list>}</@callbackBlock>
