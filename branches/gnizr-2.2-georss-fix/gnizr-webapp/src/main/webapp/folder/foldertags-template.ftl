<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#if folderTags?exists>
  <@sidebarElement> 
    <@tagCloudList tagLabels=folderTags tagHrefPrefix=thisPageBaseHref/>      
    <@tagCloudOptions pageHref=thisPageHref 
                      redirectUrl=toPageHref
                      minTagFreq=minTagFreq?if_exists
                      sortBy=sortBy?if_exists
                      tagView=tagView?if_exists/>
  </@sidebarElement> 
</#if>