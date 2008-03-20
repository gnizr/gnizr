<#-- timeline/home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#-- import Freemarker macros that are specific to TIMELINE -->
<#include "./macro-lib.ftl"/>

<#if (user.username)?exists>
  <#assign title=user.username+"'s timeline"/>
  <#assign sourceUrl=gzUrl("/user/"+user.username+"/output/timeline")/>
<#else>
  <#assign title="timeline"/> 
</#if>

<@pageBegin pageTitle=title             
            cssHref=[gzUrl("/css/gnizr-timeline.css")]>
  <script type="text/javascript" src='${gzUrl('/lib/javascript/timelineapi/timeline-api.js')}'></script>
  <script type="text/javascript" src='${gzUrl('/lib/javascript/timelineapi/scripts/sources.js')}'></script>
  <script type='text/javascript' src='${gzUrl('/lib/javascript/timeline.js')}'></script>
  <script type='text/javascript' src='${gzUrl('/lib/javascript/timeline-util.js')}'></script>
</@pageBegin>
<@headerBlock></@headerBlock>
<@pageContent>
<#assign bct = userArchiveBCT(user.username) + 
               [gzBCTPair('timeline view',gzUrl('/timeline/user/'+user.username))]/>
<@infoBlock bct=bct/>               
<@mainBlock>               
<#if (user.username)?exists>
<h2>Timeline of when bookmarks are created</h2>
<a id="timelineSourceUrl" class="invisible" href="${sourceUrl?if_exists}">timeline data</a>
</#if>
<div id="timeline"></div>
<div id="controls"></div>
</@mainBlock>
</@pageContent>

<@pageEnd/>
