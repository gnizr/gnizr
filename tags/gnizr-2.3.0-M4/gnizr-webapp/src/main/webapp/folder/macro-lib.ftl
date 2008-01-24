<#include "/lib/web/util-func-lib.ftl"/>
<#include "/lib/web/tagcloud-macro-lib.ftl"/>

<#function setSystemFoldersFirst folders>
  <#local resultList = setMyRSSImportedFoldersFirst(folders)/>
  <#local resultList = setMyBookmarksFolderFirst(resultList)/>
  <#return resultList/>
</#function>

<#function setMyBookmarksFolderFirst folders>
  <#local resultList = []/>
  <#list folders as fd>
    <#if gzIsMyBookmarksFolder(fd.name) == true>
      <#local resultList = [fd] + resultList/>       
    <#else>
      <#local resultList = resultList + [fd]/>      
    </#if>
  </#list>
  <#return resultList/>
</#function>

<#function setMyRSSImportedFoldersFirst folders>
  <#local resultList = []/>
  <#list folders as fd>
    <#if gzIsMyRSSImportedFolder(fd.name) == true>
      <#local resultList = [fd] + resultList/>       
    <#else>
      <#local resultList = resultList + [fd]/>      
    </#if>
  </#list>
  <#return resultList/>
</#function>