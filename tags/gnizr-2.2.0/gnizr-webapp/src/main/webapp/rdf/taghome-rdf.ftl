<#-- 
 taghome-rdf.ftl  
 A freemarker template that displays bookmarks that are tagged with a specific keyword
-->
<#include "/lib/web/macro-lib.ftl"/>
<#include "/lib/web/rdf-macro-lib.ftl"/>
<?xml version="1.0" encoding="UTF-8"?>
<rdf:RDF
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:owl="http://www.w3.org/2002/07/owl#"
	xmlns:vs="http://www.w3.org/2003/06/sw-vocab-status/ns#"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:wot="http://xmlns.com/wot/0.1/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:sioc="http://rdfs.org/sioc/ns#"
	xmlns:tags="http://www.holygoat.co.uk/owl/redwood/0.1/tags/">
<sioc:Forum rdf:about="${getTagUri(tag)}">
  <sioc:name>Bookmarks tagged ${tag?html}</sioc:name>
  <sioc:link>${getTagUri(tag)}</sioc:link>
<#list bookmarks as bm>
  <sioc:container_of>
   <sioc:Post rdf:about="${getSIOCPostLink(bm)}" dc:title="${bm.title?html}">	  
   <sioc:has_creator>
     <sioc:User rdf:about="${getSIOCForumUri(bm.user)}" rdfs:label="${bm.user.fullname?if_exists}">
         <rdfs:seeAlso rdf:resource="${getSIOCForumUri(bm.user)}"/>
     </sioc:User>
   </sioc:has_creator>
   <#list bm.tagList as aTag>
	  <tags:taggedWithTag>
	    <tags:Tag rdf:about="${getTagUri(aTag)}">
	      <tags:tagName>${aTag?html}</tags:tagName>
	    </tags:Tag>
	  </tags:taggedWithTag>
   </#list>
   </sioc:Post>
  </sioc:container_of>
</#list>
</sioc:Forum>	
</rdf:RDF>