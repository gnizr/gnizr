<#-- 
 users/home-timeline.ftl  
 A freemarker template that displays user bookmakrs in SIMILE Timeline XML output.
-->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to USERS -->
<#include "./macro-lib.ftl"/>
<?xml version="1.0" encoding="UTF-8"?>
<data>
<#list bookmark as bm>
  <event start="${getTimelineDateTime(bm.createdOn)}" 
         image="${gzUrl("/images/tiny-webpage.gif")}"
         title="${bm.title?html}">         
<#if bm.notes?exists>
  <#assign notes = sliceNotes(bm.notes)/>
<#else>
  <#assign notes = [""]/>
</#if>         
<#assign bcontent = 
   '<div class="bmark_content">' +
   '<a target="_blank" title="${bm.title}" href="${bm.link.url}">' + bm.link.url + '</a>' +
   '<p>' + notes[0]?html + '</p>' 
/>       
<#if (bm.tagList?size > 0)>
  <#assign bcontent=bcontent+ '<b>tags</b>:'/>
</#if>
<#list bm.tagList as tag>
  <#assign bcontent = bcontent+ '<a target="_blank" href="${gzUserBmarkArchivesUrl(bm.user.username,tag)}">${tag}</a> '/>
</#list>

<#assign bcontent=bcontent+'<br/>'/>

<#assign edtBmHref = gzUrl("/edtpost?url="+bm.link.url?url)/>
<#assign savBmHref = gzUrl("/post?url="+bm.link.url?url+"&title="+bm.title?url)/>
<#assign infBmHref = gzLinkUrl(bm.link.urlHash)/>

<#assign bcontent=bcontent+'<span class="commands">'/>
<#if loggedInUser?exists && (loggedInUser.username==bm.user.username)> 
  <#assign bcontent=bcontent+ '<a href="${edtBmHref}" title="edit bookmark">edit</a> |'/>
<#elseif loggedInUser?exists>
  <#assign bcontent=bcontent+ '<a href="${savBmHref}" title="save bookmark">save</a> |'/>
</#if>
<#assign bcontent=bcontent+ '<a href="${infBmHref}" title="read more">read more</a>'/>
<#assign bcontent=bcontent+'</span>'/>
<#assign bcontent=bcontent + '</div>'/>        
         ${fhtml(bcontent)}
         </event>  
</#list>
</data>

