<#include "/lib/web/macro-lib.ftl"/>
<?xml version="1.0"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
<#if (gnizrConfiguration.siteName?exists) >
<ShortName>${gnizrConfiguration.siteName?html}</ShortName>
<#else>
<ShortName>gnizr</ShortName>
</#if>
<#if (gnizrConfiguration.siteDescription?exists)>
<Description>${gnizrConfiguration.siteDescription?html}</Description>
<#else>
<Description>Gnizr</Description>
</#if>
<Image height="16" width="16" type="image/x-icon">${gzUrl('/images/favicon.ico')}</Image>
<#assign url = gzUrl('/search/list.action?q={searchTerms}')/>
<Url type="text/html" method="get" template="${url}"/>
</OpenSearchDescription>