<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@sidebarElement>    
   <#if senders?exists && (senders?size > 0)>
   suggested by:
   <ul>
   <#list senders as sender>   
     <#assign bySenderHref = gzUrl("/for/me/from/"+sender.username)/>
     <li><a href="${bySenderHref}">${sender.username}</a></li>
   </#list>
   <#if (senders?size > 1)>
     <li><a href="${gzUrl("/for/me")}">everyone</a>
   </#if>
   </ul>
   </#if>
</@sidebarElement>