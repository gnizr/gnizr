<#-- settings/macro-lib.ftl -->
<#-- assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#--
MACRO: settingsBlock
INPUT: title: string // title of this settings block
-->
<#macro settingsBlock title>
<p class="settings">
<h4>${title}</h4>
  <#nested/>
</p>
</#macro>
