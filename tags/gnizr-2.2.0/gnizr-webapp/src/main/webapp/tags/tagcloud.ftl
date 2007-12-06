<#-- /tags/tagcloud.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to TAGS -->
<#include "./macro-lib.ftl"/>

<#assign title="tag cloud: popular tags on gnizr"/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-tagcloud.css")]/>
<@headerBlock>
</@headerBlock>
<@pageContent>
 <#assign bct = communityBCT() + [gzBCTPair('popular tags',gzUrl('/tags'))]/>
 <@infoBlock bct=bct/>
 <@mainBlock>
    <@tagCloudList tagCloudMap=tagCloudMap sortedTags=sortedTags />
 </@mainBlock>    
</@pageContent>

<@pageEnd/>