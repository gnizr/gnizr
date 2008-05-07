<#-- 
===================================================================
MACRO: pageEnd
INPUT: rssHref:String // a feed URL of this page
===================================================================
-->
<#macro pageEnd rssHref="" rdfHref="">
<!-- FOOTER BEGINS -->
<div id="footer">
  <div id="sitemap"> 
    &copy; 2006-2008 by 
    <a href="http://www.imagemattersllc.com" title="Image Matters LLC" class="snap_noshots">Image Matters LLC</a> 
    | <a href="http://gnizr.googlecode.com" title="Gnizr Project Home" class="snap_noshots">Gnizr Project</a>
    <#-- only display RSS href if there is one defined     -->
    <#if (rssHref?length > 0)>
    | <a href="${rssHref?html}"><img src="${gzUrl("/images/rss.gif")}" alt="rss for this page"/></a>
    </#if>
    <#if (rdfHref?length > 0)>
    | <a href="${rdfHref?html}"><img src="${gzUrl("/images/rdf.gif")}" alt="rdf of this page"/></a>
    </#if>
  </div>
</div>

<!-- FOOTER ENDS -->
</body>
<!-- HTML BODY ENDS -->
</html>
</#macro>
