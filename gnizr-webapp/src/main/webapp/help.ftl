<#include "/lib/web/macro-lib.ftl"/>
<@pageBegin pageTitle="help -- gnizr" />          
<@headerBlock></@headerBlock>
<@pageContent>
<@mainBlock>
<h2>gnizr tools</h2>
<p>
<h3>Save bookmark tool</h3>
<h4>Option 1: For browsers with Pop-up disabled</h4>
<ul>
  <li>Drag 
  '<a href="javascript:location.href='${gzUrl("/post")}?url='+encodeURIComponent(location.href)+'&amp;title='+encodeURIComponent(document.title)+'&redirect=true'" title="post to gnizr" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to gnizr</a>' 
  up to your Bookmark Toolbar.</li>
</ul>
<h4>Option 2: For browsers permits Pop-up windows</h4>
<ul> 
  <li>Drag '<a href="javascript:function post2gnizr(url,title){window.open('${gzUrl("/post")}?url='+encodeURIComponent(url)+'&title='+encodeURIComponent(title)+'&redirect=false&saveAndClose=true','gnizr','alwaysRaised=yes,toolbar=no,width=800,height=640,left=0,scrollbars=yes');}; post2gnizr(location.href,document.title);" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to gnizr</a>' up to your Bookmarks Toolbar.</li>
</ul>
<h3>Your gnizr homepage</h3>
<ul>  
  <li>Drag this link: '<a href="javascript:location.href='${gzUrl("/home")}'" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">my gnizr</a>' up to your Bookmarks Toolbar.
</ul>
</p>
</@mainBlock>
</@pageContent>
<@pageEnd/>