<#macro tagCloudList tagCloudTitle="tags" tagLabels=[] tagHrefPrefix="">
<ul class="menu">
<!-- TAG CLOUD BEGINS -->
<li>
<span class="title">${tagCloudTitle}</span>:&nbsp;
<#if tagView?exists && (tagView == "list")>
<ul class="taglist">
<#else/>
<ul class="tagcloud">
</#if>
<#local tagCloudHash = computeTagCloud(tagLabels)/>
<#list tagLabels as ut>  
  <#local tag = ut.label/>
  <#local count = ut.count/>
  <#local classStyle = getTagPopularityStyle(tagCloudHash[tag])/>
   
  <#if (count > 1)>
    <#local linkTitle=count+" posts"/>
  <#else>
    <#local linkTitle=count+" post"/>
  </#if>
   
  <#local tagHref = ""/>    
  <#if (tagHrefPrefix != "")>
    <#local tagHref=tagHrefPrefix+"/tag/"+tag?url/>
  <#else>
    <#local tagHref="tag/"+tag?url/>    
  </#if>
  <li><a href="${tagHref}" title="${linkTitle}" class="${classStyle?if_exists}">${tag}</a>
  <#local tuCls = "invisible">
  <#if tagView?exists>
    <#if tagView == "list">
     <#local tuCls = ""/>
    </#if>
  </#if>  
  <span class="${tuCls} light-comment">(${count})</span>
  </li>
</#list>
<#if tagLabels?has_content == false>
  <li>(no tags)</li>
</#if>
</ul>
</li>
<!-- TAG CLOUD ENDS -->
</ul>
</#macro>

<#--
MACRO: tagGroupsList
-->
<#macro tagGroupsList tagGroups={} tagHrefPrefix="">
  <#local tagGroupNames = tagGroups?keys/>
  <#list tagGroupNames as tgGrp>
    <#if (tgGrp == "")>
      <#local tcTitle = "(default)"/>
    <#else>
      <#local tcTitle = tgGrp/>
    </#if>
    <@tagCloudList tagCloudTitle=tcTitle tagLabels=tagGroups[tgGrp] tagHrefPrefix=tagHrefPrefix/>
  </#list>
</#macro>


<#--
MACRO: skosTagPhrase
-->
<#macro skosTagPhrase related=[] narrower=[] broader=[] tag="">
<#if (related?size > 0) || (narrower?size >0) || (broader?size > 0) 
     || (loggedInUser?exists && loggedInUser.username=username)>
<ul class="menu">
<li><span class="title">${tag}'s tag relations</span>:
<#if loggedInUser?exists && loggedInUser.username=username>
<a href="${gzUrl("/settings/tags/edit.action?tag="+tag?url)}" title="edit tag relations: ${tag}">edit</a>
</#if>
<ul class="submenu">
<#if (narrower?size > 0)>
  <li>narrower:
  <#local cnt = 0/>
  <#list narrower as nt>
    <#local tagLabel = mapUserTagLabel(nt,username)/>
    <#local tagUser = nt.user.username/>
    <#local tagHref=gzUserBmarkArchivesUrl(tagUser,nt.tag.label)/>  
    <a href="${tagHref}" title="${tag} has a narrower concept: ${tagLabel}" class="tag">${tagLabel}</a>
  </#list>   
  </li>  
</#if>

<#if (related?size > 0)>
  <li>related:
  <#local cnt = 0/>
  <#list related as rt>
    <#local tagLabel = mapUserTagLabel(rt,username)/>
    <#local tagUser = rt.user.username/>
    <#local tagHref=gzUserBmarkArchivesUrl(tagUser,rt.tag.label)/>  
    <a href="${tagHref}" title="${tag} has a related concept: ${tagLabel}" class="tag">${tagLabel}</a>
  </#list>
  </li>
</#if>

<#if (broader?size > 0)>
  <li>broader:
  <#local cnt = 0/>
  <#list broader as bt>
    <#local tagLabel = mapUserTagLabel(bt,username)/>
    <#local tagUser = bt.user.username/>
    <#local tagHref = gzUserBmarkArchivesUrl(tagUser,bt.tag.label)/>   
    <a href="${tagHref}" title="${tag} has a broader concept: ${tagLabel}" class="tag">${tagLabel}</a>
  </#list>
  </li>
</#if>
</ul>
</li>
</ul>
</#if>
</#macro>


<#-- 
MACRO: tagCloudOptions
INPUT: pageHref: the URL to this page
-->
<#macro tagCloudOptions redirectUrl minTagFreq sortBy tagView pageHref="">
<ul class="menu">
  <li>
  <span class="title">tag cloud options</span>:
  <ul class="submenu">  
<#local minOneUrl = gzUrl("/bookmark/configTagCloud.action?minTagFreq=1&redirectToPage="+redirectUrl?url)/>  
<#local minTwoUrl = gzUrl("/bookmark/configTagCloud.action?minTagFreq=2&redirectToPage="+redirectUrl?url)/> 
<#local minFiveUrl = gzUrl("/bookmark/configTagCloud.action?minTagFreq=5&redirectToPage="+redirectUrl?url)/> 
    <li>use minimum: 
<#-- hide the "minXXXurl" link that has been currently selected -->
<#if (minTagFreq != 1)><a href="${minOneUrl}">1</a><#else>1</#if>,
<#if (minTagFreq != 2)><a href="${minTwoUrl}">2</a><#else>2</#if>,
<#if (minTagFreq != 5)><a href="${minFiveUrl}">5</a><#else>5</#if>
    </li>

    <li>sort by: 
<#local sortByAlphaUrl = gzUrl("/bookmark/configTagCloud.action?sortBy=alpha&redirectToPage="+redirectUrl?url)/> 
<#local sortByFreqUrl = gzUrl("/bookmark/configTagCloud.action?sortBy=freq&redirectToPage="+redirectUrl?url)/>      
<#if (sortBy != "alpha")><a href=${sortByAlphaUrl} title="sort tags alphabetically">alpha</a><#else>alpha</#if> | 
<#if (sortBy != "freq")><a href=${sortByFreqUrl} title="sort tags based on usage frequency">freq</a><#else>freq</#if>
    </li>

    <li>view as: 
<#local tagCloudViewUrl = gzUrl("/bookmark/configTagCloud.action?tagView=cloud&redirectToPage="+redirectUrl?url)/> 
<#local tagListViewUrl = gzUrl("/bookmark/configTagCloud.action?tagView=list&redirectToPage="+redirectUrl?url)/> 
<#if (tagView != "cloud")><a href=${tagCloudViewUrl} title="view tag cloud">cloud</a><#else>cloud</#if> |
<#if (tagView != "list")><a href=${tagListViewUrl} title="view tag list">list</a><#else>list</#if>
    </li> 
    <li>tag groups: 
<#local showTgGrpsUrl = gzUrl("/bookmark/configTagCloud.action?hideTagGroups=false&redirectToPage="+redirectUrl?url)/>
<#local hideTgGrpsUrl = gzUrl("/bookmark/configTagCloud.action?hideTagGroups=true&redirectToPage="+redirectUrl?url)/>
<#if (hideTagGroups == true)><a href="${showTgGrpsUrl}" title="show tag groups">show</a><#else>show</#if> |
<#if (hideTagGroups == false)><a href="${hideTgGrpsUrl}" title="hide tag groups">hide</a><#else>hide</#if>
    </li>
  </ul>
  </li>
</ul>
</#macro>

<#-- 
FUNCTION: getClassTagCssStyle
INPUT: classTag: a hash of UserTag object
RETURN: a CSS style class name
-->
<#function getClassTagCssStyle classTag={}>
  <#if emUserTag?exists && (classTag.id)?exists>  
    <#if (emUserTag = classTag.id)>
     <#return "emTagInClass"/>
    </#if>
  </#if> 
  <#return ""/>
</#function>

<#-- 
FUNCTION: isInUserTagList
INPUT: userTagList : a sequence of UserTag objects
       aUserTag : a UserTag object
RETURN: true if aUserTag.id equals to the id of one of the UserTag
        in userTagList. Otherwise, returns false.
-->
<#function isInUserTagList userTagList aUserTag>
  <#list userTagList as ut>
    <#if (ut.id = aUserTag.id)>
      <#return true/>
    </#if>
  </#list>
  <#return false/>
</#function>

<#--
FUNCTION: mapUserTagLabel 
-->
<#function mapUserTagLabel userTag curUsername>
  <#if userTag.user.username != curUsername>
    <#return userTag.user.username+"/"+userTag.tag.label/>    
  <#else>
    <#return userTag.tag.label/>
  </#if>
</#function>