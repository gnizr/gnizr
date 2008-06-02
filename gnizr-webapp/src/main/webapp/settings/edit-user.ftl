<#-- /settings/edit-user.ftl -->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>

<#assign title="manage user accounts"/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css"),
                                     gzUrl("/css/gnizr-edituser.css")]/>
 
<@headerBlock/>

<@pageContent>
<#assign bct = settingsBCT(loggedInUser.username) +
               [gzBCTPair('edit users',gzUrl('/admin/editUser.action'))]/> 
<@infoBlock bct=bct/>
<@mainBlock>
<@pageTitle>Manage User Accounts</@pageTitle>
<@pageDescription>
<p>
Users who currently have access to gnizr. Deleting a user will permanently remove all 
bookmarks of that user. To change a user's password, full name, email address and account status,
click "edit profile". 
</p>
<p>
About "Acct Status":
<ul>
  <li>ON HOLD: email address is not verified; user can't login</li>
  <li>ACTIVE: email address is verified; user can login</li>
  <li>DISABLED: user login is prohibited</li>
</ul>
</p>
</@pageDescription>
<table class="usersTable">
<tr><th>Username</th><th>Full Name</th><th>Email</th><th>Created On</th><th>Acct Status</th><th>Action</th></tr>
<#list gnizrUsers as gUser>
  <@userInfoRow user=gUser/>
</#list>
<@ww.url id="newUserHref" namespace="/admin" action="newUser.action" includeParams="none"/>
<tr><td></td><td></td><td></td><td></td><td></td><td><a href="${newUserHref}" title="add new user accounts">add a new user</a></td></tr>
</table>
<@ww.actionmessage/>
</@mainBlock>
</@pageContent>
<@pageEnd/>

<#macro userInfoRow user>
<tr>
<td><a href="${gzUserUrl(user.username)}">${user.username}</a></td>
<td>${user.fullname}</td>
<td>${user.email}</td>
<td>${user.createdOn?string("yyyy-MM-dd")}</td>
<td>${getAccountStatusLabel(user.accountStatus)}</td>
<td><@editAction username=user.username/></td>
</tr>
</#macro>

<#macro editAction username>
<@ww.url id="delete" value="deleteUser.action" username=username includeParams="none"/>
<a href="${delete}" title="delete user account: ${username}">delete user</a> |
<@ww.url id="edit" value="editProfile.action" username=username includeParams="none"/>
<a href="${edit}" title="edit user profile: ${username}">edit profile</a>
</#macro>

<#function getAccountStatusLabel acctStat>
  <#if acctStat == 0>
    <#return "ON HOLD"/>
  <#elseif acctStat == 1>
    <#return "ACTIVE"/>
  <#elseif acctStat == 2>
    <#return "DISABLED">
  </#if>
</#function>