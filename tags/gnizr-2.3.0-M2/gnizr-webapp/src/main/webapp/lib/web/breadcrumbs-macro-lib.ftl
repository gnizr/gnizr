<#setting url_escaping_charset="UTF-8">

<#-- 
FUNCTION: gzBCTPair
-->
<#function gzBCTPair name url>
 <#return {'name':name,'url':url}/>
</#function>

<#function settingsBCT username>
  <#return [gzBCTPair(username,gzUserUrl(username)),
            gzBCTPair('settings',gzUrl('/settings'))]/>
</#function>

<#function communityBCT>
  <#return [gzBCTPair('community',gzUrl('/topusers'))]/>
</#function>

<#function userhomeBCT username>
  <#return [gzBCTPair(username,gzUserUrl(username))]>
</#function>

<#function userArchiveBCT username>
  <#return [gzBCTPair(username,gzUserUrl(username)),
            gzBCTPair('bookmark archive',gzUserBmarkArchivesUrl(username))]>
</#function>

<#function searchBCT>
  <#return [gzBCTPair('search',gzUrl('/search'))]>
</#function>