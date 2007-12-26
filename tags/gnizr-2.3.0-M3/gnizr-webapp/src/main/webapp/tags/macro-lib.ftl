<#-- tags/macro-lib.ftl -->

<#-- 
MACRO: tagCloudList 
INPUT: tagCloudMap:Hash // a Hash of {Tag:tagClsId}
                        // tagClsId is the tag cloud bucket (1-6)
                        // that a Tag belongs to.
       sortedTags:Sequence // a sequence of sorted Tags                 
-->
<#macro tagCloudList tagCloudMap={} sortedTags=[]>
<div class="tag-cloud">
<#if (tagCloudMap?size > 0)>
<p>
This is a tag cloud - a list of tags where size reflects popularity.
</p>
<#assign sortAlphaHref=gzUrl("/community/tagcloud.action?sort=alpha")/>
<#assign sortFreqHref=gzUrl("/community/tagcloud.action?sort=freq")/>
<p class="sortOpts">
sort: <a href="${sortAlphaHref}" title="sort tag cloud alphabetically">alphabetically</a> | <a href="${sortFreqHref}" title="sort tag cloud by usage frequency">by usage frequency</a>
</p>
<ol>
<#-- sortedTags: a list of sorted Tags object in the value stack-->
<@ww.iterator value="sortedTags">
  <#-- get the next Tag object in this iterator (i.e., referenced by 'top') -->
  <@ww.set name="tag" value="top"/>
  <@ww.set name="tagClsId" value="tagCloudMap[top]"/>  
  <li class="${gzTagClass(tagClsId)}"><span>${tag.count} bookmarks are tagged with </span><a href="${gzTagUrl(tag.label)}" class='tag' title="${tag.label}">${tag.label}</a></li>     
</@ww.iterator>
</ol>
<#else>
no available tag data. check back later.
</#if>
</div>
</#macro>

<#-- 
MACRO: taggedBookmarkList 
INPUT: links:Sequence // a sequence of Bookmark objects
-->
<#macro taggedBookmarkList bookmarks=[]>
<!-- TAGGED LINK LIST BEGINS -->
<ul class="posts">
<#list bookmarks as bmark>
  <@postItem postId=bmark.id
             postUrl=bmark.link.url postTitle=bmark.title postNotes=bmark.notes
             postUser=bmark.user postTags=bmark.tagList 
             postCreatedOn=bmark.createdOn postLink=bmark.link/> 
</#list>
</ul>
<!-- TAGGED LINK LIST ENDS -->
</#macro>

<#--
MACRO: popularTagList
INPUT: popularTags:Sequence // a sequence of LinkTag objects       
       hideTag: a Tag object // hide from the tag cloud 
-->
<#macro popularTagList hideTag="" popularTags=[] >
<ul class="menu">
<li>
<h5 class="title">common tags:&nbsp;</h5>
<ul class="tagcloud">
<#list commonTags as linkTag>  
  <#local tagLabel = linkTag.tag.label/>
  <#local tagUrl = gzTagUrl(tagLabel)/>
  <#if (hideTag != tagLabel)>
  <li><a href="${tagUrl}" title="pages tagged '${tagLabel}'">${tagLabel}</a></li>
  </#if>
</#list>
</ul>
</li>
</ul>
</#macro>


<#-- 
FUNCTION: gzTagClass
INPUT: tagClsId:Integer // the tag cloud bucket id of a tag
RETURN: a CSS class that represents the input tagClsId
-->
<#function gzTagClass tagClsId>
  <#if (tagClsId = 1)>
      <#return 'not-popular'/>
    <#elseif (tagClsId = 2)>
      <#return 'not-very-popular'/>
    <#elseif (tagClsId = 3)>
      <#return 'somewhat-popular'/>
    <#elseif (tagClsId = 4)>
      <#return 'popular'/>
    <#elseif (tagClsId = 5)>
      <#return 'very-popular'/>
    <#elseif (tagClsId = 6)>
      <#return 'ultra-popular'/>
    </#if>   
    <#return ''/>
</#function>
