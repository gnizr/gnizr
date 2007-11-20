<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign queryString=q?if_exists/>
<#assign thisPageBaseHref=gzUrl("/search.action")/>  
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-search.css")]>                       
<script type="text/javascript" src="${gzUrl("/lib/javascript/opensearch.js")}"></script> 
<script type="text/javascript">
 services = <@openSearchSrvJson services=services/>;
 imagePathUrl = '${gzUrl("/images")}';
 queryTerm = '${q?if_exists?html}';
 proxyUrl = '${gzUrl("/data/json/searchproxy.action?searchUrl=")}';
<#if loggedInUser?exists>
 loggedInUser = '${loggedInUser.username}'; 
</#if>
</script>
</@pageBegin>            
<@headerBlock/>
<@pageContent>              
<#if q?exists>
  <#assign bct=searchBCT() + [gzBCTPair(q,'')]/>
<#else>
  <#assign bct=searchBCT()/>  
</#if>
<@infoBlock bct=bct/>
<div id="searchPageContent">
<div id="left">
<table id="resultTiles">
<tbody>
<tr id="resultTilesRow">
</tr>
</tbody>
</table>
</div>
<div id="right">
<div class="searchSideBlock">
<ul id="selectSearchServices">
</ul>
</div>
</div>
</@pageContent>
<@pageEnd/>