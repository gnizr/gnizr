A new user account has been created, but requires your approval.

  Username: ${username}
  Email: ${email}
  CreatedOn: ${createdOn?datetime?string.short_long}

1) Login as 'gnizr' -- the Super User.

${gnizrConfiguration.webApplicationUrl}/login

2) Aprrove or deny the request.

To approve the request, click the link below:

${gnizrConfiguration.webApplicationUrl}/register/approve.action?username=${username}&token=${token}

To deny the request, click the link below:

${gnizrConfiguration.webApplicationUrl}/register/deny.action?username=${username}&token=${token}

If clicking the link above does not work, copy and paste the URL in a
new browser window instead.

Thank you!


