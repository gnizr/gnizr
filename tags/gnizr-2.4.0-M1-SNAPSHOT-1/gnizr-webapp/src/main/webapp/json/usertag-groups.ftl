<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@callbackBlock>{<#if userTagGroups?exists><#assign tagGroupNames = userTagGroups?keys/>
<#list tagGroupNames as grp>"${formatGroupName(grp)}":[<#assign grpMembers = userTagGroups[grp]/><#list grpMembers as grpTag>"${grpTag.tag.label?html}"<#if grpTag_has_next>,</#if></#list>]<#if grp_has_next>,</#if></#list></#if>}</@callbackBlock>

<#function formatGroupName gname>
  <#if (gname == "")>
    <#return "(default)"?html/>
  </#if>
  <#return gname?html/>
</#function>