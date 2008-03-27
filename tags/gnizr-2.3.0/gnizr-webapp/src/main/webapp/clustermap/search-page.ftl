<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#if (queryString?exists)>
  <#assign title="search '${queryString}' in clustermap -- gnizr"/>  
  <#assign sourceUrl=gzFullUrl("/data/clustermap/search.action?queryString=${queryString?url}&type=${type?default('text')}")/>
<#else>
  <#assign title = "search in clustermap -- gnizr"/>
</#if>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-clustermap.css")]/>
<@headerBlock/>

<@pageContent>
<#assign bct = communityBCT() + searchBCT() +
               [gzBCTPair('clustermap view',gzUrl("/clustermap/search"))]/>
<@infoBlock bct=bct/>
<div id="searchForm">
<table>
<@ww.form action="search" namespace="/clustermap" theme="simple">
<tr>
<td>
<@ww.textfield id="searchQuery" name="queryString" value="${queryString?if_exists}"/>
</td>
<td>
<@ww.submit id="searchButton" cssClass="btn" value="Search & Clustermap"/>
</td>
</tr>
<@ww.hidden name="type" value="text"/>
</@ww.form>
</table>
</div>
<#--
<#if (queryString?exists)>
  search query: 
   <#list queryString?split("\\s+","r") as st>
     <@ww.url id="subQ" queryString="${st?url}" type="${type?default('text')}"/>
   '<a href="${subQ}" title="">${st}</a>'
   </#list>
</#if>
-->

<div id="clustermap">
<applet code="com.gnizr.core.web.clustermap.ClusterMapApplet.class" 
        codebase="${gzUrl("/applets")}"
        archive="gnizr-clustermap-applet.jar"
        width="100%" height="100%">
  <#if sourceUrl?exists>
    <param name="datasourceurl" value="${sourceUrl}">
  </#if>
</applet>
</div>
</@pageContent>

<@pageEnd/>