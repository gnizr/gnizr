<#-- 
 users/home-rss1_0.ftl  
 A freemarker template that displays user bookmakrs in SIMILE Timeline XML output.
-->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to USERS -->
<#include "../users/macro-lib.ftl"/>
<#assign title="${username}'s bookmarks"/>
<#assign thisPageHref=gzUserUrl(username)/>
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
	xmlns="http://purl.org/rss/1.0/"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:content="http://purl.org/rss/1.0/modules/content/"
	>
<channel rdf:about="${thisPageHref}">
	<title>${title?html}</title>
	<link>${thisPageHref}</link>
	<description>10 bookmarks recently saved by ${username}</description>
	<dc:date>${getDateTimeISO8601(now)}</dc:date>
	<items>
	  <rdf:Seq>
	<#list bookmark as bm>
	    <rdf:li resource="${gzUrl("/bookmark/"+bm.id)}"/>	   	
	</#list>
	  </rdf:Seq>
	</items>
</channel>
<#list bookmark as bm>
<item rdf:about="${gzUrl("/bookmark/"+bm.id)}">
  <title>${bm.title?html}</title>
  <link>${bm.link.url?html}</link>
  <dc:date>${getDateTimeISO8601(bm.lastUpdated)}</dc:date>
  <dc:creator>${bm.user.fullname}</dc:creator>
  <dc:subject>
  <#list bm.tagList as tag>
    ${tag?html} 
  </#list>
  </dc:subject>
  <description>${(bm.notes?if_exists)?html}</description>
</item>
</#list>
</rdf:RDF>