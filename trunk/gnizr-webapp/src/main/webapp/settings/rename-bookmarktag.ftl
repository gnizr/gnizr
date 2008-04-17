<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to SETTING -->
<#include "./macro-lib.ftl"/>
<@ensureUserLoggedIn>
<#assign title="rename tags"/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title />
<@headerBlock/>

<@pageContent>
<#assign bct = settingsBCT(username) + [gzBCTPair('rename tags',gzUrl('/settings/tags/rename.action'))]/>
  <@infoBlock bct=bct/>
<@mainBlock>
<@pageTitle>Rename Tags</@pageTitle>
<@pageDescription>
<p>
Define new tags to replace an existing tag. 
Any bookmarks that use that tag will be automatically updated. 
</p>
<ol>
<li>Select a tag from the list to be renamed</li>
<li>Enter one or more new tags -- separate tags with spaces</li>
<li>Click the "rename" button</li>
</ol>
<p>
<@ww.url id="deleteTagHref" value="/settings/tags/delete.action" includeParams="none"/>
<b>Tip</b>: You can also <a href="${deleteTagHref}" title="delete tags">delete tags</a>.
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
<@ww.form action="rename.action" method="post">
<@ww.select label="Tag to be renamed" 
            name="tag"             
            list=opts/>         
<@ww.textfield label="New tag/tags" name="newTag" size="20"/>            
<@ww.submit value="rename" cssClass="btn"/>            
</@ww.form>
</@formInput>
<#if tag?exists>
<p>Rename tag: <b>${tag}</b>
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