<#-- 
 clustermap/emptymodel.ftl
 A freemarker template that generates an empty user bookmark list in CLUSTERMAP XML output.
 this template is used to handle a special case when the user has no saved bookmark.
-->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#assign title="${username}'s bookmarks"/>
<#assign thisPageHref=gzUserUrl(username)/>
<?xml version="1.0" encoding="UTF-8"?>
<ClassificationTree version="1.0">
<ClassificationSet>

  <Classification ID="root">
     <Name>${username}'s bookmarks</Name>
     <Objects objectIDs=""/>
  </Classification>
  
</ClassificationSet>

</ClassificationTree>