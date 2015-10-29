This page describes the steps to install gnizr web applications. Developers can also download and build gnizr from [the SVN source code](HowToBuild.md).

If you have any questions, post them in [Gnizr User Discussion](http://groups.google.com/group/gnizr-users).

## System Requirement ##

  * Apache Tomcat 5.x
  * MySQL 5.0+
  * Cygwin (if running on a Windows machine)

## Steps to Install ##

  1. Download and unpack a gnizr release.
    * e.g., after unpacking, the top-level directory is `gnizr-x.y.z`
  1. Prepare MySQL database for installation
    * See SetupGnizrDatabase
  1. Copy gnizr web application directory to Tomcat
    * `cp -r gnizr-x.y.z/gnizr /your/tomcat/webapps/`
  1. Edit gnizr configuration
    * _For gnizr 2.3.0_: See [GnizrConfigXML](GnizrConfigXML.md)
    * _For gnizr 2.4.0_: See [GnizrConfigXML\_2\_4](GnizrConfigXML_2_4.md)
  1. Start Tomcat

## Steps to Upgrade ##

  1. Download and unpack a gnizr release.
    * e.g., after unpacking, the top-level directory is `gnizr-x.y.z`
  1. Upgrade gnizr database
    * See _Upgrade database tables and stored procedures_ in SetupGnizrDatabase
  1. Backup your existing gnizr web application in Tomcat
    * e.g., `mv /your/tomcat/webapps/gnizr to /tmp/backup-gnizr`
  1. Copy gnizr web application directory to Tomcat
    * `cp -r gnizr-x.y.z/gnizr /your/tomcat/webapps/`
  1. Edit gnizr configuration
    * _For gnizr 2.3.0_: See [GnizrConfigXML](GnizrConfigXML.md)
    * _For gnizr 2.4.0_: See [GnizrConfigXML\_2\_4](GnizrConfigXML_2_4.md)
  1. Start Tomcat

|**TIPS**: Don't forget to upgrade your bookmarklet program. It can be found in the `help` page. |
|:-----------------------------------------------------------------------------------------------|


## Login and Create New Accounts ##

Gnizr comes with a default user account -- `gnizr`, who is the administrator of the system. After you have logged in as `gnizr`, you can create additional user accounts.

| **NOTE**: This `gnizr` account is not the same MySQL database user account for accessing gnizr database. |
|:---------------------------------------------------------------------------------------------------------|

  1. Go to the front page of gnizr
    * If installed on `localhost` port 8080, `http://localhost:8080/gnizr`
  1. Log in as `gnizr`
    * username: `gnizr`
    * password: `gnizr`
  1. Change `gnizr` password (**IMPORTANT!**)
    * Go to `settings` -> `Change your password`
  1. Create new accounts
    * Go to `settings` -> `Manage user accounts`

## Enable UTF-8 Support (Optional) ##

If you want to be able to save bookmarks in non-English languages, you may need to configure both Tomcat and MySQL to support UTF-8.

### Tomcat Configuration ###

In `server.xml` file, configure the `<Connector/>` to encode URI with UTF-8. This ensures all HTTP-GET and HTTP-POST parameters are properly encoded using UTF-8.
```
<Connector port="8080" maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" redirectPort="8443" acceptCount="100"
               connectionTimeout="20000" disableUploadTimeout="true"
               URIEncoding="UTF-8"/>
```

### MySQL Configuration ###

In the MySQL server configuration file (e.g. `my.ini`),  configure both client and server to use UTF-8 as their default encoding character set.

```
[mysql]
default-character-set=utf8
[mysqld]
default-character-set=utf8
character_set_server=utf8
```