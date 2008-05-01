<#setting url_escaping_charset="UTF-8">
<#-- 
MACRO: postItem
INPUT: postUrl
       postTitle 
       postNotes
       postTags
       postCreatedOn
       postUser     
       postLink     
-->
<#macro postItem postId postUrl postTitle postNotes 
        postUser postTags postCreatedOn postLink postInFolders=[] emText=[] postMachineTags=[]>
<#if (postId > 0)>
  <#if folderName?exists && loggedInUser?exists> 
    <#if (loggedInUser.username==folder.user.username)>
      <#local rmbmUrl=gzUrl("/rmbmark?url="+postUrl?url+"&folderName="+folderName
                          +"&owner="+postUser.username+"&redirectToPage="+(toPageHref?if_exists)?url)/>   
    </#if>                          
  </#if> 
  <#local delbmUrl=gzUrl("/delpost?url="+postUrl?url+"&redirectToPage="+(toPageHref?if_exists)?url) />     
  <#local edtbmUrl=gzUrl("/edtpost?url="+postUrl?url+"&redirectToPage="+(toPageHref?if_exists)?url) />
  <#local arcbmUrl=gzUrl("/arcpost?url="+postUrl?url) />
  <#local bmarkId="bmark_"+postId?c />
</#if>
<#local savbmUrl=gzUrl("/post?url="+postUrl?url+"&title="+postTitle?url) />        
<li id="${'p_'+bmarkId}" class="post">   

<div class="post-container">  
 <#if loggedInUser?exists> 
    <#if (loggedInUser.username == postUser.username) && (postId > 0)>  
<div class="post-select">
<@ww.checkbox cssClass="selectBookmark " name="bookmarkId" id="c_"+bmarkId fieldValue=postId?c/>  
</div>
	</#if>
</#if>
<div class="post-title">     
     <a id="${bmarkId}" href="${gzBookmarkUrl(postId?c)}" class="bmark-title link-title" >${cleanTitle(postTitle)}</a>           
     <@iconLabels mTags=postMachineTags user=postUser.username/> 
     <div class="post-link"><a href="${postUrl}" class="bmark-link web-link" target="_blank">${prettyFormatUrl(postUrl)}</a></div>   
</div>    

  <div class="post-actions">  
  <#nested/>
  <#if loggedInUser?exists> 
    <#if (loggedInUser.username == postUser.username) && (postId > 0)>  
      <a href="${edtbmUrl}" title="edit" class="system-link edit-bmark-link">edit</a>    
    <#else>    
      <a href="${savbmUrl}" title="save" class="system-link save-bmark-link">save</a>
    </#if>   
  </#if>  
  </div>   
</div>
   
    <#assign notesGrp = sliceNotes(postNotes)/>
    <div class="notes">
      <div class="shrtNotes">${notesGrp[0]}</div>
    </div>
    <div class="meta">
    <div class="info">by <a href="${gzUserUrl(postUser.username)}">${postUser.username}</a> 
  <#if (postTags?size > 0) >
    tagged 
  <#list postTags as tag>    
    <a class="tag" href="${gzUserBmarkArchivesUrl(postUser.username,tag)}">${tag}</a><#if tag_has_next>, </#if>
  </#list>
  ::
  </#if>
  <#if (postInFolders?size > 0)>
   in folder 
    <#list postInFolders as fldr>
    <#local fldrHref = gzUserFolderUrl(postUser.username,fldr)/>
    <#local fname = gzFormatFolderName(fldr)/>
    <a class="folder f_${bmarkId}" href="${fldrHref}" title="folder: ${fname}">${fname}</a><#if fldr_has_next>, </#if>
    </#list>
   ::
  </#if>
  saved by <a href="${gzBookmarkUrl(postId?c)}">${postLink.count}
  <#if (postLink.count > 1)>people <#else> person</#if></a>
   :: ${postCreatedOn?string("EEE, MMM d, ''yy")}
   </div>
   </div>
  </li>      
</#macro>

<#macro checkBookmarkExists bookmarks inFolder="">
<#if (bookmarks?size > 0)>
  <#nested/>
<#else>
<h3>No saved bookmarks here!</h3>
<div class="short-infobox">
<#if inFolder != "">
  <#if (loggedInUser.username)?exists>
    <@whatCanIDo loggedInUser=loggedInUser user=inFolder.user/>      
  </#if>   
</#if>
<@whatCanAnyoneDo/>
</div>
</#if>  
</#macro>

<#macro whatCanIDo loggedInUser user>
<#-- if there is a loggedInUser and the user is viewing his/her own page -->
<#if (isUserAuth(loggedInUser,user) == true)>
<h3>Get Started!</h3>  
<ul>
<li><a href="${gzUrl('/post')}">Add new bookmark</a></li> 
<li><a href="${gzUrl('/settings/folders/create!doInput.action')}">Create new folder</a></li>
<li><a href="${gzUrl('/settings/feeds/create!doInput.action')}">Subscribe to RSS feed</a></li>
<li><a href="${gzUrl("/settings/help.action")}">Get browser tools</a></li>
</ul>
</#if>
</#macro>

<#macro whatCanAnyoneDo>
<h3>You can explore the community...</h3>
<ul>
<#local cmmtyHref = gzUrl('/topusers')/>
<li><a href="${cmmtyHref}" title="top users">Top users</a></li>
<#local tagsHref = gzUrl('/tags')/>
<li><a href="${tagsHref}" title="popular tags">Popular tags</a></li>
</ul>
</#macro>

<#function cleanTitle title>
	<#local s = title?replace("</?[a-z]+[^>]*>"," ","irm")/>
	<#return s/>
</#function>

<#function sliceNotes notes>
  <#local nts = notes/> 
  <#local s = notes?replace("</?[a-z]+[^>]*>"," ","irm")/>
  <#if (s?length > 100) || (notes?matches(".*<.*>.*"))>
    <#local shrNotes = ""/>   
    <#local res = s?matches("([\\S]+)","rm")/>
    <#list res as t>     
      <#if (t_index < 15) == true>
        <#local shrNotes = shrNotes + t?groups[1] + " "/> 
      <#else>
        <#break/>
      </#if>    
    </#list>
    <#return [shrNotes+"...",nts]/>
  <#else>
    <#return [nts]/>    
  </#if>
</#function>

<#macro iconLabels mTags user>
  <#list mTags as mt>
   <#if mt.predicate == 'icon'>
      <#if mt.value == 'video'>
        <#local url = gzUserBmarkArchivesUrl(user,"icon:video")/>
        <a href="${url}"><img class="icon" src="${gzUrl('/images/video-icon-16.png')}" alt="icon:video"/></a>
      <#elseif mt.value == 'audio'>
        <#local url = gzUserBmarkArchivesUrl(user,"icon:audio")/>
        <a href="${url}"><img class="icon" src="${gzUrl('/images/audio-icon-16.png')}" alt="icon:audio"/></a>
      <#elseif mt.value == 'photo'>
        <#local url = gzUserBmarkArchivesUrl(user,"icon:photo")/>
        <a href="${url}"><img class="icon" src="${gzUrl('/images/photo-icon-16.png')}" alt="icon:photo"/></a>
      <#elseif mt.value == 'heart'>
        <#local url = gzUserBmarkArchivesUrl(user,"icon:heart")/>
        <a href="${url}"><img class="icon" src="${gzUrl('/images/heart-icon-16.png')}" alt="icon:heart"/></a>
      <#elseif mt.value == 'star'>
        <#local url = gzUserBmarkArchivesUrl(user,"icon:star")/>
        <a href="${url}"><img class="icon" src="${gzUrl('/images/star-icon-16.png')}" alt="icon:star"/></a>
      <#elseif mt.value == 'db'>
        <#local url = gzUserBmarkArchivesUrl(user,"icon:db")/>
        <a href="${url}"><img class="icon" src="${gzUrl('/images/db-icon-16.png')}" alt="icon:db"/></a>
      <#elseif mt.value == '!'>
        <#local url = gzUserBmarkArchivesUrl(user,"icon:!")/>
        <a href="${url}"><img class="icon" src="${gzUrl('/images/exclaim-icon-16.png')}" alt="icon:!"/></a>
      </#if>
   </#if>
  </#list>
</#macro>
