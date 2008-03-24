<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign queryString=q?if_exists/>
<#assign thisPageBaseHref=gzUrl("/search/list.action")/>  
<#assign title="search " + q?if_exists?html/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-search.css")]>                       
<script type="text/javascript" src="${gzUrl("/lib/javascript/opensearch.js")}"></script> 
<script type="text/javascript" src="${gzUrl("/lib/javascript/suggest-searchterms.js")}"></script>
<script type="text/javascript">
 services = <@openSearchSrvJson services=services/>;
 imagePathUrl = '${gzUrl("/images")}';
 queryTerm = '${q?if_exists?js_string}';
 proxyUrl = '${gzUrl("/data/json/searchproxy.action?searchUrl=")}';
 bookmarkDetailUrl = '${gzUrl("/bookmark/id")}';
 <#-- global variables defined in suggest-searchterms.js -->
 searchUrl = '${thisPageBaseHref}';
 <#-------------------------------------------------------->
 postUrl = '${gzUrl("/post?")}';
 <#if q?exists>
 fetchSuggestionUrl = '${gzUrl("/data/json/suggestSearchTags.action?q="+q?js_string)}';
 </#if>
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
<div id="searchPageTop">
<#--
<div id="searchTermSuggest">
 <table id="searchTermSuggestContent">
 <tr>
 <td>Related terms
 <ul>
  <li>java</li><li>technology</li>
  </ul>
 </td>
 <td>Narrower terms
 <ul>
  <li>foo</li><li>bar</li>
  </ul></td>
 <td>Broader terms
 <ul>
  <li>poppo</li><li>jajdd</li>
  </ul></td>
 </tr>
 </table>
 -->
<#--
 <div id="suggestedSearchTerms">
 <ul>
 <li>related
   <ul><li>java</li><li>technology</li></ul>
 </li>
 <li>broader
   <ul><li>earth</li></ul>
 </li>
 <li>narrower
   <ul><li>earth</li></ul>
 </li>
 </ul>
 </div>
 
 <div id="searchTermSuggestControl">search suggestions: <a href="#" id="openSearchSuggestion" class="system-link">expand</a></div>
</div>
-->
</div>
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