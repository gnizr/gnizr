<#-- /tags/home.ftl -->

<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to TAGS -->
<#include "../tags/macro-lib.ftl"/>

<#assign title="filter subscriptions -- gnizr"/>
<#assign thisPageHref="/subscription/filter"/>

<@pageBegin pageTitle=title/>

<@headerBlock>
<a href="${gzUrl("/")}">gnizr</a> :: query feed :: ${bookmark.title}
</@headerBlock>

<@infoBlock>
<p>
Query and pipe intelligent RSS feeds
</p>
</@infoBlock>

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
  <div>
    ${form}
  </div>
  <div id="searchResults">
    This is where the search results will go
  </div>
</@mainBlock>

<@pageEnd/>