<?xml version="1.0" encoding="UTF-8"?>
<#include "/lib/web/macro-lib.ftl"/>
<#include "../users/macro-lib.ftl"/>
<#assign fldname = gzFormatFolderName(folderName)?html/>
<#assign title="${username}'s bookmarks saved in '${fldname}' tagged '${tag}'"/>
<#assign thisPageHref=gzUserUrl(username)/>
<rdf:RDF
	xmlns="http://purl.org/rss/1.0/"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:content="http://purl.org/rss/1.0/modules/content/"
	>
<channel rdf:about="${thisPageHref}">
	<title>${title?html}</title>
	<link>${thisPageHref}</link>
	<description>10 bookmarks saved by ${username} in folder '${fldname}' tagged '${tag?html}'</description>
	<dc:date>${getDateTimeISO8601(now)}</dc:date>
	<items>
	  <rdf:Seq>
	<#list bookmarks as bm>
	    <rdf:li resource="${gzBookmarkUrl(bm.id?c)}"/>	   	
	</#list>
	  </rdf:Seq>
	</items>
</channel>
<#list bookmarks as bm>
<item rdf:about="${gzBookmarkUrl(bm.id?c)}">
  <title>${bm.title?html}</title>
  <link>${gzBookmarkUrl(bm.id?c)?html}</link>
  <dc:date>${getDateTimeISO8601(bm.lastUpdated)}</dc:date>
  <dc:creator>${bm.user.fullname}</dc:creator>
  <dc:subject>
  <#list bm.tagList as tag>
    ${tag} 
  </#list>
  </dc:subject>
  <description>${makeShortNotes(bm.notes)?html}</description>
</item>
</#list>
</rdf:RDF>