<#-- /settings/home.ftl -->
<#-- import Freemarker macros that are common to the site -->
<#include "/lib/web/macro-lib.ftl"/>

<#assign title="${loggedInUser.username}'s settings -- gnizr"/>
<#assign username=loggedInUser.username/>
<@pageBegin pageTitle=title cssHref=[gzUrl("/css/gnizr-settings.css")]/> 
<@headerBlock/>
<@pageContent>
<#assign bct = settingsBCT(username) + 
               [gzBCTPair('RSS robot',gzUrl('/admin/robot.action'))]/>
<@infoBlock bct = bct/>
<@mainBlock>
<@ww.form action="editRobot" namespace="/admin" method="post" title="configuration" cssClass="userInputForm">
<@ww.checkbox label="Service Enabled" name="status" value="${status?string}" labelPosition="left"/>
<#--
<@ww.textfield label="How Often? (hours)" size="3" name="timeInterval" value="${timeInterval?c}"/>
-->
<@ww.submit value="Save" cssClass="btn"/>
</@ww.form>
</@mainBlock>
</@pageContent>
<@pageEnd/>