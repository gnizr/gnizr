<#-- /webapp/bookmarks/edit.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<@ensureUserLoggedIn>
<#assign username=loggedInUser.username/>
<#if url?exists >
  <#assign title="save url: " + url>
<#else>
  <#assign title="save url"/>
</#if>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-post.css")]>                                  
</@pageBegin>
<@headerBlock>
</@headerBlock>

<@pageContent>
<@infoBlock>save url to my bookmarks</@infoBlock>
<@ww.actionerror/>
<@ww.actionmessage/>

<@ww.form id="saveBookmark" action="/bookmark/edit.action" method="post" cssClass="userInputForm">
  <@ww.textfield cssClass="text-input-url" required="true" label="url" 
                 size="80" name="url" value="${url?if_exists}"/>  
  <@ww.submit cssClass="btn" value="next"/>
</@ww.form>
</@pageContent>
<@pageEnd/>

</@ensureUserLoggedIn>