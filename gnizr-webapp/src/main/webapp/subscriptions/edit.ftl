<#assign ww=JspTaglibs["/WEB-INF/webwork.tld"]>
<html>
<title>edit subscription - gnizr</title>
<h2>
<@ww.url id="userhome" value="viewUserPage.action?username=${loggedInUser.username}"/>
gnizr / <a href="${userhome}">${loggedInUser.username}</a>
</h2>
<hr/>
<h3>add/edit subscription</h3>
<@ww.actionerror/>
<@ww.form action="saveSubscription.action" method="post">
  <@ww.textfield cssStyle="width: 500px;" required="true" label="feed url" size="80" name="bookmark.link.url"/>
  <@ww.textfield cssStyle="width: 500px;" required="true" label="description" size="80" name="bookmark.title"/> 
  <@ww.textfield cssStyle="width: 500px;" label="tags" name="bookmark.tags"/>
  <#if op?exists>
    <@ww.hidden name="op" value="${op}"/>
  <#else>
    <@ww.hidden name="op" value="add"/>
  </#if>
  <@ww.submit value="save"/>
</@ww.form>
</html>