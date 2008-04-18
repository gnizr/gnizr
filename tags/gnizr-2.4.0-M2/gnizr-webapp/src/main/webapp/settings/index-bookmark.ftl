<#-- /settings/import-delicious.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>

<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>
<#assign title="rebuild bookmark search index -- ${loggedInUser.username}"/>
<#assign username=loggedInUser.username/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
<@headerBlock>
</@headerBlock>

<@pageContent>
  <#assign bct = settingsBCT(username) + [gzBCTPair('rebuild search index',gzUrl('/settings/indexBookmark.action'))]/>
  <@infoBlock bct=bct/>
<@mainBlock>  
<h2>Rebuild Search Index</h2>
<#if (status?exists == false)>
<div class="instruction">
<p>To rebuild the search index database, click the button in below.</p>
<p>This operation 
will delete the existing search index database, and creates a new database
from the bookmarks that are currently saved in the system.  
</p>
<p>If there are many bookmarks in the system, this operation may take few minutes.</p>
<p><b>TIPS</b>: It's recommended that you run gnizr with the <i>serverMaintenanceModeEnabled</i> option
enabled in the <i>gnizr-config.xml</i> file before starting the re-index operation.</p>
</div>
<@ww.form action="updateIndex.action" namespace="/settings" method="post">
  <@ww.submit cssClass="btn" value="Rebuild Index Now"/>
</@ww.form>

<#else>
<div class="instruction">
<p>
<ul>
 <li>Search index created sucessfully!</li>
 <li>Total number of bookmark indexed: ${status.bookmarkIndexed?c}</li>
</ul>
</p>
</div>
</#if>
</@mainBlock>
</@pageContent>
<@pageEnd/>
</@ensureUserLoggedIn>