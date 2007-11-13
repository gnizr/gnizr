<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign thisPageBaseHref=gzUrl("/search.action")/>  
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-search.css")]>                       
<script type="text/javascript" src="${gzUrl("/lib/javascript/opensearch.js")}"></script> 
<script type="text/javascript">
 services = <@openSearchSrvJson services=services/>;
 imagePathUrl = '${gzUrl("/images")}';
 queryTerm = '${q?if_exists?html}';
 proxyUrl = '${gzUrl("/data/json/searchproxy.action?searchUrl=")}';
</script>
</@pageBegin>            
<@headerBlock/>
<@pageContent>              
<@infoBlock bct=searchBCT()/>
<div id="searchPageContent">
<div id="left">
<table id="resultTiles">
<tr id="resultTilesRow">
<!--
<td id="t1">tile1</td>
<td id="t1sp" class="tileSeperator"><img id="img" src="${gzUrl("/images/drag-resize.jpg")}"></img></td>
<td id="t2">tile2</td>
<td id="t2sp" class="tileSeperator"><img id="img" src="${gzUrl("/images/drag-resize.jpg")}"></img></td>
<td id="t3">tile3</td>
<td id="t3sp" class="tileSeperator"><img id="img" src="${gzUrl("/images/drag-resize.jpg")}"></img></td>
<td id="t4">tile4</td>
-->
</tr>
</table>
</div>
<div id="right">
<div class="searchSideBlock">
<ul id="selectSearchServices">
<!--
<li><input type="checkbox" name="My Gnizr Bookmarks" CHECKED="true">My Gnizr Bookmarks</li>
<li><input type="checkbox" name="Gnizr Community Bookmarks" CHECKED="true">Gnizr Community Bookmarks</li>
<li><input type="checkbox" name="Google" CHECKED="true">Google</li>
-->
</ul>
</div>
</div>
</@pageContent>
<@pageEnd/>