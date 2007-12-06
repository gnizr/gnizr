<#function getActionMsg code>
  <#if code == "NO_SUCH_FEED">
    <#return "No such feed!"/>
  <#elseif code == "NOT_VALID_FEED_URL">
    <#return "This URL doesn't seem to be valid RSS feed, or it's currently inaccessible."/> 
  <#elseif code == "ILLEGAL_FOLDER_NAME">
    <#return "You've specified an illegal folder name."/>
  </#if>
  <#return ""/>
</#function>