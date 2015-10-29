# User Authentication #

Gnizr implements a session-based user authentication. After a user is logged in, a special authentication object of the user is created in the HTTP Session. This object is subsequently used to authenticate the user when the client browser is trying access protected pages in gnizr.

When a user is logged out, the user's authentication object is removed from the session. The authentication object may also be destroyed when a previously established HTTP session is expired.

When logging into the system, in the login page, the user can choose to enable a 'Remember Me' option. When this option is enabled, a special cookie object will be created on the client browser if the login is successful. This 'Remember Me' cookie allows the user to be automatically authenticated when a currently established HTTP session is expired. The life-span of this cookie is set to 'never expire'. When the user is logged out (i.e., the user clicks on the 'logout' link), the gnizr application will attempt to delete the 'Remember Me' cookie from the browser.

# Database Implementation #

By default, the gnizr application authenticates a login user using the information stored in the [MySQL database table `user`](GnizrDatabaseModel.md). A user can be successfully authenticated if the input `username` and `password` match an existing record in the table.

In the database, the `password` value is an MD5 hash of the user-defined password. The MySQL function `md5()` is used to match the input password with the password in the database.

# API Implementation #

The class `com.gnizr.core.user.UserManager` implements the user authentication. This class doesn't directly perform JDBC calls to authenticate user login. It uses the low-level API provided by the `com.gnizr.db.dao.user.UserDao`.

The class `UserDao` is a Java Interface class. By default, the gnizr API provides an concrete implementation of `UserDao` for interacting the `user` table defined in the gnizr MySQL database. This class is `com.gnizr.db.dao..user.UserDBDao`. The instantiation of this implementation class is not "hard-wired" in the implementation of `UserManager` class. It's dynamically created and "injected" into the `UserManager` class by the Spring IoC framework.

The creation of `UserDBDao` is defined in `spring/gnizr-dao.xml`.

```
<bean id="userDao" class="com.gnizr.db.dao.user.UserDBDao" singleton="true">
  <constructor-arg>
    <ref bean="dataSource"/>
  </constructor-arg>
</bean>

<!-- this class holds all low-level DAO objects used in gnizr -->
<bean id="gnizrDao" class="com.gnizr.db.dao.GnizrDao" singleton="true">
 ...
  <property name="userDao">
    <ref bean="userDao"/>
  </property>
 ...
</bean>
```

The injection of the class into `UserManager` is defined in `spring/core-managers.xml`.

```
<bean id="userManager" class="com.gnizr.core.user.UserManager" singleton="true">
  <constructor-arg>
    <ref bean="gnizrDao"/>
  </constructor-arg>
</bean>	
```

These XML files are located in the `gnizr-webapp` project, directory `src/main/resources`.


# WebWork Action Implementation #

The class `com.gnizr.web.action.user.UserLogin` and `com.gnizr.web.action.user.UserLogout` handle user login and user logout functions, respectively.

The `UserLogin` class is implemented to handle FORM input submitted by a user from a login page. The a pair `username` and `password` is received, the class uses the `UserManager` class to verify the login information. If the information is correct, then a special authentication object is created and placed in the HTTP session. This object is an instance of the `com.gnizr.db.dao.User` class, and it can be accessed from the session using the key string `loggedInUser`. The `UserLogin` class also handles the creation of the 'Remember Me' cookie.

The `UserLogout` class is implemented to handle user log out. When a logout request is received, this action removes the authentication object (i.e., key `loggedInUser`) from the HTTP session. If a 'Remember Me' cookie exists in the session, the action will attempt to delete it from the client browser.


