<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to USERS -->
<#include "/users/macro-lib.ftl"/>
<#assign username = user.username/>
<#assign title="${username}'s folder '${gzFormatFolderName(folderName)}'"/>
<#assign thisPageBaseHref = gzUserFolderUrl(username,folderName)/>
<#assign thisPageRSS = gzUserUrl(username)+"/output/rss1.0"/>
<#assign thisPageRDF = gzUserUrl(username)+"/output/rdf"/>
<#if tag?exists>
  <#assign thisPageHref = thisPageBaseHref + "/tag/"+tag?url/>
  <#assign title = title + ", bookmarks tagged '"+tag+"'"/>
<#else>
  <#assign thisPageHref = thisPageBaseHref/>  
</#if>
<#assign toPageHref = getToPageHref(thisPageHref)/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-userpage.css")]
		    thisPageBaseHref=thisPageBaseHref 
            thisPageHref=thisPageHref
            toPageHref=toPageHref>
<#--
  <script type="text/javascript" src="${gzUrl("/lib/javascript/overlay-dialog.js")}"></script>           
  <script type="text/javascript" src="${gzUrl("/lib/javascript/bookmark-quick-view.js")}"></script>    
  -->
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

<@headerBlock/>

<@pageContent>
<#assign bct = userhomeBCT(username) + 
               [gzBCTPair("folders",gzUserFolderUrl(username,"")),
                gzBCTPair(gzFormatFolderName(folderName),gzUserFolderUrl(username,folderName))]/>
<#if tag?exists>
  <#assign bct = bct + [gzBCTPair(tag,gzUserFolderUrl(username,folderName,tag))]/>
</#if>    
<@infoBlock bct=bct/>
<@checkBookmarkExists bookmarks=bookmarks inFolder=folder>
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
    <#assign cmapHref=gzUrl("/clustermap/user/"+username+"/folder/"+folderName)/>
    <#assign gmapHref=gzUrl("/gmap/user/"+username+"/folder/"+folderName)/>
    explore: <a href="${cmapHref}">clustermap</a> |
     <a href="${gmapHref}" title="bookmarks with placemarks">map</a> 
    </@pageActionControl>
    <#if loggedInUser?exists>
      <#if isUserAuth(loggedInUser,user)>   
        <@bookmarkActionControl/>
      </#if>
    </#if>
  </@pagerControl>
  <@bookmarkList bookmarks=bookmarks/>
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
  <@ww.action name="folderTagGroups" namespace="/folder" executeResult=true>
  </@ww.action>
<#else>
  <@ww.action name="folderTagCloud" namespace="/folder" executeResult=true>
  </@ww.action>  
</#if>
</@sidebarBlock>
</@checkBookmarkExists>
</@pageContent>
<@pageEnd rssHref=thisPageRSS rdfHref=thisPageRDF/>
