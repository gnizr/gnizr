<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>
<#-- if not logged in, redirect to the login page -->
<@ensureUserLoggedIn>
<#assign title="${loggedInUser.username}'s settings"/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/>
 <@headerBlock/>
<@pageContent>
<#assign bct = settingsBCT(username)/> 
<@infoBlock bct=bct/>
<@mainBlock>
<@settingsBlock title="Account Settings">
<ul>
<li><a href="${gzUrl('/settings/changePassword.action')}">Change password</a></li>
<li><a href="${gzUrl('/settings/changeProfile.action')}">Edit profile</a></li> 
</ul>
</@settingsBlock>
<@settingsBlock title="Manage Bookmarks & Folders">
<ul>
<li><a href="${gzUrl('/post')}">Add new bookmark</a></li>
<li><a href="${gzUrl('/settings/folders/create!doInput.action')}">Create new folder</a></li>
<li><a href="${gzUrl('/settings/feeds/create!doInput.action')}">Subscribe to RSS feed</a></li>
</ul>
</@settingsBlock>
<@settingsBlock title="Manage Tags">
<ul>
<li><a href="${gzUrl("/edit/tag")}" title="edit tag relations">Create/Edit tag relations</a></li>
<@ww.url id="deleteTagHref" value="/settings/tags/delete.action" includeParams="none"/>
<@ww.url id="renameTagHref" value="/settings/tags/rename.action" includeParams="none"/>
<li><a href="${deleteTagHref}" title="delete tags">Delete tags</a></li>
<li><a href="${renameTagHref}" title="rename tags">Rename tags</a></li>
</ul>
</@settingsBlock>

<@settingsBlock title="Import/Export Bookmarks">
<ul>
<li><a href="${gzUrl("/settings/delicious.action")}">Import from del.icio.us</a></li>
<li><a href="${gzUrl("/settings/export/bookmark.action")}">Export bookmarks</a></li>
</ul>
</@settingsBlock>

<#if loggedInUser.username == 'gnizr'>
<@settingsBlock title="Administration">
<ul>
<@ww.url id="adminUser" namespace="/admin" action="editUser.action" includeParams="none"/>
<li><a href="${adminUser}" title="edit user accounts">Manage user accounts</a></li>
<@ww.url id="adminRssRobotHref" namespace="/admin" action="robot.action" includeParams="none"/>
<li><a href="${adminRssRobotHref}" title="change RSS robot settings">Manage RSS robot</a></li>
<@ww.url id="upIndexHref" namespace="/settings" action="indexBookmark.action" includeParams="none"/>
<li><a href="${upIndexHref}" title="rebuild search index">Rebuild search index</a></li>
</ul>
</@settingsBlock>
<#--
<@settingsBlock title="Software Migration Tools">
<ul>
<@ww.url id="updateOwnershipHref" namespace="/admin" action="updateOwnership.action" includeParams="none"/>
<li><a href="${updateOwnershipHref}" title="update bookmark ownership">Update bookmark ownership</a> (for migrating from eariler versions to 2.2-M2)</li>
</ul>
</@settingsBlock>
-->
</#if>

</@mainBlock>
</@pageContent>
<@pageEnd/>

</@ensureUserLoggedIn>