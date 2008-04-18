<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>
<@ensureUserLoggedIn>
<#assign title="delete tags "/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title />
<@headerBlock>
</@headerBlock>

<@pageContent>
<#assign bct = settingsBCT(username) + [gzBCTPair('rename tags',gzUrl('/settings/tags/rename.action'))]/>
  <@infoBlock bct=bct/>
<@mainBlock>
<@pageTitle>Delete Tags</@pageTitle>
<@pageDescription>
<p>
Delete tags that are used by your bookmarks. Deleting a tag will not
remove any bookmarks that use it. 
</p>
<ol>
<li>Select a tag from the list to be deleted</li>
<li>Click the "Delete" button</li>
</ol>
<p>
<b>Tip</b>: You can also 
<@ww.url id="renameTagHref" value="/settings/tags/rename.action" includeParams="none"/>
<a href="${renameTagHref}" title="rename tags">rename tags</a>.
</p>
</@pageDescription>
<div id="edit-bookmarktag">
<#assign opts = "#"+"{"/>
<#list userTags as ut>
  <#assign itm = "'"+ut.tag.label+"':'"+ut.tag.label+" ("+ut.count+")'"/>
  <#if ut_has_next>
    <#assign itm=itm+","/>
  </#if>
  <#assign opts = opts+itm/>
</#list>
<#assign opts = opts + "}"/>

<@formInput>
<@ww.form action="delete.action" method="post">
<@ww.select label="Your tag" 
            name="tag"         
            list=opts/>         
<@ww.submit value="Delete" cssClass="btn"/>            
</@ww.form>
</@formInput>

<#if tag?exists>
<p>
Delete tag: <b>${tag}</b>
<#if action.actionMessages?has_content>
... Failed!</p>
   <div class="errorMessage">
   <ul>
   <#list action.actionMessages as msg>
     <li>${getActionMsg(msg)}</li>
   </#list>
   </ul>
   </div>
<#else>
... Succeed!</p>  
</#if>
</#if>
</div>
</@mainBlock>
</@pageContent>
<@pageEnd/>
</@ensureUserLoggedIn>

<#function getActionMsg code>
  <#if code == "DELETE_TAG_FAILED">
    <#return "error: tag doesn't exist."/>
  <#elseif code == "RENAME_TAG_FAILED">
    <#return "error: tag doesn't exist."/>
  </#if>
</#function>