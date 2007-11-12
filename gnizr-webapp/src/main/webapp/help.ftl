<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >

<#include "/lib/web/macro-lib.ftl"/>

<@pageBegin pageTitle="help -- gnizr" />
          
<@headerBlock></@headerBlock>
<@pageContent>
<@mainBlock>
<h2>gnizr tools</h2>
<p>
<ul>
  <#assign postHref = gzUrl("/post?url='+encodeURIComponent(location.href)+'&amp;title='+encodeURIComponent(document.title)+'&redirect=true")/>
  <li>Drag this link: <a href="javascript:location.href='${postHref}'" title="post to gnizr" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to gnizr</a>' up to your Bookmarks Toolbar.</li>
  <#assign homeHref = gzUrl("/home")/>
  <li>Drag this link: <a href="javascript:location.href='${homeHref}'" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">my gnizr</a> up to your Bookmarks Toolbar.
</ul>
</p>
</@mainBlock>
</@pageContent>
<@pageEnd/>