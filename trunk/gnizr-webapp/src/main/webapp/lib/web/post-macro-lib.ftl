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
     <a id="${bmarkId}" href="${gzBookmarkUrl(postId?c)}" class="bmark-title link-title" >${postTitle}</a>           
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
<p>

<#if inFolder != "">
<#local user = inFolder.user/>
  <#if tag?exists>
<h4>Explore bookmarks tagged '${tag}':</h4>  
<ul>
<#local mytaggedHref = gzUserBmarkArchivesUrl(user.username,tag)/>
<li><a href="${mytaggedHref}">by ${user.username} in bookmark archive</a></li>
<#local cmmtagHref = gzUrl('/tag/'+tag?url)/>
<li><a href="${cmmtagHref}">by users in the community</a></li>
</ul>    
  </#if>
<h4>Explore bookmarks saved by ${user.username}:</h4>
<ul>
<#local fldrHref = gzUserFolderUrl(user.username,'')/>
<li><a href="${fldrHref}" title="folders">in other folders</a></li>
<#local arcHref = gzUserBmarkArchivesUrl(user.username)/>
<li><a href="${arcHref}" title="bookmark archive">in bookmark archive</a></li>
</ul>
<#else>
  <#if loggedInUser?exists && (loggedInUser.username == username)>
<h4>Try this:</h4>  
<ul>
<li><a href="${gzUrl('/post')}">Add new bookmark</a></li>  
</ul>
  </#if>
</#if>
<h4>Explore the community:</h4>
<ul>
<#local cmmtyHref = gzUrl('/topusers')/>
<li><a href="${cmmtyHref}" title="top users">top users</a></li>
<#local tagsHref = gzUrl('/tags')/>
<li><a href="${tagsHref}" title="popular tags">popular tags</a></li>
</ul>
</p>

</#if>  
</#macro>

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
