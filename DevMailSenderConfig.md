To enable email support in gnizr, you must properly configure the `mailSender` property in the `gnizr-config.xml` file. This property defines how gnizr should communicate with the SMTP server on your network, and use it to send email messages.

This document describes some typical `mailSender` configuration.

For advanced SMTP configuration, consult [JavaMailSenderImpl](http://static.springframework.org/spring/docs/2.5.x/api/org/springframework/mail/javamail/JavaMailSenderImpl.html) and [JavaMail](http://java.sun.com/products/javamail/javadocs/com/sun/mail/smtp/package-summary.html) documentation.

## SMTP Server Runs on `localhost` ##

If the SMTP server runs on the same host as the gnizr web application, the following configuration should be sufficient.

```
<bean id="mailSender"
      class="org.springframework.mail.javamail.JavaMailSenderImpl" 
      singleton="true">
  
  <property name="host">
    <value>localhost</value>
  </property>

</bean>
```

## SMTP Server with SSL Support ##

If the SMTP server supports SSL and requires user authentication, you can use the following configuration:

```
<bean id="mailSender"
      class="org.springframework.mail.javamail.JavaMailSenderImpl" 
      singleton="true">

  <property name="host">
    <value>YOUR_MAIL_HOST</value>
  </property>
	
  <property name="port">
    <value>465</value>
  </property>
	
  <property name="protocol">
    <value>smtp</value>
  </property>
	
  <property name="username">
    <value>YOUR_MAIL_USERNAME</value>
  </property>

  <property name="password">
    <value>YOUR_MAIL_PASSWORD</value>
  </property>

  <property name="javaMailProperties">
    <props>
      <prop key="mail.smtp.auth">true</prop>
      <prop key="mail.smtp.socketFactory.port">465</prop>
      <prop key="mail.smtp.socketFactory.class">javax.net.ssl.SSLSocketFactory</prop>
      <prop key="mail.smtp.timeout">25000</prop>
      <prop key="mail.smtp.socketFactory.fallback">false</prop>
    </props>
  </property>

</bean>
```