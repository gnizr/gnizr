<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to folder -->
<#include "./macro-lib.ftl"/>
<#assign title="${user.username}'s folders"/>
<#assign thisPageHref = gzUserFolderUrl(user.username,"")/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-folder.css")]
            thisPageHref=thisPageHref
            toPageHref=thisPageHref
            />
<@headerBlock/>

<@pageContent>  
<#assign bct = [gzBCTPair(username,gzUserUrl(username)),
                gzBCTPair("folders",gzUserFolderUrl(username,""))]/>
<@infoBlock bct=bct/>
  <div id="folders">  
<#if (folders?size > 0)>  
  <table id="folderTable">
  <tr><th>folder</th><th>size</th></tr>
  <#assign folders = setSystemFoldersFirst(folders)/>
  <#list folders as fd> 
  <#assign fname = gzFormatFolderName(fd.name)/> 
  <tr class="folderRow">
  <td class="foldername"><a href="${gzUserFolderUrl(user.username,fd.name)}" title="folder: ${fname}">${fname}</a></td><td class="foldersize">${fd.size}</td>  
  <#if loggedInUser?exists && isUserAuth(loggedInUser,user) == true && 
       (fd.name != "_my_") && (fd.name != "_import_")>
  <td class="folder-edit">
  <a href="${gzUrl("/settings/folders/edit.action?folderName="+fd.name)}">edit</a> |
  <a href="${gzUrl("/settings/folders/delete.action?folderName="+fd.name)}"/>delete</a> 
  </td>
  </tr>    
  </#if>
  <tr class="folderRow2" >
  <#assign spanColNum = 2/>
  <#if loggedInUser?exists && isUserAuth(loggedInUser,user) == true>
     <#assign spanColNum = 3/>
  </#if>
  <td class="folder-description" colspan="${spanColNum}"> 
  ${fd.description?if_exists}
  </td>
  </tr>
  </#list>
  </table>
<#else>
  No folders here!
</#if>  
<#if loggedInUser?exists && isUserAuth(loggedInUser,user)>
 <div id="create-folder-form">    
    <@ww.form action="create" namespace="/settings/folders">
     <@ww.textfield label="new folder" name="folderName"/>
     <@ww.submit cssClass="btn" value="create"/>
    </@ww.form>
    </div> 
  </div>
</#if>  
</@pageContent>
<@pageEnd/>