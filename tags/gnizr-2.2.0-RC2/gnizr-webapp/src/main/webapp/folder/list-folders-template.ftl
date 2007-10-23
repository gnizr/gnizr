<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>

<@sidebarBlock cssClass="colored-sidebar">
<#if folders?exists && folders?has_content>
Select a folder to edit
 <ul>
 <#list folders as fd>  
  <#assign edtUrl = gzUrl("/settings/folders/edit.action?folderName="+fd.name)/>
  <li><a href="${edtUrl}">${gzFormatFolderName(fd.name)} (${fd.size})</a></li>
 </#list>
 </ul>
</#if>
<a href="${gzUserFolderUrl(loggedInUser.username,"")}" id="create-folder">Create new folder</a>
</@sidebarBlock> 
