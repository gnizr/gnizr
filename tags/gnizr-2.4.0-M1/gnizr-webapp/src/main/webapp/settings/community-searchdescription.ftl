<#include "/lib/web/macro-lib.ftl"/>
<?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription xmlns="http://a9.com/-/spec/opensearch/1.1/" 
   xmlns:gn="http://gnizr.com/ont/opensearch/2007/11/">
  <ShortName>Bookmarked by others</ShortName>
  <Description>Search bookmarks in ${getSiteName()}</Description>
  <Tags>bookmarks gnizr</Tags>
  <gn:DefaultEnabled>true</gn:DefaultEnabled>
  <Url type="application/vnd.gn-opensearch+json"
       template="${gzUrl("/data/json/community/searchBookmark.action?queryString={searchTerms}&amp;page={startPage}")}"/>
</OpenSearchDescription>