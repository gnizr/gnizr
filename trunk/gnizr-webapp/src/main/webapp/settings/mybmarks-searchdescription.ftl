<#include "/lib/web/macro-lib.ftl"/>
<?xml version="1.0" encoding="UTF-8"?>
<OpenSearchDescription 
  xmlns="http://a9.com/-/spec/opensearch/1.1/"
  xmlns:gn="http://gnizr.com/ont/opensearch/2007/11/">
  <ShortName>My Gnizr Bookmarks</ShortName>
  <Description>Search my saved bookmarks in gnizr</Description>
  <Tags>bookmarks gnizr</Tags>
  <Url type="text/xml" 
       template="${gzUrl("/data/atom/user/search.action?queryString={searchTerms}&amp;page={startPage}")}"/>
  <gn:LoginRequired>true</gn:LoginRequired>
</OpenSearchDescription>