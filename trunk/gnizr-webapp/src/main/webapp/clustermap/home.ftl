<#-- /webapp/clustermap/home.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#assign user = loggedInUser/>
<@pageBegin pageTitle=title/>
<@headerBlock/>
<@pageContent>
<@infoBlock>Clustermaps of ${user.username}</@infoBlock>
<ul>
<li>View ${user.username}'s bookmark in clustermap</li>
<li>Select a folder to be viewed in clustermap</li>
</ul>

</@pageContent>
<@pageEnd/>