<#-- /tags/edit-usertag.ftl -->

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to TAGS -->
<#include "./macro-lib.ftl"/>
<#assign username = loggedInUser.username/>
<#assign title="edit tag '${tag}' -- gnizr"/>
<#assign thisPageHref=gzTagUrl(tag)/>

<#if editValueName?exists == false>
  <#assign editValueName="relatedTags"/>
</#if>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-editusertag.css")]/>

<@headerBlock>
</@headerBlock>

<@pageContent>
<#assign bct = settingsBCT(loggedInUser.username)/>
<#assign bct = bct + [gzBCTPair('edit tag relations', gzUrl("/edit/tag")),
                      gzBCTPair(tag,gzUrl("/settings/tags/edit.action?tag="+tag?url))]/> 
<@infoBlock bct=bct/>
<@mainBlock>
<p class="instruction">
Define semantic relationships between <b>'${tag}'</b> and other tags. 
</p>
<ol class="instruction">
<li>Choose a relation on the left.</li>
<li>Enter one or more tags on the right.</li>
<li>Tags should be seperated with spaces.</li>
<li>Click "Save" when you are done.</li>
</ol>
<p class="instruction">
<b>Tips</b>: To define relations between your tags and those of the other users, use Machine Tag "tag". 
</p>
<ul class="instruction">
<li><code>tag:joe/java</code> -- tag "java" of user "joe"</li>
<li><code>tag:joe/semantic_web</code> -- tag "semantic_web" of user "joe"</li>
</ul>
<div id="editArea">
<div id="controlPanel">
<ul>
<@ww.url id="editRelatedTagsHref" namespace="/settings/tags" action="edit.action" editValueName="relatedTags" tag=tag/>
<li><a href="${editRelatedTagsHref}" rel="${relatedTags}" class="${getEditOptionClass("relatedTags")}"><span class="editTag">${tag}</span> is related to</a></li>
<@ww.url id="editNarrowerTagsHref" namespace="/settings/tags" action="edit.action"  editValueName="narrowerTags" tag=tag/>
<li><a href="${editNarrowerTagsHref}" rel="${narrowerTags}" class="${getEditOptionClass("narrowerTags")}"><span class="editTag">${tag}</span> has narrower</a></li>
<@ww.url id="editBroaderTagsHref" namespace="/settings/tags" action="edit.action" editValueName="broaderTags" tag=tag/>
<li><a href="${editBroaderTagsHref}" rel="${broaderTags}" class="${getEditOptionClass("broaderTags")}"><span class="editTag">${tag}</span> has broader</a></li>
<@ww.url id="editInstanceTagsHref" namespace="/settings/tags" action="edit.action" editValueName="instanceTags" tag=tag/>
<li><a href="${editInstanceTagsHref}" rel="${instanceTags}" class="${getEditOptionClass("instanceTags")}"><span class="editTag">${tag}</span> has group member</a></li>
</ul>
</div>
<div id="editor">
<@ww.form action="save.action" method="post" namespace="/settings/tags">
<@ww.hidden name="tag" value="${tag}"/>
<@ww.hidden name="editValueName" value="${editValueName}"/>
<@ww.textarea name="${editValueName}" cssClass="editor-input" rows="8" id="editValue"/>
<@ww.submit value="save" cssClass="btn"/>
</@ww.form>
<@ww.actionmessage/>
</div>
</div>
</@mainBlock>
</@pageContent>
<@pageEnd/>

<#function getEditOptionClass name>
  <#if name == editValueName>
    <#return "optionFocused"/>
  <#else>
    <#return "option"/>
  </#if>
</#function>