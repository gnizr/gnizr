<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"] >
<#include "/lib/web/macro-lib.ftl"/>
  <#assign hasFocusedMenu = false/>      
  <#assign path2match = request.servletPath/>
  <!-- ${path2match} -->
  <ul id="primary">
  <#list menuItems?if_exists as mItm>  
    <li>      
    <#if (path2match?matches(mItm.actionNamespace)) || (mItm_has_next == false && hasFocusedMenu == false)>
      <a class="current" href="${resolveUrl(mItm.actionPath)}">${mItm.actionLabel}</a>
       <ul id="secondary">
         <#list mItm.subItems as subItm>
           <li><a href="${resolveUrl(subItm.actionPath)}">${subItm.actionLabel}</a></li>
         </#list>         
       </ul>
       <#assign hasFocusedMenu = true/>
    <#else>
      <a href="${resolveUrl(mItm.actionPath)}">${mItm.actionLabel}</a>        
    </#if>
    </li>
  </#list>
  </ul>

<#function resolveUrl actionPath>
  <#if actionPath?starts_with("http://")>
    <#return actionPath/>
  <#else>
    <#return gzUrl(actionPath)/>
  </#if>
</#function>  
  