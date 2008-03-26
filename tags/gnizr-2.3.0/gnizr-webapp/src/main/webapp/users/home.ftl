<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign username=user.username/>
<#assign title="${username}'s bookmarks"/>
<#assign thisPageBaseHref = gzUserBmarkArchivesUrl(username)/>
<#assign thisPageRSS = gzUserUrl(username)+"/output/rss1.0"/>
<#assign thisPageRDF = gzUserUrl(username)+"/output/rdf"/>
<#if tag?exists>
  <#assign title=title+" tagged '${tag}'"/>
  <#assign thisPageHref = gzUserBmarkArchivesUrl(username,tag)/>    
<#else>
  <#assign thisPageHref = gzUserBmarkArchivesUrl(username)/>    
</#if>
<#assign toPageHref = getToPageHref(thisPageHref)/>
<#assign title=title+" -- gnizr"/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-userpage.css")]
            thisPageBaseHref=thisPageBaseHref 
            thisPageHref=thisPageHref
            toPageHref=toPageHref>
  <link rel="alternate" type="application/rss+xml" title="RSS 1.0" 
        href="${gzFullUrl("/user/"+username+"/output/rss1.0")}"/>  
<#if loggedInUser?exists>
  <#if isUserAuth(loggedInUser,user)>  
  <script type="text/javascript" src="${gzUrl("/lib/javascript/bookmark-actions.js")}"></script>     
  <script type="text/javascript" 
    src="${gzUrl("/data/json/listFolders.action?callback=setUserFoldersData&username="+user.username)}">   
  </script> 
  <script type="text/javascript">
    enableAction('delete','${gzUrl("/bookmark/bulkDelete.action")}');
    enableAction('addFolder','${gzUrl("/bookmark/manage.action")}');  
    enableAction('removeFolder','${gzUrl("/bookmark/manage.action")}');   
  </script>        
  </#if>
</#if>  
      
</@pageBegin>

<@headerBlock>
</@headerBlock>

<@pageContent>
<#assign bct = userhomeBCT(user.username) +
               [gzBCTPair('bookmark archive',gzUserBmarkArchivesUrl(username))]/>
<#if tag?exists>
  <#assign bct = bct + [gzBCTPair(tag,gzUserBmarkArchivesUrl(username,tag))]/>
</#if>    
<@infoBlock bct=bct/>

<@checkBookmarkExists bookmarks=bookmark>

<@ww.if test="hasActionErrors()">
  <ul>
  <@ww.iterator value="actionErrors">
    <li><@ww.property/></li>
  </@ww.iterator>
  </ul>
</@ww.if> 
<@ww.else> 
  <@ww.form id="bookmarkActionForm" namespace="/bookmark" action="manage" theme="simple" method="post">
  <@ww.hidden name="redirectToPage" value="${toPageHref}"/>
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount>
    <@pageActionControl>
      <#assign cmapHref=gzUrl("/clustermap/user/"+username)/>
      <#assign gmapHref=gzUrl("/gmap/user/"+username)/>
      <#assign tmlnHref=gzUrl("/timeline/user/"+username)/>
      explore: 
      <a href="${cmapHref}" title="clustermap bookmarks">clustermap</a> |
      <a href="${gmapHref}" title="bookmarks with placemarks">map</a> |
      <a href="${tmlnHref}" title="bookmark timeline">timeline</a>
    </@pageActionControl>
    <#if loggedInUser?exists>
      <#if isUserAuth(loggedInUser,user)>   
        <@bookmarkActionControl/>
      </#if>
    </#if>
  </@pagerControl>
  <@bookmarkList bookmarks=bookmark/>
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount/>
  <@viewConfigBlock pageHref=thisPageHref/>  
  </@ww.form>
</@ww.else>

<@sidebarBlock cssClass="colored-sidebar">
<#if tag?exists>
  <@ww.action name="suggestTags" namespace="/bookmark" executeResult=true>    
  </@ww.action>
</#if>

<#if hideTagGroups?exists && (hideTagGroups == false)>
  <@ww.action name="userTagGroups" namespace="/bookmark" executeResult=true>
  </@ww.action>
<#else>
  <@ww.action name="userTagCloud" namespace="/bookmark" executeResult=true>
  </@ww.action>
</#if>
 
</@sidebarBlock>
</@checkBookmarkExists>
</@pageContent>

<@pageEnd rssHref=thisPageRSS rdfHref=thisPageRDF/>
