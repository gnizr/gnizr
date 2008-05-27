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
    <@ww.submit id="search-submit" cssClass="btn" value="Search"/>    
</@ww.form>

<!-- This DIV is used loading query suggestions -->
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
<li class="first"><img class="icon" src="${gzUrl("/images/home-16.gif")}"></img><a href="${gzUrl("/home")}" class="system-link">home</a></li>
<li><img src="${gzUrl("/images/link-16.gif")}" class="icon"></img><a href="${gzUrl("/for/me")}" title="for you by others" id="bmarkForYou" class="system-link">for you</a></li>
<li><img class="icon" src="${gzUrl("/images/add-16.gif")}"></img><a href="${gzUrl("/post")}"  title="add new bookmark" class="system-link">bookmark</a></li>
</#if>
</ul>
</div>
</#macro>