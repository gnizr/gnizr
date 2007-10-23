<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#-- if logged in, go to the home page of the user -->
<@pageBegin pageTitle="gnizr -- organize." 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")] enableJS=false/>
            
<div id="headline">
<h1>gnizr<span class="version">${gnizrVersion()}</span></h1>
<br></br>
<h2>organize</h2>
<br/>
<h2>Server is currently under routine maintenance...</h2>
<h3>Please come back later!</h3>
</div>
<@pageEnd/>


