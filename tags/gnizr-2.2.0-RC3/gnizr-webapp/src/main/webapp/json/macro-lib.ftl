<#include "/lib/web/json-macro-lib.ftl"/>
<#macro callbackBlock>
<#if Parameters.callback?exists>
${Parameters.callback}(<#nested/>)
<#else>
<#nested/>
</#if>
</#macro>