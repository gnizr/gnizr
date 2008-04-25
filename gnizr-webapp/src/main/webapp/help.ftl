<#include "/lib/web/macro-lib.ftl"/>
<@pageBegin pageTitle="help"> 
</@pageBegin>          
<@headerBlock></@headerBlock>
<@pageContent>
<@mainBlock>
<h2>Browser Tools</h2>
<div class="instruction">
<p>
You can add these tools to your browser. They provide an integrated user experience for 
saving bookmarks, visiting your bookmark collections and searching for information.  
</p>
</div>
<h3>Save bookmark tool</h3>
<ul>
  <li>Drag 
  '<a href="javascript:var d=document,g='${gzUrl("/post?")}',l=d.location,e=encodeURIComponent,p='url='+e(l.href)+'&title='+e(d.title);1;a=function(){if(!window.open(g+p+'&saveAndClose=true','${getSiteName()}','toolbar=no,status=no,resizable=yes,width=800,height=640,scrollbars=yes'))l.href=g+p+'&saveAndClose=false&redirect=true'};if(/Firefox/.test(navigator.userAgent))setTimeout(a,0);else{a()};void(0)" title="post to ${getSiteName()}" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to ${getSiteName()}</a>' 
  up to your Bookmark Toolbar.</li>
  <li>Drag 
  '<a href="javascript:(function(){setTimeout(function(){var g='${gzUrl("/post?")}', p='url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title); try{window.open(g+p+'&saveAndClose=true','gnizr','toolbar=no,status=no,resizable=yes,width=800,height=640,scrollbars=yes');}catch(e){location.href=g+p+'&saveAndClose=false&redirect=true';}},10);})()" title="post to ${getSiteName()}" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to ${getSiteName()}</a>' 
  up to your Bookmark Toolbar. (experimental)</li>
</ul>
<h3>Your bookmark homepage</h3>
<ul>  
  <li>Drag this link: '<a href="javascript:location.href='${gzUrl("/home")}'" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">my ${getSiteName()} bookmarks</a>' up to your Bookmarks Toolbar.
</ul>
<h3>Add browser search plugin</h3>
<ul>
  <li>Click <a href="javascript:window.external.AddSearchProvider('${gzUrl('/settings/opensearch/description.action')}');">this link</a> to add a search plugin your Firefox or Internet Explorer 7 browser.</li>
</ul>
</@mainBlock>
</@pageContent>
<@pageEnd/>