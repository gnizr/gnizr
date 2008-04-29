<#include "/lib/web/macro-lib.ftl"/>
<#if bookmark?exists>
  <#assign title='\''+bookmark.title+'\' saved by ' + bookmark.user.username/>
  <#assign thisPageHref = gzBookmarkUrl(bookmark.id?c)/>
</#if>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-linkhistory.css")]>
</@pageBegin>
<@pageContentLite>
<#if bookmark?exists>
<div id="bookmark-detail">
<h2 class="title"><a target="_blank" href="${bookmark.link.url}" title="${bookmark.title}">${bookmark.title}</a></h2>
<p class="link">${bookmark.link.url}</p>
<p class="meta">Saved by <a href="${gzUserUrl(bookmark.user.username)}">${bookmark.user.username}</a> 
<#if (bookmark.tagList?size > 0)> tagged
  <#list bookmark.tagList as tag>    
    <a class="tag" href="${gzUserTagUrl(bookmark.user.username,tag)}">${tag}</a><#if tag_has_next>, </#if>
  </#list>
</#if>
</p> 
<#if bookmark.notes?exists && (bookmark.notes?length > 0)>
<div class="notes">
${bookmark.notes}
</div>
</#if>
<p class="meta">Created On: ${bookmark.createdOn} :: Last Updated: ${bookmark.lastUpdated} :: 
<#if loggedInUser?exists>
  <#if (loggedInUser.id == bookmark.user.id)>
  <a href="${gzUrl('/edtpost?url='+bookmark.link.url?url+'&redirectToPage='+thisPageHref)}" class="system-link">edit</a>
  <#else>
  <a href="${gzUrl('/post?url='+bookmark.link.url?url+'&redirectToPage='+thisPageHref)}" class="system-link">save</a>
  </#if>
</#if>
</p>
</div>

<div>
<#if othersSaved?exists && (totalOthersSaved > 1)>
<p class="meta"><b>Also saved by</b>:
  <#list othersSaved as obm>    
    <#if obm.user.id != bookmark.user.id>
      <#assign obmHref = gzBookmarkUrl(obm.id?c)/>
      <a href="${obmHref}">${obm.user.username}</a>
    </#if>
  </#list>
</p>
</#if>
</div>
<#else>
<h3>No such bookmark.</h3>
</#if>
</@pageContentLite>




   