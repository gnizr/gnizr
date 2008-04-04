<#-- clustermap/home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#include "/lib/web/util-func-lib.ftl"/>
<#-- import Freemarker macros that are specific to TIMELINE -->
<#include "./macro-lib.ftl"/>

<#if (username)?exists>
  <#assign title=user.username+"'s folder clustermap"/>  
  <#assign sourceUrl=gzFullUrl("/data/clustermap/folder.action?username="+username+"&folderName="+folderName?url)/>
<#else>
  <#assign title="clustermap -- gnizr"/> 
</#if>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-clustermap.css")]/>

<@headerBlock/>
<@pageContent>
<#assign bct = userhomeBCT(username) + 
               [gzBCTPair("folders",gzUserFolderUrl(username,"")),
                gzBCTPair(gzFormatFolderName(folderName),gzUserFolderUrl(username,folderName)),
                gzBCTPair('clustermap view',gzUrl("/clustermap/user/"+username+"/folder/"+folderName))]/>
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