<#-- 
 clustermap/searchresult-model.ftl  
 A freemarker template that generates search result in CLUSTERMAP XML output.
-->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- imports the macro of this package in namespace "cm" -->
<#import "./macro-lib.ftl" as cm/>
<#assign rootLabel="Mathching bookmarks"/>
<@cm.clustermapDoc>
<#if bookmarks?exists>
  <@cm.genObjectSet bookmarks=bookmarks/>
</#if>
<@cm.classificationSet>
  <@cm.genRootClassification name=rootLabel objectIdList=rootCluster/>  
  <#if (userClusterKey?size > 0)>
    <#assign usersClusterLabel = "Saved by users"/>  
    <#assign usersClusterId = "users"/>
    <@cm.genClassification id=usersClusterId name=usersClusterLabel superClassId="root" objectIdList=rootCluster/>
    <#list userClusterKey as user>
      <#assign clsLabel = user.username/>
      <#assign key = "u"+user.id?c/>
      <#assign objIdList = userClusterMap[key]/>
      <@cm.genClassification id=key name=clsLabel superClassId=usersClusterId objectIdList=objIdList/>
    </#list>
  </#if>
  <#if (tagClusterKey?size > 0 )>
    <#assign tagsClusterLabel = "Tagged"/>
    <#assign tagsClusterId = "tags"/>
    <@cm.genClassification id=tagsClusterId name=tagsClusterLabel superClassId="root" objectIdList=rootCluster/>
    <#list tagClusterKey as tag>
      <#assign key = "t"+tag.id?c/>
      <#if (tag.id == 0)>
        <#assign clsLabel = "(no tags)"/>
      <#else>
        <#assign clsLabel = tag.label?html/>    
      </#if>
      <#assign objIdList = tagClusterMap[key]/>
      <@cm.genClassification id=key name=clsLabel superClassId=tagsClusterId objectIdList=objIdList/>
    </#list>
  </#if>
  
</@cm.classificationSet>
</@cm.clustermapDoc>