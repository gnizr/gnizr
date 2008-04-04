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
<p class="instruction">
Delete tags that are used by your bookmarks. Deleting a tag will not
remove any bookmarks that use it. 
</p>
<ol class="instruction">
<li>Select a tag from the list to be deleted</li>
<li>Click the "delete" button</li>
</ol>
<p class="instruction">
Tip: You can also 
<@ww.url id="renameTagHref" value="/settings/tags/rename.action" includeParams="none"/>
<a href="${renameTagHref}" title="rename tags">rename tags</a>.
</p>
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
<@ww.form action="delete.action" method="post">
<@ww.select label="tag" 
            name="tag"         
            list=opts/>         
<@ww.submit value="delete" cssClass="btn"/>            
</@ww.form>
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