<#setting url_escaping_charset="UTF-8">

<#-- 
  FUNCTION: getSIOCForumUri
-->
<#function getSIOCForumUri user>
  <#return gzFullUrl("/rdf/user/"+user.username)/>
</#function>

<#function getSIOCPostUri bookmark>
  <#return gzFullUrl("/rdf/bookmark/user/"+bookmark.user.username+"/url/"+bookmark.link.url?url)/>
</#function>

<#function getSIOCPostLink bookmark>
  <#return gzFullUrl("/redirect/user/"+bookmark.user.username+"/url/"+bookmark.link.url?url)>
</#function>

<#function getTagUri tagLabel>
  <#if tagLabel?contains('&') || tagLabel?contains('+')> 
    <#local tagHref = gzFullUrl("/data/rdf/tag.action?tag="+tagLabel?url)/>
    <#return tagHref/>
  <#else>
    <#return gzFullUrl("/rdf/tag/"+tagLabel?url)/>
  </#if>
</#function>
