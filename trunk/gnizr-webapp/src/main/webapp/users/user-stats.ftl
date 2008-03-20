<#-- users/user-stats.ftl -->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to USERS -->
<#include "./macro-lib.ftl"/>
<#assign title="top users"/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-userstats.css")]/>
<@headerBlock>
</@headerBlock>
<@pageContent>  
 <#assign bct = communityBCT() + [gzBCTPair('top users',gzUrl('/topusers'))]/>
 <@infoBlock bct=bct/>
 <@mainBlock>
  <div id="userStats"> 
  <p>Explore bookmarks saved by other users.</p>
  <table id="userStatTable">
  <#list userStats as us>
  <#if (us.numOfBookmarks > 0)>
  <tr>
  <td class="username"><a href="${gzUserUrl(us.username)}" title="saved bookmarks of ${us.username}">${us.username}</a></td>
  <td class="bookmark_num">${us.numOfBookmarks} bookmarks
   <span class="altLink"> [ <a href="${gzUserUrl(us.username)+"/output/rss1.0"}" 
                             title="recently saved bookmarks of ${us.username} (RSS)">rss</a> |
   <a href="${gzUserUrl(us.username)+"/output/rdf"}" title="bookmarks and tags of ${us.username} (RDF)">rdf</a> ]
   </span>
  </td>
  <td class="tag_num">${us.numOfTags} tags</td></tr>
  <tr class="viewer_links"><td colspan="3">
    <a href="${gzUrl("/clustermap/user/"+us.username)}" title="clustermap of ${us.username}'s bookmarks">clustermap</a> | 
   <a href="${gzUrl("/gmap/user/"+us.username)}" title="geotagged bookmarks of ${us.username}">map</a> | 
   <a href="${gzUrl("/timeline/user/"+us.username)}" title="timeline of ${us.username}'s bookmarks">timeline</a> |
   <a href="${gzUserFolderUrl(us.username,"")}" title="folders of ${us.username}">folders</a> |
   <a href="${gzUserFeedUrl(us.username,"")}" title="feeds of ${us.username}">feeds</a> 
   </td></tr>
   </#if>
  </#list>
  </table> 
  </div>
</@mainBlock>
</@pageContent>
<@pageEnd/>