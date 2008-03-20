<#-- error.ftl -->
<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>
<#assign title="Oops..."/>
<#if loggedInUser?exists>
  <#assign user=loggedInUser/>
</#if>
<#if username?exists == false && (user.username)?exists == true>
  <#assign username=user.username/>
</#if>
<@pageBegin pageTitle=title />
<@headerBlock></@headerBlock>
<@pageContent>
<@mainBlock>
<h2>You reach this page because:  </h2>
<p>
  <ul> 
  <li>the page that you tried to access is still under construction, or </li>
  <li>the operation that you tried to invoke resulted an unexpected error</li>
  </ul>
  <h4>Questions? Contact your system administrator.</h4>
</p>
</@mainBlock>
</@pageContent>
<@pageEnd/>