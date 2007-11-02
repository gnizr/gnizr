<#include "/lib/web/macro-lib.ftl"/>
<?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/">
  <ShortName>Gnizr Community</ShortName>
  <Description>Search bookmarks in gnizr</Description>
  <Tags>bookmarks gnizr</Tags>
  <Url type="text/xml" 
       template="${gzUrl("/data/atom/community/search.action?queryString={searchTerms}&amp;page={startPage}")}"/>
</OpenSearchDescription>