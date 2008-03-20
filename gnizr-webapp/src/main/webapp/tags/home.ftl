<#-- /tags/home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#include "/lib/web/rdf-macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign title="bookmarks tagged '${tag}'"/>
<#assign thisPageHref=gzTagUrl(tag)/>
<#assign thisPageRdf = getTagUri(tag)/>
<#assign toPageHref = getToPageHref(thisPageHref)/>
<@pageBegin pageTitle=title >

</@pageBegin>
<@headerBlock isTagPage=true/>
<@pageContent>
 <#assign bct = communityBCT() + [gzBCTPair('popular tags',gzUrl('/tags')),
                                  gzBCTPair(tag,gzTagUrl(tag))]/>
 <@infoBlock bct=bct/>
<@mainBlock>
<#if (bookmarks?exists == false) || (bookmarks?size == 0)>
  <ul>
    <li>No URL has yet been tagged ${tag}</li>
  </ul>
<#else> 
<#--
  <@ww.form id="bookmarkActionForm" namespace="/bookmark" action="manage" theme="simple" method="post">
  <@ww.hidden name="redirectToPage" value="${toPageHref?if_exists}"/>
  -->
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount>  
  </@pagerControl>
  <@taggedBookmarkList bookmarks=bookmarks/>
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount/>
  <@viewConfigBlock pageHref=thisPageHref/>  
  <#--
  </@ww.form>
    -->
  <#if commonTags?exists>
    <@sidebarBlock cssClass="colored-sidebar">
      <@sidebarElement>
        <@popularTagList popularTags=commonTags hideTag=tag/>
      </@sidebarElement>
    </@sidebarBlock>
  </#if>
</#if>
</@mainBlock>
</@pageContent>
<@pageEnd rdfHref=thisPageRdf/>