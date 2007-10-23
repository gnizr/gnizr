<html>
<head>
<title>${user.username}'s subscriptions</title>
</head>
<h2>
<a href="<@ww.url value="viewUserPage.action" username="${user.username}"/>">${user.username}</a>'s subscriptions</h2>
<hr/>
<ol>
<#if subscriptions?has_content == false>
No subscribed subscriptions.
</#if>
<@ww.actionerror/>
<#list subscriptions as subs>  
	<li>${subs.bookmark.title}
    <@ww.url id="delbm" action="deleteSubscription" feedId="${subs.id}" includeParams="none"/>
    <@ww.url id="edtbm" action="updateSubscription" feedId="${subs.id}" includeParams="none"/>
    <font size="-1"><a href="${edtbm}">edit</a>/<a href="${delbm}">delete</a></font>
    </li>
</#list> 
</ol>
<font size="-1">
<a href="<@ww.url action="addSubscription"/>">add subscription</a>
</font>
</html>