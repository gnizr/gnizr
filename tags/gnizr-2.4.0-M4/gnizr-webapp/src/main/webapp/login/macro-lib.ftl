<#macro frontPage title>
<@pageBegin pageTitle=title
            cssHref=[gzUrl("/css/gnizr-frontpage.css")]
            enableJS=false/>                      
<!-- frontPage DIV STARTS -->
<div class="frontPage">          

  <div id="siteBanner">
    <h1 class="siteName"><a href="${gzUrl("/")}" class="nodecor">${getSiteName()}</a></h1>
    <h2 class="siteDescription">${getSiteDescription()}</h2>
  </div>
  
  <#nested/>
</div>
<!-- frontPage DIV ENDS -->

<div class="cleardiv"/>
<@pageEnd/>
</#macro>