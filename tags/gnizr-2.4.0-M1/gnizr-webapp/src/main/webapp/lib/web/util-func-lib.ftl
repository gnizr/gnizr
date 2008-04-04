<#setting url_escaping_charset="UTF-8">

<#-- 
FUNCTION: getSiteName
-->
<#function getSiteName>
  <#local siteName = 'gnizr'/>
  <#if (gnizrConfiguration.siteName)?exists>
    <#return gnizrConfiguration.siteName/>
  </#if>
  <#return siteName/>
</#function>

<#-- 
FUNCTION: getSiteDescription
-->
<#function getSiteDescription>
  <#local siteDsp = 'organize'/>
  <#if (gnizrConfiguration.siteDescription)?exists>
    <#return gnizrConfiguration.siteDescription/>
  </#if>
  <#return siteDsp/>
</#function>

<#--
FUNCTION: hasRememberMeCookie
-->
<#function hasRememberMeCookie>
  <#if (request.cookies)?exists >
    <#list request.cookies as c>
      <#if c.name == 'rememberMe'>
       <#return true/>
      </#if>
    </#list>
  </#if>
  <#return false/>
</#function>

<#-- 
===================================================================
FUNCTION: gzUrl
INPUT: url:String 

Adds a prefix string to the input URL. This prefix is always equals
to ${request.contextPath}
===================================================================
-->
<#function gzUrl url>
<#--
  <#return (request.contextPath+url)?html/>
-->
  <#return gzFullUrl(url)/>  
</#function>


<#function gzFullUrl url>
  <#if (gnizrConfiguration.webApplicationUrl)?exists>
    <#return (gnizrConfiguration.webApplicationUrl+url)/>
  <#else>
    <#return "http://"+request.serverName+":"+request.serverPort?string("0.######")+request.contextPath+url/>
  </#if>
</#function>


<#function gzIsUserRegistrationOpen>
  <#if (gnizrConfiguration.registrationPolicy)?exists>
    <#local policy = gnizrConfiguration.registrationPolicy/>
    <#if policy?matches("open","i")>      
      <#return true/>
    </#if>
  </#if>
    <#return false/>
</#function>

<#-- 
===================================================================
FUNCTION: gzCss
INPUT: url:String 

Adds a prefix string to the input CSS URL. This prefix is always equals
to ${request.contextPath} + "/css/" + url
===================================================================
-->
<#function gzCss url>
  <#return (request.contextPath+"/webwork/css/"+url)/>
</#function>

<#--
===================================================================
FUNCTION: gzUserUrl
INPUT: username 

Encodes a RESTful url representation of a user's home page
===================================================================
-->
<#function gzUserUrl username>
  <#return gzUrl("/user/"+username)/>
</#function>

<#function gzUserBmarkArchivesUrl username tag="">
  <#if (tag != "")>
    <#return gzUrl("/user/"+username+"/bookmark/archive/tag/"+tag?url)>
  <#else>
    <#return gzUrl("/user/"+username+"/bookmark/archive")/>
  </#if>
</#function>

<#function gzUserImportedFolderUrl username>
  <#return gzUserFolderUrl(username,"_import_")/>
</#function>
<#--
===================================================================
FUNCTION: gzUserFolderUrl
INPUT: username, folderName, tag [optional]

Encodes a RESTful url representation of a user's folder
===================================================================
-->
<#function gzUserFolderUrl username folderName tag="">
 <#if tag != "">
   <#return gzUrl("/user/"+username+"/folder/"+folderName+"/tag/"+tag?url)/>
 <#else>
   <#return gzUrl("/user/"+username+"/folder/"+folderName)/>
 </#if>
</#function>

<#function gzFormatFolderName folderName>
  <#if gzIsMyBookmarksFolder(folderName)>
    <#return "My Bookmarks"/>
  <#elseif gzIsMyRSSImportedFolder(folderName)>
    <#return "My RSS Imported"/>
  <#else>
    <#return folderName/>
  </#if>
</#function>

<#function gzIsMyBookmarksFolder folderName>
   <#if folderName == "_my_">
     <#return true/>
   </#if>
   <#return false/>
</#function>

<#function gzIsMyRSSImportedFolder folderName>
  <#if folderName == "_import_">
    <#return true/>
  </#if>
  <#return false/>
</#function>


<#--
===================================================================
FUNCTION: gzUserFeedUrl
INPUT: username, feedUrl

Encodes a RESTful url representation of a user's folder
===================================================================
-->
<#function gzUserFeedUrl username feedUrl>
  <#return gzUrl("/user/"+username+"/feed/"+feedUrl)/>
</#function>

<#--
===================================================================
FUNCTION: gzUserTagUrl
INPUT: username (required)
       tag (required)

Encodes a RESTful url representation of a user's tag home page
===================================================================
-->
<#function gzUserTagUrl username tag>
  <#if tag?contains('&') || tag?contains('+')>
   <#local tagHref = gzUrl("/bookmark/viewUserPage.action?username="+username+"&tag="+tag?url)>
   <#return tagHref/>
  <#else>
    <#return gzUrl("/user/"+username+"/tag/"+tag?url)/>
  </#if>
</#function>

<#--
===================================================================
FUNCTION: gzTagUrl
INPUT: tag (optional)

Encodes a RESTful url representation of a tag page
===================================================================
-->
<#function gzTagUrl tag="">  
  <#if tag?contains('&') || tag?contains('+')>
   <#local tagHref = gzUrl("/community/viewTaggedLink.action?tag="+tag?url)>
   <#return tagHref/>
  <#else>
    <#return gzUrl("/tag/"+tag?url)/>
  </#if>
</#function>

<#--
===================================================================
FUNCTION: gzLinkUrl
INPUT: linkUrlHash (required)

Encodes a RESTful url representation of a link page
===================================================================
-->
<#function gzLinkUrl linkUrlHash>
  <#return gzUrl("/url/"+linkUrlHash)/>
</#function>

<#function gzBookmarkUrl bookmarkId>
  <#return gzUrl("/bookmark/id/"+bookmarkId)/>
</#function>

<#--
===================================================================
FUNCTION: gzJsUrl
INPUT: jsFile (required)

Encodes a RESTful url representation of a gnizr JavaScript page
===================================================================
-->
<#function gzJsUrl jsFile>
  <#return gzUrl("/webwork/javascripts/"+jsFile)/>
</#function>

<#--
===================================================================
FUNCTION: gzGeoRssUrl
INPUT: geoRssFile (required)

Encodes a RESTful url representation of a gnizr GeoRSS page
===================================================================
-->
<#function gzGeoRssUrl geoRssFile>
  <#return gzFullUrl("/georss/"+geoRssFile)/> 
</#function>

<#--
===================================================================
FUNCTION: getTimelineDateTime
INPUT: date (required) : a Java Date object
Returns a date/time string in a format represented that is defined
by the SIMILE Timeline API
===================================================================
-->
<#function getTimelineDateTime date>
  <#return date?string("EEE MMM dd yyyy hh:mm:ss zzz")/>
</#function>

<#-- 
===================================================================
FUNCTION: fhtml
INPUT: htmlString (optional): a string 
Return a string that is encoded safe for HTML display
===================================================================
-->
<#function fhtml htmlString>
  <#return htmlString?html/>
</#function>

<#--
===================================================================
FUNCTION: getDateTimeISO8601
INPUT: date (required) : a Java Date object
Returns a date/time string in a format represented that is defined
by ISO 8601
===================================================================
-->
<#function getDateTimeISO8601 date>
 <#return date?string("yyyy-MM-dd'T'HH:mm:ss.SSSZ")/>
</#function>

<#--
===================================================================
FUNCTION: mapToYesNo
INPUT: a boolean value
OUTPUT: returns 'yes', 'no' that maps 'true', 'false' respectively
===================================================================
-->
<#function mapToYesNo value>
  <#if value == true>
    <#return 'yes'/>
  <#else>
    <#return 'no'/>
  </#if>
</#function>

<#function prettyFormatUrl url breakCnt=45>
  <#if (url?length > breakCnt)> 
    <#local ppUrl =""/>
    <#list 0..(url?length-1) as i>    
     <#local ppUrl=ppUrl+url[i]/>
     <#if ((i % breakCnt) == 0)>
       <#local ppUrl=ppUrl+"<wbr>"/>
     </#if>
    </#list>
    <#return ppUrl/>
  <#else>
    <#return url/>
  </#if>
</#function>

<#function isUserAuth loggedInUser user>
 <#if loggedInUser?exists && (user.username == loggedInUser.username)>
   <#return true/>
 </#if>
 <#return false/>
</#function>

<#function getTagPopularityStyle tagClsId=0>  
   <#if (tagClsId = 1)>
     <#return 'not-popular'/>
   <#elseif (tagClsId = 2)>
     <#return 'not-very-popular'/>
   <#elseif (tagClsId = 3)>
     <#return 'somewhat-popular'/>
   <#elseif (tagClsId = 4)>
     <#return 'popular'/>
   <#elseif (tagClsId = 5)>
     <#return 'very-popular'/>
   <#elseif (tagClsId = 6)>
     <#return 'ultra-popular'/>
   </#if>   
   <#return ''/>
</#function>

<#function emphasizeText textBlob emText>
 <#local resultText =""/>
 <#local words = textBlob?split(" ","rim")/>   
 <#list words as aWord >    
   <#if hasMatch(aWord,emText)>    
      <#local resultText = resultText + aWord?replace(aWord?trim,'<span class="matched_text">'+aWord?trim+'</span> ')/> 
   <#else>     
     <#local resultText = resultText + aWord+" "/>
   </#if> 
 </#list>
 <#return resultText/>
</#function>

<#function hasMatch s emText>
 <#list emText as em>
   <#local toMatch = em/>
   <#if s?trim?matches(em,'i')>
     <#return true/>
   </#if>
 </#list>
 <#return false/>
</#function>

<#function getToPageHref url>
<#local toUrl = url/>
<#if page?exists>
  <#if url?matches('.*\\?.*')>
    <#local toUrl = url+"&page="+page?c/>  
  <#else>
    <#local toUrl = url+"?page="+page?c/>       
  </#if>
</#if>
<#return toUrl/>
</#function>

<#function googleMapKeyUrl key>
  <#return "http://maps.google.com/maps?file=api&amp;v=2&amp;key="+key/>
</#function>
