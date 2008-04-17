<#setting url_escaping_charset="UTF-8">
<#include "./util-func-lib.ftl"/>
<#include "./post-macro-lib.ftl"/>
<#include "./breadcrumbs-macro-lib.ftl"/>
<#include "./rdf-macro-lib.ftl"/>
<#include "./tagcloud-macro-lib.ftl"/>
<#include "./json-macro-lib.ftl"/>
<#-- 
FUNCTION: gnizrVersion
NOTE: returns a version string to be injected in the <head/> of 
each gnizr page.
-->
<#function gnizrVersion >
  <#return "2.4.0"/>
</#function>

<#function gnizrDevVersion >
  <#return "-M2"/>
</#function>

<#-- 
MACRO: ensureUserLoggedIn
INPUT: [none]
-->
<#macro ensureUserLoggedIn>
<#if loggedInUser?exists >
  <#nested/>
<#elseif hasRememberMeCookie() == true>
  ${response.sendRedirect(gzUrl("/home"))}
<#else>
  ${response.sendRedirect(gzUrl("/login"))}
</#if>
</#macro>

<#-- 
MACRO: goHome
INPUT: [none]
-->
<#macro goHome>
<#if loggedInUser?exists>
  ${response.sendRedirect(gzUrl("/home/"+loggedInUser.username))}
<#elseif hasRememberMeCookie() == true>
  ${response.sendRedirect(gzUrl("/home"))}
<#else>
  <#nested/>
</#if>
</#macro>

<#-- 
===================================================================
MACRO: pageBegin
INPUT: pageTitle:String // the <title/> of this HTML page
       cssHref:Sequence // a list of CSS URL to be linked in <head/>
===================================================================
-->
<#macro pageBegin pageTitle="" cssHref=[] 
				  bodyOnUnload="" bodyOnLoad="" bodyOnResize="" enableJS=true
				  thisPageHref="" thisPageBaseHref="" toPageHref="">
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" 
"http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<!-- leave this for stats -->
<meta name="generator" content="Gnizr ${gnizrVersion()}${gnizrDevVersion()}"></meta>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<title>${pageTitle?html} -- ${getSiteName()}</title>
<link rel="shortcut icon" href="${gzUrl('/images/favicon.ico')}" />

<!-- GNIZR OPENSEARCH DESCRIPTION -->
<link rel="search" type="application/opensearchdescription+xml" href="${gzUrl('/settings/opensearch/description.action')}" title="${getSiteName()}" />

<!-- GNIZR DEFAULT CSS -->
<link href="${gzUrl("/css/gnizr-default.css")}" rel="stylesheet" type="text/css">
<link href="${gzUrl("/css/gnizr-header.css")}" rel="stylesheet" type="text/css">
<link href="${gzUrl("/css/ajax-suggestions.css")}" rel="stylesheet" type="text/css">
<!-- PAGE SPECIFIC CSS BEGINS -->
<#list cssHref as css>
<link href="${css}" rel="stylesheet" type="text/css">
</#list>
<!-- PAGE SPECIFIC CSS ENDS -->

<#if (enableJS == true)>
<!-- JAVASCRIPT DATA BEGINS -->
<script type="text/javascript">MochiKit = {__compat__: true};</script>
<script type="text/javascript" charset="utf-8" src="${gzUrl("/lib/javascript/Mochikit/MochiKit1.4.js")}"></script>
<script type="text/javascript" charset="utf-8" src="${gzUrl("/lib/javascript/tags-util.js")}"></script>
<script type="text/javascript" charset="utf-8" src="${gzUrl("/lib/javascript/common.js")}"></script>
<script type="text/javascript" charset="utf-8" src="${gzUrl("/lib/javascript/cookie-util.js")}"></script>
<script type="text/javascript" charset="utf-8" src="${gzUrl("/lib/javascript/ajaxSuggestions/ajaxSuggestions.js")}"></script>
<#if (gnizrConfiguration.snapShotsKey)?exists>
<script type="text/javascript" src="http://shots.snap.com/snap_shots.js?${gnizrConfiguration.snapShotsKey}"></script>
</#if>
<script type="text/javascript" src="${gzUrl("/lib/javascript/showhide-bmrknotes.js")}"></script>
<#if loggedInUser?exists>
<script type="text/javascript" src="${gzUrl("/lib/javascript/bookmarks-foryou.js")}"></script>
<script type="text/javascript" src="${gzUrl("/lib/javascript/quicksave-bookmark.js")}"></script>
<script type="text/javascript">
  setForYouCountUrl('${gzUrl("/data/json/getForUserCount.action")}');
  setSaveBookmarkUrl('${gzUrl("/data/json/saveBookmark.action")}');
  setFetchBookmarkUrl('${gzUrl("/data/json/getBookmark.action")}');

  // global variable
  var imgUrl = '${gzUrl("/images/")}';
</script>
</#if>
<!-- JAVASCRIPT DATA ENDS -->
</#if>
<#-- this hack is needed to pass on the page context to Action executed within the page -->
<#if (thisPageHref != "")>
  ${session.setAttribute("thisPageHref",thisPageHref)}
<#elseif (Session.thisPageHref)?exists>  
 ${session.setAttribute("thisPageHref",null)}
</#if>
<#if (thisPageBaseHref != "")>
  ${session.setAttribute("thisPageBaseHref",thisPageBaseHref)}
<#elseif (Session.thisPageBaseHref)?exists>  
 ${session.setAttribute("thisPageBaseHref",null)}
</#if>
<#if (toPageHref != "")>
  ${session.setAttribute("toPageHref",toPageHref)}
<#elseif (Session.toPageHref)?exists>  
  ${session.setAttribute("toPageHref",null)}
</#if>
<!-- OTHER DATA BEGINS -->
<#nested/>
<!-- OTHER DATA ENDS -->
</head>
<!-- HTML BODY BEGINS -->
<body
<#if (bodyOnUnload?length > 0) > 
     onunload="${bodyOnUnload}"
</#if>
<#if (bodyOnLoad?length >0)> 
      onload="${bodyOnLoad}" 
</#if>      
<#if (bodyOnResize?length >0)>
      onresize="${bodyOnResize}"
</#if>      
>
</#macro>



<#-- 
===================================================================
MACRO: pageEnd
INPUT: rssHref:String // a feed URL of this page
===================================================================
-->
<#macro pageEnd rssHref="" rdfHref="">
<!-- FOOTER BEGINS -->
<div id="footer">
  <div id="sitemap"> 
    &copy; 2006-2008 by 
    <a href="http://www.imagemattersllc.com" title="Image Matters LLC" class="snap_noshots">Image Matters LLC</a> 
    | <a href="http://gnizr.googlecode.com" title="Gnizr Project Home" class="snap_noshots">Gnizr Project</a>
    <#-- only display RSS href if there is one defined     -->
    <#if (rssHref?length > 0)>
    | <a href="${rssHref?html}"><img src="${gzUrl("/images/rss.gif")}" alt="rss for this page"/></a>
    </#if>
    <#if (rdfHref?length > 0)>
    | <a href="${rdfHref?html}"><img src="${gzUrl("/images/rdf.gif")}" alt="rdf of this page"/></a>
    </#if>
  </div>
</div>

<!-- FOOTER ENDS -->
</body>
<!-- HTML BODY ENDS -->
</html>
</#macro>

<#-- 
===================================================================
MACRO: headerBlock
INPUT: [NONE]
FTL NESTED: YES

if the session variable "loggedInUser" exists, then
display additional links in the "header-l" DVI. Otherwise,
display a login link in the "header-r" DIV. 

The following template will include "foo bar" to be displayed
as <h1/> (page header) of this page:

<@headerBlock>
 foo bar
</@headerBlock>
===================================================================
-->
<#macro headerBlock namespace="" isTagPage=false>
<#if (user.username)?exists>
  <#local username = user.username/>
<#elseif (username?exists)>
  <#local username=username/>  
<#elseif (loggedInUser?exists)>
  <#local username=loggedInUser.username/> 
<#else>
  <#local username=""/>
</#if>
<!-- HEADER BLOCK BEGINS -->
<div id="header2">
  <div id="header2-l">
    <#local logoName = 'gnizr'/>
    <#if (gnizrConfiguration.siteName)?exists> 
      <#local logoName = gnizrConfiguration.siteName/>
    </#if>   
    <h1 class="logo"><a href="${gzUrl("/")}">${logoName}</a></h1>
  </div>
  <div id="header2-r">
    <ul id="app-links">
<#if loggedInUser?exists == false>
    <li class="first"><a href="${gzUrl("/login")}">login</a></li>  
  <#if (gzIsUserRegistrationOpen() == true)>    
    <li><a href="${gzUrl("/register")}">register</a></li>
  </#if>   
<#else>    
    <li class="first">logged in as <a href="${gzUserUrl(loggedInUser.username)}" class="user">${loggedInUser.username}</a></li>     
    <li><a href="${gzUrl("/settings")}">settings</a></li>
    <li><a href="${gzUrl("/logout")}">logout</a></li>     
</#if>
    <li><a href="${gzUrl("/settings/help.action")}">help</a></li>
    </ul>
  </div>
</div>  
<div id="search-box">
<@ww.form action="search" namespace="/bookmark" theme="simple">
  <#local suggestUrl = gzUrl('/search/suggest.action')/>
  <@ww.textfield id="search-input" name="queryString" 
                 value="${queryString?if_exists}" size="40"
                 cssClass="ajax-suggestion url-"+suggestUrl
                />
  <@ww.hidden name="type" value="opensearch"/>
    <@ww.submit id="search-submit" cssClass="" value="Search"/>    
</@ww.form>
<#-- This DIV is used loading query suggestions -->
<div id="ajax-suggestions-container">
	<div id="ajax-suggestions-results" class="invisible">
	</div>
</div>

</div>
<div id="header2-menu">   
   <@ww.action name="menu" namespace="/ui" executeResult=true>    
   </@ww.action>   
</div>
<div id="header2-sub-menu">
<#if loggedInUser?exists>
<ul>
<li class="first"><img class="icon" src="${gzUrl("/images/home-16.gif")}"></img><a href="${gzUrl("/home")}">home</a></li>
<li><img src="${gzUrl("/images/link-16.gif")}" class="icon"></img><a href="${gzUrl("/for/me")}" title="for you by others" id="bmarkForYou">for you</a></li>
<li><img class="icon" src="${gzUrl("/images/add-16.gif")}"></img><a href="${gzUrl("/post")}"  title="add new bookmark">bookmark</a></li>
</#if>
</ul>
</div>
</#macro>

<#-- 
===================================================================
MACRO: infoBlock
INPUT: [NONE]
FTL NESTED: YES

<@infoBlock>
  some info to be display inside the info block
</@infoBlock>
===================================================================
-->
<#macro infoBlock bct=[]>
<div id="infobanner">
<ul id="breadcrumbs"> 
  <#list bct as bctItm>
    <#if (bctItm['url']?length > 0)>
      <#local url = bctItm['url']/>
    <#else>
      <#local url = ''/>
    </#if>
    <#local name = bctItm['name']/>
    <li><#if url?exists && (url?length>0)><a href="${url}">${name}</a><#else>${name}</#if>
    <#if bctItm_has_next><img src="${gzUrl("/images/arrow.gif")}"></img></#if>
    </li>
  </#list>
</ul>  
</div>
</#macro>

<#-- 
===================================================================
MACRO: pageContent
===================================================================
-->
<#macro pageContent>
<div id="header2-menu-follow">
<div id="contents">
<#nested>
</div>
</div>
</#macro>

<#macro pageContentLite>
<#nested>
</body>
</html>
</#macro>

<#macro pageTitle>
<h2><#nested/></h2>
</#macro>

<#macro pageDescription>
<div class="instruction"><#nested/></div>
</#macro>

<#macro formInput id="">
<div class="forminput" 
 <#if id != "">
   id="${id}"
 </#if>
 ><#nested/></div>
</#macro>

<#-- 
===================================================================
MACRO: mainBlock
===================================================================
-->
<#macro mainBlock>

<!-- MAIN DIV BEGINS -->
<div id="main">
<#nested/>
</div>
<!-- MAIN DIV ENDS -->

</#macro>

<#-- 
===================================================================
MACRO: sidebarBlock
===================================================================
-->
<#macro sidebarBlock cssClass="">
<!-- SIDEBAR DIV BEGINS -->
<div id="sidebar" class="${cssClass}">
<#nested/>
</div> 
<!-- SIDEBAR DIV ENDS -->
</#macro>

<#macro sidebarElement>
<div class="element">
  <#nested/>
</div>
</#macro>

<#-- 
===================================================================
MACRO: pagerControl
INPUT: pageHref:String // the URL of the calling page
	   pageNum:integer //the currrent page number
       totalPageNum:integer // total number of pages available
       prevLabel: string // href label for the paging previous link
       nextLabel: string // href label for the paging next link
===================================================================
-->
<#macro pagerControl pageHref="" pageNum=0 totalPageNum=0 prevLabel="previous" nextLabel="next">
<#if pageHref?matches('.*\\?.*')>
  <#local prevUrl=pageHref+"&page="+(pageNum-1)/>
  <#local nextUrl=pageHref+"&page="+(pageNum+1)/>
<#else>
  <#local prevUrl=pageHref+"?page="+(pageNum-1)/>
  <#local nextUrl=pageHref+"?page="+(pageNum+1)/>
</#if>
<div class="pager">
<p>
&laquo;&nbsp;
<#if ((pageNum-1) > 0)>
<a href="${prevUrl}">${prevLabel}</a> 
<#else>
${prevLabel}
</#if>
| 
<#if ((pageNum+1) <= totalPageNum)>
<a href="${nextUrl}">${nextLabel}</a>
<#else>
${nextLabel}
</#if>
&nbsp;&raquo; 
<#if (pageNum > 0) && (totalPageNum > 0)>
<span class="pagenumber">page ${pageNum} of ${totalPageNum}</span>
</#if>
</p>
<#nested/>
</div> 
</#macro>

<#macro pageActionControl>
  <div class="pageActionCtrl">
    <#nested/>
  </div>
</#macro>

<#--
===================================================================
MACRO: viewInTimeRangeControl
INPUT: pageHref:String // the URL of the calling page
       timeRange: String // preset timeRange string
===================================================================
-->
<#macro viewInTimeRangeControl pageHref >
<@ww.form cssClass="timeRangeCtrl" action=pageHref method="post" >	
<#assign opts = r"#{'today':'today','yesterday':'yesterday',
                 'last7days':'last 7 days',
                 'thismonth':'this month','lastmonth':'last month','all':'all available'}"/>
<@ww.select label="view" 
            name="timeRange" 
            list=opts 
            cssClass="normal-text"            
            onchange="this.form.submit()"/>
</@ww.form>
</#macro>


<#-- 
===================================================================
MACRO: viewConfigBlock
INPUT: pageHref:String // the URL of the calling page

NOTE: the calling page must be a WebWork action that
support bean property "perPageCount" (i.e., implements 
getPerPageCount() and setPerPageCount(int))
===================================================================
-->
<#macro viewConfigBlock pageHref="">
<!-- VIEW CONFIG DIV BEINGS -->
<#local spc = '?'/>
<#if pageHref?matches('.*\\?.*')>
 <#local spc = '&'/>
</#if>
<#local paging5 = pageHref+spc+"perPageCount=5"/>
<#local paging10 = pageHref+spc+"perPageCount=10"/>
<#local paging25 = pageHref+spc+"perPageCount=25"/>
<#local paging50 = pageHref+spc+"perPageCount=50"/>
<#local paging100 = pageHref+spc+"perPageCount=100"/>
<div class="viewconfig">
show 
<#if (pageCount != 5)><a href="${paging5}">5</a><#else>5</#if>, 
<#if (pageCount != 10)><a href="${paging10}">10</a><#else>10</#if>, 
<#if (pageCount != 25)><a href="${paging25}">25</a><#else>25</#if>, 
<#if (pageCount != 50)><a href="${paging50}">50</a><#else>50</#if>, 
<#if (pageCount !=100)><a href="${paging100}">100</a><#else>100</#if> items per page
</div>
<!-- VIEW CONFIG DIV ENDS -->
</#macro>


<#-- NOTHING HERE -->
<#macro nothingHereMainBlock>
  <@mainBlock>
  <ul>
    <li>No bookmarks here!</li>
  </ul>
  </@mainBlock>
</#macro>

<#macro bookmarkActionControl>
  <div id="bookmark-action" class="bookmark-action-container">
    <div id="checkboxAction" class="actions-left">
      <a id="checkboxAction_selectAll" class="system-link">select all</a> | 
      <a id="checkboxAction_unselectAll" class="system-link">unselect all</a>
    </div>
    <div class="actions-right">   
    <button id="action_delete" class="invisible" type="button" name="delete">delete</button>
    <@ww.select id="selectAction" label="action" name="op" 
                list=r"#{'':'---- select action ----'}"/>      
    </div>                
  </div>
</#macro>
