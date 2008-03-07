<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock>
[
<#list bookmarks as bookmark>
 <@bookmarkJson bookmark=bookmark/>
 <#if bookmark_has_next>,</#if>
</#list>
]
</@callbackBlock>