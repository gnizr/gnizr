<#-- /webapp/links/home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to LINKS -->
<#include "./macro-lib.ftl"/>

<#-- use first bookmark object as the representative link object -->
<#assign thisbmark = bookmarks?first />
<#assign thislink = thisbmark.link/>

<#if (thislink.url)?exists>
  <#assign title="url: ${thislink.url}"/>
<#else>
  <#assign title="link history"/>
</#if>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-linkhistory.css")]/>

<@headerBlock>
</@headerBlock>

<@pageContent>
 <#assign bct = communityBCT()/>    
 <@infoBlock bct = bct/>
<@ww.if test="hasActionErrors()">
  <ul>
  <@ww.iterator value="actionErrors">
    <li><@ww.property/></li>
  </@ww.iterator>
</@ww.if> 
<@ww.else> 
  <@linkDescription bookmark=thisbmark/>
  <#-- 
     syntax: bookmarks=bookmarks 
     1st bookmarks is the argument of the macro.
     2nd bookmarks is an object that we popped off from the webwork value stack
  -->
  <@userCommentList bookmarks=bookmarks/>
</@ww.else>

<#if commonTags?exists>
<@sidebarBlock cssClass="colored-sidebar">
  <@sidebarElement>
    <@commonTagList commonTags=commonTags/>
  </@sidebarElement>  
</@sidebarBlock>
</#if>

</@pageContent>
<@pageEnd/>