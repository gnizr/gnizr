<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
<#-- if logged in, go to the home page of the user -->
<@pageBegin pageTitle="gnizr -- organize." 
            cssHref=[gzUrl("/css/gnizr-frontpage.css")] enableJS=false/>
            
<div class="frontPage">          

<div id="siteBanner">
  <h1 class="siteName">${getSiteName()}</h1>
  <h2 class="siteDescription">${getSiteDescription()}</h2>
</div>

<div class="frontPageMessage">
<@pageTitle>Site Maintenance</@pageTitle>
<p>
This site is currently under maintenance. 
</p>
<p>Please come back later.</p>
</div>
</div>
<div class="cleardiv"/>
<@pageEnd/>


