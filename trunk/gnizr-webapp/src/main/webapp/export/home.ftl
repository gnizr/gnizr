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
<p>Export your saved bookmarks, so that they can be used in other applications. Select the format 
that you want to export.</p>
<p>After you click on the 'Export' button, the next page will display the progress of the export process. 
After the process is completed, you will be promted to save a file that contains the exported bookmarks.</p>
<p>If you have many bookmarks, this process may take a moment. </p>
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

</@mainBlock>
</@pageContent>
<@pageEnd/>
