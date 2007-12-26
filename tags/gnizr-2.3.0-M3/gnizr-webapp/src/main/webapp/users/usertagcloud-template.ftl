<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#if userTags?exists>
  <@sidebarElement>
    <@tagCloudList tagLabels=userTags tagHrefPrefix=thisPageBaseHref/>      
    <@tagCloudOptions pageHref=thisPageHref
                      redirectUrl=toPageHref
                      minTagFreq=minTagFreq?if_exists
                      sortBy=sortBy?if_exists
                      tagView=tagView?if_exists
    />
  </@sidebarElement>  
</#if>