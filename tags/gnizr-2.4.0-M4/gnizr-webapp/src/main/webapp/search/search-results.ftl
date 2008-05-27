<#-- /webapp/links/search-results.ftl -->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to LINKS -->
<#include "/links/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign thisPageBaseHref=gzUrl("/bookmark/search.action")/>  
<#if (queryString)?exists>
  <#assign title='search: ${queryString}'/>
  <#assign thisPageHref=thisPageBaseHref+'?queryString='+queryString?url/>
  <#if type?exists && type == 'text'>
    <#assign thisPageHref=thisPageHref+'&type=text'/>
  <#else>
    <#assign type='user'/>
    <#assign thisPageHref=thisPageHref+'&type=user'/>
  </#if>
  <#assign toPageHref=getToPageHref(thisPageHref)/>
<#else>
  <#assign title="search -- gnizr"/>
  <#assign thisPageHref=thisPageBaseHref/>
</#if>
<#assign toPageHref = getToPageHref(thisPageHref)/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-search.css")]
            thisPageBaseHref=thisPageBaseHref 
            thisPageHref=thisPageHref
            toPageHref=toPageHref>         
<#if loggedInUser?exists && type == 'user'>
  <script type="text/javascript" src="${gzUrl("/lib/javascript/bookmark-actions.js")}"></script>     
  <script type="text/javascript" 
    src="${gzUrl("/data/json/listFolders.action?callback=setUserFoldersData&username="+loggedInUser.username)}">   
  </script> 
  <script type="text/javascript">   
    enableAction('delete','${gzUrl("/bookmark/bulkDelete.action")}');
    enableAction('addFolder','${gzUrl("/bookmark/manage.action")}');  
    enableAction('removeFolder','${gzUrl("/bookmark/manage.action")}');   
  </script>        
</#if>              
</@pageBegin>            
<@headerBlock/>
<@pageContent>
<#if type == 'user' && (loggedInUser.username)?exists>
   <#assign bct = userhomeBCT(loggedInUser.username) + searchBCT()/>
<#else>
   <#assign bct = communityBCT() + searchBCT()/>      
</#if>               
<@infoBlock bct=bct/>
<@mainBlock>
<#if (totalMatched <= 0)>
<h2>No match!</h2>
<#elseif (totalMatched == 1)/>
<h2>Total matched: ${totalMatched} bookmark</h2>
<#else>
<h2>Total matched: ${totalMatched} bookmarks</h2>
</#if>
<#if (bookmarks?size > 0)>
  <@ww.form id="bookmarkActionForm" namespace="/bookmark" action="manage" theme="simple" method="post">  
  <@ww.hidden name="redirectToPage" value="${toPageHref}"/>  
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount
                 prevLabel="previous" nextLabel="next">
    <#if (type == 'user') && loggedInUser?exists>
      <@bookmarkActionControl/>
    </#if>                  
  </@pagerControl>               
  <@searchBookmarkResultList bookmarks=bookmarks/>
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount
                 prevLabel="previous" nextLabel="next"/>
  <@viewConfigBlock pageHref=thisPageHref/>
  </@ww.form> 
<#else>
<#--
<@searchForm namespace="/bookmark"/>
-->
<p><b>Search Suggestions:</b>
<ul>
<li>Make sure all words are spelled correctly</li>
<li>Try different phrases or tags</li>
</ul>
</p>    
</#if>

</@mainBlock>
<#if (bookmarks?size > 0)>
<@sidebarBlock cssClass="colored-sidebar">
  <#if type == 'text'>
    <@sidebarElement>
      <#assign cmapHref=gzUrl("/clustermap/search?queryString="+queryString+"&type=text")/>
      view all matched in: <a href="${cmapHref}" class="large-text">clustermap</a>
    </@sidebarElement>
  </#if>
<#if (commonTags?exists) && (commonTags?size > 0)>  
  <@sidebarElement>
    <@commonTagList commonTags=commonTags/>
  </@sidebarElement>
</#if>
</@sidebarBlock>
</#if>
</@pageContent>
<@pageEnd/>

<#-- 
MACRO: searchBookmarkResultList
-->
<#macro searchBookmarkResultList bookmarks>
<!-- SEARCH LINK RESULT BEGINS -->
<ul class="posts">
<#list bookmarks as bmark>
  <@postItem postId=bmark.id postUrl=bmark.link.url postTitle=bmark.title postNotes=bmark.notes
             postUser=bmark.user postTags=bmark.tagList 
             postCreatedOn=bmark.lastUpdated postLink=bmark.link 
             postInFolders = bmark.folderList
             emText=parseMatchedText(queryString?if_exists)/> 
</#list>
</ul>
<!-- SERACH LINK RESULT ENDS -->
</#macro>

<#function parseMatchedText queryString="">
  <#return queryString?split("\\s+",'r')/>
</#function>