<#-- 
 clustermap/home.ftl  
 A freemarker template that generates user bookmark list in CLUSTERMAP XML output.
-->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- imports the macro of this package in namespace "cm" -->
<#import "./macro-lib.ftl" as cm/>
<#assign rootLabel="${username}'s bookmarks"/>
<@cm.clustermapDoc>
<#if bookmarks?exists>
  <@cm.genObjectSet bookmarks=bookmarks/>
</#if>
<@cm.classificationSet>
<#if cluster?exists && cluster["root"]?exists>
  <@cm.genRootClassification name=rootLabel objectIdList=cluster["root"]/>  
  <#list tags as ut>
    <#assign aId = ut.id/>
    <#if (aId == 0)>
      <#assign aName = "(no tags)"/>
    <#else>
      <#assign aName = ut.label?html/>
    </#if>    
    <#assign aList = cluster[(ut.id?c)?string]>
    <@cm.genClassification id=ut.id name=aName objectIdList=aList/>
  </#list>
<#else>
  <@cm.genRootClassification name="${username}'s bookmarks" objectIdList=[]/>     
</#if>
</@cm.classificationSet>
</@cm.clustermapDoc>
