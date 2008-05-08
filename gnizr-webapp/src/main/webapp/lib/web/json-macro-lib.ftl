<#macro bookmarkJson bookmark>
<#local notes = ""/>
<#if bookmark.notes?exists>
  <#local notes = bookmark.notes/>
</#if>  
   {'id':'${bookmark.id?c}',
   'title':'${bookmark.title?js_string?html}',
   'url':'${bookmark.link.url?html}',
   'notes':'${notes?js_string?html}',   
   'summary':'${(makeShortNotes(notes))?js_string?html}',
   'tags':${list2array(bookmark.tagList)},
   'lastUpdated':'${(bookmark.lastUpdated)?datetime}',
   'createdOn':'${(bookmark.createdOn)?datetime}',
   'username':'${bookmark.user.username?html}'}
</#macro>

<#function list2array aList>
<#local anArray = '['/>
 <#list aList as t>
   <#local anArray = anArray + '"'+t?js_string?html + '"'/>
   <#if t_has_next>
     <#local anArray = anArray +","/>
   </#if>
 </#list>
<#local anArray = anArray + ']'/>
 <#return anArray/>
</#function>

<#macro pointMarkerListJson pointMarkers>
[
<#list pointMarkers as pm>
  <@pointMarkerJson pointMarker=pm/>
  <#if pm_has_next>,</#if>
</#list>
]
</#macro>

<#macro pointMarkerJson pointMarker>
{
 'id':${pointMarker.id?c},
 'notes':'${pointMarker.notes?html}',
 'iconId':${pointMarker.markerIconId?c},
 'point':'${pointMarker.x?c},${pointMarker.y?c}'
}      
</#macro>

<#macro openSearchSrvJson services>
[<#list services as srv>
  {'id':'OS${srv_index+1}',
   'shortName':'${srv.shortName?html}',
   'description':'${srv.description?html}',
   'type':'${srv.type}',
   'supportsPageBased':${srv.supportsPageBased?string},
   'supportsIndexBased':${srv.supportsIndexBased?string},
   'serviceUrl':'${srv.serviceUrlPattern?url}',
   'defaultEnabled':${srv.defaultEnabled?string}}   
  <#if srv_has_next>,</#if>
</#list>]
</#macro>