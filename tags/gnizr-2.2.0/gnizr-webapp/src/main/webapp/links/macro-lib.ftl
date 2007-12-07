<#-- links/macro-lib.ftl-->


<#-- 
MACRO: linkDescription
INPUT: bookmark // a Bookmark object 
-->
<#macro linkDescription bookmark>
<p class="link-description">
<span class="title"><a href="${bookmark.link.url}">${bookmark.title}</a></span><br/>
<#if (bookmark.link.url?length > 60)>
<span class="url">${bookmark.link.url[0..59]}...</span><br/>
<#else>
<span class="url">${bookmark.link.url}</span><br/>
</#if>
<br/>
<span class="notes">this url has been saved by ${bookmark.link.count} people.</span>
</p>
</#macro>

<#--
MACRO: userCommentList
INPUT: bookmarks:Sequence // a sequence of Bookmark objects
-->
<#macro userCommentList bookmarks>
<ol class="user-comments">
<#list bookmarks as bm>
<li>
<p class="user-comment">
<span class="date">${bm.createdOn?string("MMM d, ''yy")}</span><br/>
<span class="quote">
<#if bm.notes?has_content>&#8220;${bm.notes}&#8221;<#else><i>no comment.</i></#if>
</span><br/>
<span class="username">-<a href="${gzUserUrl(bm.user.username)}">${bm.user.username}</a></span>
</p>
</li>
</#list>
<#if (bookmarks?size == 0)>
<li><p class="user-comment">no user has yet made any comments about this link.</p></li>
</#if>
</ol>
</#macro>

<#--
MACRO: commonTagList
INPUT: commonTags:Sequence // a sequence of LinkTag objects
-->
<#macro commonTagList commonTags=[]>
<ul class="menu">
<li>
<h5 class="title">common tags:&nbsp;</h5>
<ul class="tagcloud">
<#list commonTags as linkTag>
  <#local tagLabel = linkTag.tag.label/>
  <#local tagUrl = gzTagUrl(tagLabel)/>
  <li><a href="${tagUrl}" title="pages tagged '${tagLabel}'">${tagLabel}</a></li>
</#list>
</ul>
</li>
</ul>
</#macro>


