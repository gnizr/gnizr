<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>

<#assign title="${loggedInUser.username}'s settings"/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-folders.css"),gzUrl("/css/gnizr-settings.css")]/>

<@headerBlock/>

<@pageContent>  
  <#assign bct = settingsBCT(username) +
                 [gzBCTPair('folders',gzUserFolderUrl(username,''))]/>
  <@infoBlock bct=bct/>
  <@pageTitle>Create Folder</@pageTitle>
  <@pageDescription>
  <p>Create folders to organize bookmarks.</p>
  <p>Folder names must not contain these characters: * ? & / \\ ; ' " * % ^ + _</p>
  </@pageDescription>
  <@formInput>    
    <@ww.form action="create" namespace="/settings/folders">
     <@ww.textfield label="Folder Name" name="folderName"/>
     <@ww.submit cssClass="btn" value="Create Folder"/>
    </@ww.form>
  </@formInput>

</@pageContent>
<@pageEnd/>