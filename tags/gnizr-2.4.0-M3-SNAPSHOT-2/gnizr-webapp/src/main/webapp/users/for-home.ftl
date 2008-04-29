<#-- /users/for-home.ftl -->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to USERS -->
<#include "./macro-lib.ftl"/>
<#assign username = loggedInUser.username/>
<#assign title="pages saved for '${username}'"/>
<#assign thisPageBaseHref=gzUrl("/for/me")/>
<#if sender?exists && sender != ''>
  <#assign thisPageHref=thisPageBaseHref+"/from/"+sender/>
<#else>
  <#assign thisPageHref=thisPageBaseHref/>
</#if>
<#assign toPageHref = getToPageHref(thisPageHref)/>
<@pageBegin pageTitle=title 
            thisPageBaseHref=thisPageBaseHref
            thisPageHref=thisPageHref 
            toPageHref=toPageHref/>
<@headerBlock/>
<@pageContent>
<#assign bct = [
  gzBCTPair(username,gzUserUrl(username)),
  gzBCTPair('for ' + username,gzUrl("/for/me"))
]/>
<#if sender?exists && sender != ''>
  <#assign bct = bct + [gzBCTPair('by ' + sender,gzUserUrl(sender))]/>
</#if>
<@infoBlock bct=bct/>
<@mainBlock>
<@ww.if test="hasActionErrors()">
  <ul>
  <@ww.iterator value="actionErrors">
    <li><@ww.property/></li>
  </@ww.iterator>
  <#if (loggedInUser.username)?exists >
    <li>To view pages that are saved for you, <a href="${gzUrl("/for/"+loggedInUser.username)}">click here</a>.</li>
  </#if>
  </ul>
</@ww.if>
<@ww.elseif test="gnizrForUsers != null && gnizrForUsers.size() > 0">
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount>
 <#-- <@viewInTimeRangeControl pageHref="/for/me"/> -->
  </@pagerControl>
  <@forUserList forUsers=gnizrForUsers/>  
  <@pagerControl pageHref=thisPageHref pageNum=page totalPageNum=pageTotalCount>
 <#-- <@viewInTimeRangeControl pageHref="/for/me"/> -->
  </@pagerControl>
  <@viewConfigBlock pageHref=thisPageHref/>  
</@ww.elseif>
<@ww.else>
  <ul><li>No bookmarks here.</li></ul>
</@ww.else>
</@mainBlock>

<#if (gnizrForUsers?size > 0)>
<@sidebarBlock cssClass="colored-sidebar">
  <@sidebarElement>
    <@ww.url id="purgeallHref" action="purgeall" namespace="/for/me" includeParams="none"/>
    <a href="${purgeallHref}" title="delete all bookmarks saved for me">purge all bookmarks</a>
  </@sidebarElement>
  <@ww.action name="formesenders" namespace="/bookmark" executeResult=true>    
  </@ww.action>
</@sidebarBlock>
</#if>
</@pageContent>
<@pageEnd/>

