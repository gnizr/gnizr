<#--
MACRO: clustermapDoc
-->
<#macro clustermapDoc>
<?xml version="1.0" encoding="UTF-8"?>
<ClassificationTree version="1.0">
<#nested/>
</ClassificationTree>
</#macro>

<#--
MACRO: genObjectSet
INPUT: bookmarks : a sequence of Bookmark objects
generates XML that describes the <ObjectSet/> section of
a Clustermap data model document
-->
<#macro genObjectSet bookmarks>
<ObjectSet>
<#list bookmarks as bm>
  <Object ID="${bm.id}">
    <Name>${bm.title?html}</Name>
    <Location>${bm.link.url?html}</Location>
  </Object>
</#list>
</ObjectSet>
</#macro>

<#--
MACRO: classificationSet
-->
<#macro classificationSet>
<ClassificationSet>
<#nested>
</ClassificationSet>
</#macro>

<#--
MACRO: genRootClassification 
INPUT: rootId: the id name of the ROOT classification (default: "root")
       name : the name (or lable) used to define <Classification>/<Name>
       objectIdList: a sequence of Integer that represents object id
-->
<#macro genRootClassification name objectIdList rootId="root">
   <Classification ID="${rootId}">
      <Name>${name}</Name>
      <Objects objectIDs="
      <#list objectIdList as id>
       ${id}
      </#list>
      "/>
    </Classification>
</#macro>

<#macro genClassification id name objectIdList superClassId="root">
  <Classification ID="${id}">
    <Name>${name}</Name>
    <Objects objectIDs="
    <#list objectIdList as objId>
      ${objId}
    </#list>
    "/>
    <SuperClass refs="${superClassId}"/>
  </Classification>
</#macro>