<#include "/lib/web/macro-lib.ftl"/>
<#assign username=loggedInUser.username/>
<#assign title="bookmark saved -- gnizr"/>
<@pageBegin pageTitle=title>
<script type="text/javascript" src="${gzUrl("/lib/javascript/close-popup.js")}"></script>
</@pageBegin>
<@headerBlock>
</@headerBlock>
<@pageContent>
<center>
<div class="system-message">
<p class="title">
Successfully saved your bookmark.</p>
<p class="description">
This window will automatically <a id="closeme" href="#">close</a> in a few seconds.
</p>
</div>
</center>
</@pageContent>
<@pageEnd/>