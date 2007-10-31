<#include "/lib/web/macro-lib.ftl"/>
<@pageBegin pageTitle="help -- gnizr" />          
<@headerBlock></@headerBlock>
<@pageContent>
<@mainBlock>
<h2>gnizr tools</h2>
<p>
<h5>Save bookmark tool</h5>
<ul>
  <li>For users of Internet Explorer: drag 
  '<a href="javascript:location.href='${gzUrl("/post")}?url='+encodeURIComponent(location.href)+'&amp;title='+encodeURIComponent(document.title)+'&redirect=true'" title="post to gnizr" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to gnizr</a>' 
  up to your Bookmark Toolbar.</li>
 
  <li>For users of Firefox: drag '<a href="javascript:function post2gnizr(url,title){window.open('${gzUrl("/post")}?url='+encodeURIComponent(url)+'&title='+encodeURIComponent(title)+'&redirect=false&saveAndClose=true','gnizr','toolbar=no,width=800,height=640,alwaysRaised=yes,left=0,scrollbars=yes');location.href=url;}; post2gnizr(location.href,document.title);">post to gnizr</a>' up to your Bookmarks Toolbar.</li>
</ul>
<h5>Your gnizr homepage</h5>
<ul>  
  <li>Drag this link: '<a href="javascript:location.href='${gzUrl("/home")}'" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">my gnizr</a>' up to your Bookmarks Toolbar.
</ul>
</p>
</@mainBlock>
</@pageContent>
<@pageEnd/>