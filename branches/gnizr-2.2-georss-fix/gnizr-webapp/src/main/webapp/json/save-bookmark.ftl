<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock>
<#if (editBookmark.id > 0)>
<@bookmarkJson bookmark=editBookmark/>
<#else>
-1
</#if>
</@callbackBlock>

