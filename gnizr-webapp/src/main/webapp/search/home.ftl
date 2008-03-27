<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#include "./macro-lib.ftl"/>
<#assign title="seach -- gnizr"/>

<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-search.css")]/>
<@headerBlock/>

<@pageContent>  
 <@infoBlock></@infoBlock>
 <@mainBlock>
 <@searchForm namespace="/bookmark"/>
 </@mainBlock>
</@pageContent>
<@pageEnd/>


