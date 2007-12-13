<#include "/lib/web/macro-lib.ftl"/>
<@pageBegin pageTitle="help -- gnizr" />          
<@headerBlock></@headerBlock>
<@pageContent>
<@mainBlock>
<h2>gnizr tools</h2>
<p>
<h3>Save bookmark tool</h3>
<ul>
  <li>Drag 
  '<a href="javascript:var d=document,g='${gzUrl("/post?")}',l=d.location,e=encodeURIComponent,p='url='+e(l.href)+'&title='+e(d.title)+'&redirect=false&saveAndClose=true';1;a=function(){if(!window.open(g+p,'gnizr','toolbar=0,status=0,resizable=0,width=800,height=640,scrollbars=yes'))l.href=g+p};if(/Firefox/.test(navigator.userAgent))setTimeout(a,0);else{a()};void(0)" title="post to gnizr" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to gnizr</a>' 
  up to your Bookmark Toolbar.</li>
</ul>
<h3>Your gnizr homepage</h3>
<ul>  
  <li>Drag this link: '<a href="javascript:location.href='${gzUrl("/home")}'" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">my gnizr</a>' up to your Bookmarks Toolbar.
</ul>
</p>
</@mainBlock>
</@pageContent>
<@pageEnd/>