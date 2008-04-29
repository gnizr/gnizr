<#include "/lib/web/macro-lib.ftl"/>
<#include "/json/macro-lib.ftl"/>
<#assign username=user.username/>
<#assign title="${username}'s bookmarks on a map"/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-gmbmarks.css")]>
<#if (gnizrConfiguration.googleMapsKey)?exists>
<script type="text/javascript" src="${googleMapKeyUrl(gnizrConfiguration.googleMapsKey)}"></script>
<script type="text/javascript" src="${gzUrl('/lib/javascript/markermanager.js')}"></script>
</#if>     
<#if (page == 0)>
  <#assign page = 1/>
</#if>
<script type="text/javascript" src="${gzUrl('/lib/javascript/view-gmbmarks.js')}"></script>
<@ww.url id="pagingHref" namespace="/data/json" action="pageGBookmark" folder=folder username=username escapeAmp=false includeParams="none"/>
<@ww.url id="getGMarkerHref" namespace="/data/json" action="fetchGMarkers" includeParams="none"/>
<@ww.url id="userTaggedBmarkHref" namespace="/bookmark" action="viewUserPage" username=username includeParams="none"/>
<script type="text/javascript">
loadBookmarkData([<#list bookmarks as bm><@bookmarkJson bookmark=bm/><#if bm_has_next>,</#if></#list>],
                 ${page},${maxPageNumber},${totalNumOfBookmark});
setPagingServiceUrl('${pagingHref}');
setGetGeometryMarkerServiceUrl('${getGMarkerHref}');
setGetMarkerIconServiceUrl('${gzUrl("/images/getMarker")}');
setUserTaggedBookmarksUrl('${userTaggedBmarkHref}');
<#if loggedInUser?exists && user?exists && isUserAuth(loggedInUser,user) == true>
<@ww.url id="edtBookmarkHref" namespace="/bookmark" action="edit" includeParams="none"/>
setEditBookmarkServiceUrl('${edtBookmarkHref}');
</#if>
</script>
<!-- ${pagingHref}-->
</@pageBegin>

<@headerBlock>
</@headerBlock>

<@pageContent>
<#assign bct = userhomeBCT(username)/>
<#if folder?exists>
  <#assign bct = bct + [gzBCTPair("folders",gzUserFolderUrl(username,"")),
                        gzBCTPair(gzFormatFolderName(folder),gzUserFolderUrl(username,folder))]/>
<#else>
  <#assign bct = bct + [gzBCTPair('bookmark archive',gzUserBmarkArchivesUrl(username))]/>                        
</#if>
<#assign bct = bct + [gzBCTPair('map','')]/>
<@infoBlock bct=bct/>
<div id="loading" class="invisible"><img src="${gzUrl("/images/loading-lng-bar.gif")}"></img></div>
<div id="container">  
  <div id="left" class="column">
  <#--
    <div id="selectDataSource">
      <ww:form namespace="/gmap" action="view" theme="simple">
        <#assign opt = r"#{'f1':'f1','f2':'f2','f3':'f3'}"/>
        <@ww.select label="show"  name="folder" list=opt/>
      <ww:submit value="Go"/>
      </ww:form>    
    </div>
    -->
    <div id="selectBookmark">
    <div id="selectBookmarkOpt" class="invisible">
    select: 
    <a id="showAllPlacemarks" href="#">all</a> | 
    <a id="hideAllPlacemarks" href="#">none</a>
    </div>
    <div class="scrollableList">
     <table id="bookmarkListTable">
      <tbody id="bookmarkList"></tbody>
     </table>
    </div>
    <div id="leftFooter"> 
    </div>        
    </div>
  </div>
  <div id="center" class="column">
    <div id="map"></div>
  </div>
  <div id="right" class="column">
  <div id="bookmarkDescription">
  </div>
  </div>
</div>
 
</@pageContent>
<@pageEnd/>