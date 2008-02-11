<#-- /tags/new-usertag.ftl -->

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to TAGS -->
<#include "./macro-lib.ftl"/>
<#include "/settings/macro-lib.ftl"/>
<#assign username = loggedInUser.username/>
<#assign title="edit tag relations -- gnizr"/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-tagcloud.css"),
	                                 gzUrl("/css/gnizr-editusertag.css")]/>

<@headerBlock>
</@headerBlock>
<@pageContent>
<#assign bct = settingsBCT(loggedInUser.username)/>
<#assign bct = bct + [gzBCTPair('edit tag relations', gzUrl("/edit/tag"))]/> 
<@infoBlock bct=bct/>
<@mainBlock>
<p class="instruction">
Tag relations define how different tags are related to each other. Making tag relations explicit can 
help you to organize your tag cloud. 
</p>
<p class="instruction">
Define tag relations for a new tag or an existing tag. 
</p>
<p class="instruction">
<b>Note</b>: New tags will not appear in your tag cloud unless it is used 
by one or more bookmarks.
</p>
<div id="createNewTag">
<h4>Create a new tag</h4>
<@ww.form action="edit" method="post" namespace="/settings/tags">
  <@ww.textfield id="tag" label="tag" name="tag" required="true"/>
  <@ww.submit value="create" cssClass="btn"/>
</@ww.form> 
<p><@ww.actionmessage/></p>
</div>
<div id="selectUserTag">
<h4>Select an existing tag</h4>
  <@userTagCloudEdit userTags=userTags editAction="/settings/tags/edit.action"/>
</div>
</@mainBlock>
</@pageContent>
<@pageEnd/>

<#macro userTagCloudEdit userTags editAction>
<ol class="tag-cloud">
<#list userTags as ut>
  <@ww.url id="editTagUrl" value=editAction tag=ut.tag.label includeParams="none"/>  
  <li><a href="${editTagUrl}" title="edit: ${ut.tag.label}">${ut.tag.label}</a></li>  
</#list>
</ol>
</#macro>
