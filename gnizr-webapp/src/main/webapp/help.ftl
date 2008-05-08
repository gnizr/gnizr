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
<div class="system-message">
<b>For Internet Explorer Users</b>: If you cannot drag to save links, use right-click to save links to your Favorites folder.
</div>
</div>
<h4>Save Bookmark Tool</h4>
<ul>
  <li>Drag 
  '<a href="javascript:(function(){setTimeout(function(){var g='${gzUrl("/post?url=")}'+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title)+'&saveAndClose=';try{var u=g+'true', t='gnizr',o='toolbar=no,status=no,resizable=yes,width=800,height=640,scrollbars=yes',w=window.open(u,t,o); if(!w){w=window.open('',t,o);w.location.href=u;}} catch(e){w=null;};if(w==null){location.href=g+'false&redirect=true';}},10);})()" title="post to ${getSiteName()}" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">post to ${getSiteName()}</a>' 
  up to your Bookmark Toolbar.</li>
</ul>
<h4>Your Bookmark Homepage</h4>
<ul>  
  <li>Drag this link: '<a href="javascript:location.href='${gzUrl("/home")}'" onclick="window.alert('Drag this link to your bookmarks toolbar, or right-click it and choose Bookmark This Link...');return false;">my ${getSiteName()} bookmarks</a>' up to your Bookmarks Toolbar.
</ul>
<h4>Add Browser Search Plugin</h4>
<ul>
  <li>Click <a href="javascript:window.external.AddSearchProvider('${gzUrl('/settings/opensearch/description.action')}');">this link</a> to add a search plugin your Firefox or Internet Explorer 7 browser.</li>
</ul>
</@mainBlock>
</@pageContent>
<@pageEnd/>