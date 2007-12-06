<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#if (username)?exists>
  <#assign title=username+"'s clustermap -- gnizr"/>  
  <#assign sourceUrl=gzFullUrl("/data/clustermap/bookmark.action?username="+username)/>
<#else>
  <#assign title="clustermap -- gnizr"/> 
</#if>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-clustermap.css")]/>

<@headerBlock/>
<@pageContent>
<#assign bct = userhomeBCT(username) +
               [gzBCTPair('bookmark archive',gzUserBmarkArchivesUrl(username)),
                gzBCTPair('clustermap view',gzUrl("/clustermap/user/"+username))]/>
<@infoBlock bct=bct/> 
<div id="clustermap">
<applet code="com.gnizr.core.web.clustermap.ClusterMapApplet.class" 
        codebase="${gzUrl("/applets")}"
        archive="gnizr-clustermap-applet.jar"
        width="100%" height="100%">
  <param name="datasourceurl" value="${sourceUrl}">
</applet>
</div>
</@pageContent>

<@pageEnd/>