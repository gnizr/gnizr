<#-- /webapp/users/folder-home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>

<#assign title="${loggedInUser.username}'s settings -- gnizr"/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-folders.css"),gzUrl("/css/gnizr-settings.css")]/>

<@headerBlock/>

<@pageContent>  
  <#assign bct = settingsBCT(username) +
                 [gzBCTPair('folders',gzUserFolderUrl(username,''))]/>
  <@infoBlock bct=bct/>
  <div id="create-folder-form">    
    <@ww.form action="create" namespace="/settings/folders">
     <@ww.textfield label="new folder" name="folderName"/>
     <@ww.submit cssClass="btn" value="create"/>
    </@ww.form>
    <div class="errorMessage">
	 <ul>
	 <li>Folder name must not contain these characters: * ? & / \\ ; ' " * % ^ + _</li>
	 </ul>
    </div>
    </div> 
  </div>

</@pageContent>
<@pageEnd/>