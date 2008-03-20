<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to folder -->
<#include "./macro-lib.ftl"/>
<#assign user=loggedInUser/>
<#assign username=user.username/>
<#assign title="${loggedInUser.username}'s folders"/>
<#assign thisPageHref = gzUserFolderUrl(loggedInUser.username,"")/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-folder.css")]
            thisPageHref=thisPageHref
            toPageHref=thisPageHref/>
<@headerBlock/>
<@pageContent>  
<#assign bct = [gzBCTPair(username,gzUserUrl(username)),
                gzBCTPair("folders",gzUserFolderUrl(username,"")),
                gzBCTPair(gzFormatFolderName(folderName),gzUserFolderUrl(username,folderName))]/>
<@infoBlock bct=bct/>
  <div id="folders"> 
<@ww.form id="edit-folder-form" action="save" namespace="/settings/folders">
  <@ww.textfield id="old-folder-name" label="folder name" name="folderName" value="${folderName?if_exists}" readonly="true"/>
  <@ww.textfield id="new-folder-name" label="rename to" name="folder.name" value=""/>
  <@ww.textarea id="folder-dsp" label="description" name="folder.description" value="${folder.description?if_exists}"/>
  <@ww.submit cssClass="btn" value="save"/>
</@ww.form>
  <p>
  Total number of bookmarks: ${folder.size}
  <span  id="purge-cmd">
  <#if (folder.size > 0)>
  <a href="${gzUrl("/settings/folders/purge.action?folderName="+folderName)}">empty this folder</a> |
  </#if>
  <a href="${gzUrl("/settings/folders/delete.action?folderName="+folderName)}"/>delete this folder</a> 
  </span>
  </p>
  <#if action.actionMessages?has_content>
   <div class="errorMessage">
   <ul>
   <#list action.actionMessages as msg>
     <li>${getActionMsg(msg)}</li>
   </#list>
   </ul>
   </div>
  </#if>
  </div>
  
  <@ww.action name="miniList" namespace="/folder" executeResult=true>    
  </@ww.action>

</@pageContent>
<@pageEnd/>

<#function getActionMsg code>
  <#if code == "DUPLICATED_FOLDER_NAME">
    <#return "A folder of the same name already exists!"/>
  <#elseif code == "CHAR_NOT_ALLOWED">
    <#return "Folder name must not contain these characters: * ? & / \\ ; \' \" * % ^ + _"/>
  <#elseif code == "DEL_FOLDER_FAILED">
    <#return "Delete folder failed!"/>
  <#elseif code == "PURGE_OKAY">
    <#return "Successfully emptied this folder!"/>    
  </#if>
</#function>