<#-- /webapp/bookmarks/edit.ftl -->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<@ensureUserLoggedIn>
<#assign username=loggedInUser.username/>
<#if url?exists >
  <#assign title="save url: " + url + " -- gnizr">
<#else>
  <#assign title="save url -- gnizr"/>
</#if>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-tagcloud.css"),
                                     gzUrl("/css/gnizr-post.css")]>      
<script type="text/javascript" src="${gzUrl("/lib/javascript/selection.js")}"></script>                                                                                                      
<script type="text/javascript" src="${gzUrl("/lib/javascript/edit-bookmark.js")}"></script>
<script type="text/javascript" src="${gzUrl("/data/json/userTagCloud.action?callback=loadUserTags&username="+username)}"></script>
<script type="text/javascript" src="${gzUrl("/data/json/listUserTagInGroups.action?callback=loadUserTagGroups&username="+username)}"></script>
<script type="text/javascript" src="${gzUrl("/data/json/listUsers.action?callback=loadUserList")}"></script>
<script type="text/javascript">
 getRecommendedTagsUrl = '${gzUrl("/data/json/recommendTags.action")}';
 getUserTagsUrl = '${gzUrl("/data/json/userTagCloud.action")}';
 getUserTagGroupUrl = '${gzUrl("/data/json/listUserTagInGroups.action")}';
</script>
<#if (gnizrConfiguration.googleMapsKey)?exists>
<script type="text/javascript"src="${googleMapKeyUrl(gnizrConfiguration.googleMapsKey)}"></script>
<script type="text/javascript" src="${gzUrl('/lib/javascript/markermanager.js')}"></script>
</#if>
<#if pointMarkers?exists && (pointMarkers?size>0)>
<script type="text/javascript"> 
 loadPointMarkers(<@pointMarkerListJson pointMarkers=pointMarkers/>);	  
</script>
</#if>
</@pageBegin>
<@headerBlock>
</@headerBlock>
<@pageContent>
<#assign bct = settingsBCT(username) + 
               [gzBCTPair('add bookmark','/post')]/>
<@infoBlock bct=bct/>               
<#assign bkmrkUrl = url?if_exists/>
<#if (editBookmark.link.url)?exists>
  <#assign bkmrkUrl = editBookmark.link.url/>
<#else>
  <#assign bkmrkUrl = 'http://'/>  
</#if>
<@ww.fielderror/>
<@ww.form onsubmit="saveAndClose('${saveAndClose?string}')" id="saveBookmark" action="/bookmark/save.action" method="post" cssClass="editInputGroup" theme="simple"
>
<div id="legend">* = Required Field</div>
  <div class="inputDataRow">
  <span class="inputLabel">URL*:</span> 
  <@ww.textfield id="bookmarkUrl" cssClass="text-input-url" required="true" label="url"  labelposition="top"
                 size="80" name="url" value="${bkmrkUrl?if_exists}"/>   
                                             
  <div id="openUrlWindowLink"><a href="#" id="openUrlWindow" class="system-link">Open URL in a new window</a></div>
  </div>
  <div class="inputDataRow">
  <span class="inputLabel">Description*:</span>                 
  <@ww.textfield cssClass="text-input-dsp" required="true" label="description"  labelposition="top"
                 size="80" name="title" value="${(editBookmark.title)?if_exists}"/> 
  </div>
  <div class="inputDataRow">
  <span class="inputLabel">Notes:</span>                 
  <#--               
  <@ww.textarea  cssClass="text-input-notes" label="notes" cols="50" rows="10" 
                 name="notes"  value="${(editBookmark.notes)?if_exists}"/>
  -->
  <div class="text-input-notes">
  <@ww.richtexteditor  labelposition="top"
		toolbarCanCollapse="false"
		cssClass="text-input-notes"
		label="notes" 
		name="notes" 
		height="300"
		fontFormats="h3;h4;p"	
		fontColors="FF0000,FF8040,00FF00,FFFF00,00FFFF,800080,FF00FF,2B60DE"
		toolbarSet="MyToolbar"
		customConfigurationsPath="${gzUrl('/lib/javascript/fckeditor-config.js')}"
		value="${(editBookmark.notes?js_string)?if_exists}"
		/>                 
  </div>
  </div>
  <div class="inputDataRow">		
  <span class="inputLabel">Tags (use white-space to separate multiple tags):</span>
  <span class="machineTagHelpers">Add machine tag: 
  <a href="#" id="geonamesMTHelper" class="system-link" title="gn:geonames"><img class="icon" src="${gzUrl('/images/globe-16.png')}"></a>
  <a href="#" id="forUserMTHelper" class="system-link" title="gn:for"><img class="icon" src="${gzUrl('/images/user-16.png')}"></a>
  <a href="#" id="folderMTHelper" class="system-link" title="gn:folder"><img class="icon" src="${gzUrl('/images/folder-16.gif')}"></a>
  <a href="#" id="subscribeMTHelper" class="system-link" title="gn:subscribe"><img class="icon" src="${gzUrl('/images/rss-16.png')}"></a> 
  </span>		
  <@ww.textarea cssClass="text-input-tags" label="tags (use white-space to separte multiple tags)"  labelposition="top"
                 name="tags"  value="${(editBookmark.tags)?if_exists}"/>                   
  <div id="suggestedTags"></div>                 
  </div>                 
  <@ww.hidden name="redirect" value="${redirect?if_exists?string}"/> 
  <#if redirectToPage?exists>    
    <@ww.hidden name="redirectToPage" value="${redirectToPage}"/>            
  <#else>
    <@ww.hidden name="redirectToPage" value="/home"/>     
  </#if>    
  <@ww.hidden name="oldUrl" value="${(editBookmark.link.url)?if_exists}"/>
<#-- Include all existing PointMarkers in this FORM's input. -->  
<#if pointMarkers?exists && (pointMarkers?size>0)>   
<#list pointMarkers as p> 
  <input type="hidden" name="pointMarkers" class="geomMarker" value="<@pointMarkerJson pointMarker=p/>"></input>
</#list>
</#if>
<div id="submits">
<div class="left">
<#if (editBookmark.link.url)?exists>
 <@ww.url id="delBmarkHref" namespace="/bookmark" action="delete" url=editBookmark.link.url includeParams="none"/>
 <a href="${delBmarkHref}" class="system-link">Delete this bookmark</a>
</#if> 
</div>
<div class="right">
<#if (editBookmark.lastUpdated)?exists>
<span id="bookmarkLastUpdated">Last updated: ${editBookmark.lastUpdated?string.short_long}</span>
</#if> 
  <@ww.submit id="submitSaveBookmark" cssClass="btn saveSubmit" value="Save" theme="simple"/>
  <@ww.submit id="submitSaveBookmarkAndEdit" name="saveAndContinueEdit" cssClass="btn saveSubmit" value="Save and Continue Editing" theme="simple"/>
</div>
</div>
</@ww.form>
<div id="editTools" class="editInputGroup">
Tools: <a id="addTags" href="#">edit tags</a> | <a id="addPlacemarks" href="#">edit placemarks</a>
<!-- user tag cloud -->
<div id="selectUserTag">
<table id="bookmark-edit-menu-bar" class="menubar">
<tr>
<td><a id="loadYourTags" class="system-link" href="">your tags</a></td>
<td>tags of: <@ww.select name="selectUser" id="userSelection" list=r"#{'':'--- user ---'}" label="tags of" theme="simple"/></td>
<td>show only: <@ww.select name="selectTagGroup" id="tagGroupSelection"  list=r"#{'':'--- tag group ---'}" label="show only" theme="simple"/> </td>
<td><a id="loadRecommendedTags" class="system-link" href="">recommend tags</a> </td>
<td>select tags: <a id="selectTags5" class="system-link" href="">top 5</a>, <a id="selectTags10" class="system-link" href="">top 10</a>, <a id="selectTagsNone" class="system-link" href="">none</a></td>
</tr>
</table>
<div id="tagCloud">
  <ol id="userTags" class="tag-cloud invisible"></ol>
</div>
</div>

<div id="editPlacemarks" class="invisible">
<table id="edit-placemarks-menu-bar" class="menubar">
<tr>
<td><a id="createPlacemark" class="system-link" href="#">add placemark</a></td>
<td>Go to location: <@ww.textfield id="zoomToInput" theme="simple" size="15"></@ww.textfield>
<input id="zoomTo" type="button" value="Go"></input></td>
</tr>
</table>
<div id="map"></div>
</div>
</div>
</@pageContent>
<@pageEnd/>

</@ensureUserLoggedIn>