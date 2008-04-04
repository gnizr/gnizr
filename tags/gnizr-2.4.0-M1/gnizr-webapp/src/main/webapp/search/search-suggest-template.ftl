<#assign keys = keywords?keys/>
<#if (keys?size > 0)>
<ul>
<#list keys as key>
	<li><a href="#" title="${key}" class="item choose-value">${keywords[key]}</a></li>
</#list>
</ul>
</#if>