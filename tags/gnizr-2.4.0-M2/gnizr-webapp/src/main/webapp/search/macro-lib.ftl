<#macro searchForm namespace="/bookmark">
<div id="search-block"> 
  <p id="search-view-options"></p>
  <@ww.form  action="search" namespace="${namespace}" >
    <@ww.textfield id="search-string" cssClass="long-text-input" name="queryString" value="${queryString?if_exists}"/>
    <@ww.hidden name="type" value="opensearch"/>
    <#--
    <#if loggedInUser?exists>
      <@ww.select name="type" list=r"#{'opensearch':'OpenSearch','user':'My Bookmark Archive','text':'Community'}"/>   
    <#else>
      <@ww.hidden name="type" value="opensearch"/>
    </#if>
    -->
    <@ww.submit id="search-button" cssClass="btn" value="Search"/>    
  </@ww.form>
</div>
</#macro>

<#macro miniSearchForm namespace="/bookmark">
<div id="mini-search-block"> 
  <@ww.form action="search" namespace="${namespace}" theme="css_xhtml">
    <@ww.textfield id="search-string" cssClass="short-text-input" name="queryString" value="${queryString?if_exists}"/>
    <@ww.hidden name="type" value="${type?if_exists}"/>
    <@ww.submit id="search-button" cssClass="btn"  value="search"/>    
  </@ww.form>
</div>
</#macro>