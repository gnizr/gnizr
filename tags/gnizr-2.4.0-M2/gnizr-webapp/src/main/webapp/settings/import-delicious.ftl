<#-- /settings/import-delicious.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>
<#assign title="import del.icio.us -- ${loggedInUser.username} "/>
<#assign username=loggedInUser.username/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
<@headerBlock>
</@headerBlock>

<@pageContent>
  <#assign bct = settingsBCT(username) + [gzBCTPair('import from del.icio.us',gzUrl('/settings/delicious.action'))]/>
  <@infoBlock bct=bct/>
<@mainBlock>  
<@pageTitle>Import del.icio.us Bookmarks</@pageTitle>
<#if (importStatus?exists = false)>
<@pageDescription>
<p>To import bookmarks from <a href="http://del.icio.us" title="del.icio.us">del.icio.us</a>, enter your
account information in below.</p>
<p><span class="warning-message">Warning</span>: if a bookmark already exists in your gnizr account, it will be overwritten by the one from del.icio.us.</p>
</@pageDescription>
<div id="action-message">
  <@ww.actionmessage/>
</div>

<@formInput>
<@ww.form action="importPosts.action" method="post">
  <@ww.textfield cssClass="text-input-username" label="del.icio.us username" name="deliciousUsername"/>
  <@ww.password cssClass="text-input-password" label="del.icio.us password" name="deliciousPassword"/>
  <@ww.submit value="Begin Import" cssClass="btn"/>
</@ww.form>
</@formInput>

<#else>
<@pageDescription>
<p class="success-message">Finished! Total number of del.icio.us bookmarks: ${importStatus.totalNumber}</p>
<p>
<ul>
<li>imported: ${importStatus.numberAdded}</li>
<li>updated: ${importStatus.numberUpdated}</li>
<li>skipped: ${importStatus.numberError}</li>
</ul>
</p>
</@pageDescription>
</#if>
</@mainBlock>
</@pageContent>
<@pageEnd/>
</@ensureUserLoggedIn>