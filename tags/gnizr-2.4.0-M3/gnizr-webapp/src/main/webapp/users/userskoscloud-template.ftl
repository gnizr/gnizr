<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<@sidebarElement>    
    <@skosTagPhrase related=skosRelatedTags narrower=skosNarrowerTags broader=skosBroaderTags tag=tag/>    
</@sidebarElement>
