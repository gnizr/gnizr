<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>

<#assign title="export bookmarks "/>
<#assign username=loggedInUser.username/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
<@headerBlock>
</@headerBlock>

<@pageContent>
  <#assign bct = settingsBCT(username) + [gzBCTPair('export bookmarks',gzUrl('/settings/export.action'))]/>
  <@infoBlock bct=bct/>
<@mainBlock>  
<@pageTitle>Export Bookmarks</@pageTitle>

<@pageDescription>
<p>Export your saved bookmarks, so that they can be used in other applications.</p> 
<p>Only bookmarks saved folders will be exported. If a bookmark appear in multiple folders, 
separate copies of the same bookmark will be created in the respective folders.</p>
<p>After you click on the 'Export' button, the export process will begin. 
After the process is completed, you will be asked to save a file that contains the exported bookmarks.</p>
<p>Select the format that you want to export.</p>
</@pageDescription>
<div id="action-message">
  <@ww.actionmessage/>
</div>

<#assign formatOpts = {"netscape":"Mozilla Firefox"}/>
<@formInput>
<@ww.form action="bookmarkDispatch" method="post">
  <@ww.select label="Select format" name="format" list=r'#{"netscape":"Mozilla Firefox"}'/>
  <@ww.submit value="Export" cssClass="btn"/>
</@ww.form>
</@formInput>
<@pageDescription>
<div class="system-message">
<p><b>Note:</b> If you have many bookmarks, this process may take a moment. Do not click stop or reload this page.</p>
</div>
</@pageDescription>

</@mainBlock>
</@pageContent>
<@pageEnd/>
