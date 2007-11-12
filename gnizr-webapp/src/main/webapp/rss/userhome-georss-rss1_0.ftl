<#include "/lib/web/macro-lib.ftl"/>
<#include "../users/macro-lib.ftl"/>
<#assign title="${username}'s bookmarks"/>
<#assign thisPageHref=gzUserUrl(username)/>
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
	xmlns="http://purl.org/rss/1.0/"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:content="http://purl.org/rss/1.0/modules/content/"
	xmlns:georss="http://www.georss.org/georss"
	>
<channel rdf:about="${thisPageHref}">
	<title>${title}</title>
	<link>${thisPageHref}</link>
	<description>Geotagged bookmarks saved by ${username}</description>
	<dc:date>${getDateTimeISO8601(now)}</dc:date>
	<items>
	  <rdf:Seq>
	<#list bookmarks as bm>
	    <rdf:li resource="${gzUrl("/bookmark/"+bm.id?c)}"/>	   	
	</#list>
	  </rdf:Seq>
	</items>
</channel>
<#list bookmarks as bm>
<item rdf:about="${gzUrl("/bookmark/"+bm.id?c)}">
  <title>${bm.title}</title>
  <link>${bm.link.url?html}</link>
  <dc:date>${getDateTimeISO8601(bm.lastUpdated)}</dc:date>
  <dc:creator>${bm.user.fullname}</dc:creator>
  <dc:subject>
  <#list bm.tagList as tag>
    ${tag} 
  </#list>
  </dc:subject>
  <#assign notes = ""/>
  <#if bm.notes?exists>
    <#assign notes = bm.notes/>
  </#if>
  <description>${notes?html}</description>
  <#assign ptMarkers = bm.pointMarkers/>
  <#list ptMarkers as ptm>
  <georss:point>${ptm.y} ${ptm.x}</georss:point>
  </#list>
</item>
</#list>
</rdf:RDF>