<#-- /webapp/users/home.ftl -->
<#include "/lib/web/util-func-lib.ftl"/>
<#include "/lib/web/tagcloud-macro-lib.ftl"/>
<#-- 
MACRO: taggedLinksList 
INPUT: links:Sequence // a sequence of Link objects
-->
<#macro forUserList forUsers=[]>
<!-- FOR USER LINK LIST BEGINS -->
<ul class="posts">
<#list forUsers as aForUser> 
  <#local id = aForUser.bookmark.id/>
  <#local url = aForUser.bookmark.link.url/>
  <#local title = aForUser.bookmark.title/>
  <#local notes = aForUser.bookmark.notes/>
  <#local frmUser = aForUser.bookmark.user/>
  <#local tags = aForUser.bookmark.tagList/>
  <#local createdOn = aForUser.bookmark.createdOn/>
  <#local link = aForUser.bookmark.link/>
  <@postItem postId=id postUrl=url postTitle=title postNotes=notes
             postUser=frmUser postTags=tags
             postCreatedOn=createdOn postLink=link>
    <#local delbmHref = gzUrl("/for/me/delete.action?deleteForUserId="+aForUser.id?c+"&redirectToPage="+toPageHref?if_exists)/>                 
     <a href="${delbmHref}" class="system-link" title="delete">delete</a> |
  </@postItem> 
</#list>
</ul>
<!-- FOR USER LINK LIST ENDS -->
</#macro>

<#-- 
===================================================================
MACRO: bookmarkList
INPUT: bookmarks:Sequence // a sequence of Bookmark objects
===================================================================
-->
<#macro bookmarkList bookmarks=[] >
<!-- BOOKMARK LIST BEGINS -->
<ul class="posts">
<#list bookmarks as bm>
  <#local postId = bm.id/>
  <#local postUrl = bm.link.url/>
  <#local postTitle = bm.title/>
  <#local postNotes = bm.notes/>
  <#local postUser = bm.user/>
  <#local postTags = bm.tagList/>
  <#local postCreatedOn = bm.createdOn/>
  <#local postLink = bm.link/>
  <#local postInFolders = bm.folderList/>
  <@postItem postId=postId postUrl=postUrl postTitle=postTitle
                   postNotes=postNotes postUser=postUser postTags=postTags
                   postCreatedOn=postCreatedOn postLink=postLink 
                   postInFolders=postInFolders/>
</#list>
</ul>
<!-- BOOKMARK LIST ENDS -->
</#macro>


