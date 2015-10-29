This document is intended for developers who want to write programs against gnizr Java API.

| Instructions described here are _experimental_ for the release gnizr-2.3.0-M3 and later. They may not work properly for the earlier releases. If you have trouble following these intructions, send your questions to [gnizr users](http://groups.google.com/group/gnizr-users). |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

# Introduction #

Gnizr provides a set of Java API that allows developers to create new application behavior  and to interface with gnizr data objects -- e.g., user accounts, bookmarks, links, tags and folders and RSS feeds.

Different ways to introduce new application behavior:
  1. Create new WebWork actions or JavaScript code that augment the existing gnizr function.
  1. Create custom implementation of gnizr interface components and inject these implementations into a gnizr installation.
  1. Create separate Java programs to manipulate the data objects stored in the gnizr database.
  1. Create web mashup programs to consume RDF, JSON or RSS documents published by gnizr.

Developing third-party web mashup programs typically doesn't require special build environment setup. These programs simply read data documents from the appropriate URL published by gnizr. Creating Java programs to access data objects stored in a gnizr database or developing custom gnizr components usually will require the use of **gnizr Core API**. Adding new application behavior by creating WebWork actions usually will require the use of **gnizr Core API** and **gnizr Web Application API**.

### Technical Documents ###

Follow these links to a specific development topic:

  1. [Gnizr Architecture Overview](GnizrDesignOverview.md)
  1. [Database Model Overview](GnizrDatabaseModel.md)
  1. [User Account Management and Authentication](DevUserManageAndAuth.md)
  1. [Gnizr OpenSearch Support](DevOpenSearch.md)
  1. Bookmark Management and Event Notification
  1. RSS Crawling and Gnizr Robot
  1. Machine Tag Syntax and Implementation
  1. Associate Geometry Markers with Bookmarks

### Java API Documentations ###

  * [Gnizr 2.3.0 API](http://dev.gnizr.com/javadocs/2.3.0)

### Tutorial ###
  * [HelloWorld: Develop a new action to say "Hello World"](HelloWorldDemo.md)


The rest of this document is focused on how to setup a build environment.

# Your Build Environment You Choose #

We recommend developers to setup build environment using a combination of tools: [Maven](http://maven.apache.org), [Eclipse](http://www.eclipse.org), [Tomcat](http://tomcat.apache.org) and [MySQL](http://www.mysql.com). For the specifics on how to install and use these tools, consult their respective documentations.

**Depending on which set of gnizr API you want to use, you can choose to set up your build environment in one of two ways (see below).**
  * Option 1: suitable for building programs that only require using gnizr Core API and do not need to integrate any functions into the web application framework of gnizr.
  * Option 2: suitable for creating new application behaviors that will integrated into the existing web application framework.

Here are some examples.

If you want to write a separate Java program to create new gnizr user accounts from an existing enterprise database, you can choose the first option. Your program probably will use gnizr Core API to create user account objects, and it doesn't need to be integrated into the existing gnizr web application framework.

If you want to add a new kind of search capability in gnizr and make this capability available to all users in a gnizr installation, you should choose the second option. It's likely that your implementation will build on the web application framework of gnizr, which is a collection of WebWork actions and Freemarker template pages. To test and deploy your implementation, you will need to extend from gnizr Web Application API, add configuration to the gnizr WebWork and Spring configuration, place Freemarker template pages in the appropriate file system directories, etc.

## Option 1: Don't need to use gnizr Web Application API ##

#### Step 1: Install gnizr, Eclipse and Maven ####

Download and install a copy of the gnizr release that you want to work with. Install Eclipse IDE for writing code. Install Maven for building your program.

#### Step 2: Create a new Maven project ####

Let's assume that you are going to create a new project call `newusers`.
```
mvn archetype:create 
    -DgroupId=com.myproj 
    -DartifactId=newusers 
    -Dpackagename=com.myproj.newusers`
```

See also: [An Introduciton to Maven 2](http://www.javaworld.com/javaworld/jw-12-2005/jw-1205-maven.html)

#### Step 3: Configure project's `pom.xml` ####

Setup `pom.xml` to include gnizr library dependency.

  * Repository: `http://dev.gnizr.com/maven2`
  * group ID: `com.gnizr`
  * artifact ID (gnizr Core API):
    * `gnizr-db`: gnizr database API
    * `gnizr-core`: gnizr core API
    * `gnizr-delicious`: gnizr del.icio.us import API
    * `gnizr-robot`: gnizr robot framework

Example:

```
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             http://maven.apache.org/maven-v4_0_0.xsd">
...
<repositories>
  <repository>
    <id>gnizr-repos</id>
    <name>Gnizr Public Maven 2 Repository</name>
    <url>http://dev.gnizr.com/maven2</url>
    <layout>default</layout>
    <snapshots>
      <enabled>false</enabled>
    </snapshots>
  </repository>

  <!-- other maven repositories -->
  ...
</repositories>

<dependencies>

  <dependency>
    <groupId>com.gnizr</groupId>
    <artifactId>gnizr-core</artifactId>
    <!-- replace this value with the gnizr version that you want to work with -->
    <version>2.3.0</version>
    <scope>compile</scope>
  </dependency>
   
  <!-- other library dependencies -->
  ...
</dependencies>
</project>

```

#### Step 4: Initialize Eclipse Project ####

Use Maven Eclipse plug-in to create project files for Eclipse.

```
mvn eclipse:eclipse
```

This command should automatically downloads all necessary Maven plugins and gnizr dependencies. If you encounter errors in fetching gnizr library, [please let us know](http://groups.google.com/group/gnizr-users).

#### Step 5: Open your project in Eclipse ####

  * Start Eclipse
  * Add a new project by import from the existing file system.

Done! Read gnizr **technical documents** (see Introduction) and check out [gnizr Java API](http://dev.gnizr.com/javadocs/).

## Option 2: Building on gnizr Web Application API ##

#### Step 1: Install Eclipse and Maven ####

Install Eclipse IDE for writing code. Install Maven for building your program.

#### Step 2: Create a new Maven Project ####

Let's assume that you want to create a new web application project called `HelloWorldWebapp`.
```
mvn archetype:create 
 -DgroupId=com.example
 -DartifactId=HelloWorldWebapp
 -Dpackagename=com.example.gnizr 
 -DarchetypeArtifactId=maven-archetype-webapp 
```

#### Step 3: Configure project's `pom.xml` ####

```
<project 
  xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>HelloWorldWebapp</artifactId>
  <packaging>war</packaging>
  <version>1.0-SNAPSHOT</version>
  <name>HelloWorldWebapp Gnizr Webapp</name>
  <url>http://example.com</url>

  <repositories>
    <repository>
      <id>gnizr-repos</id>
      <name>Gnizr Public Maven 2 Repository</name>
      <url>http://dev.gnizr.com/maven2</url>
      <layout>default</layout>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
  </repository>

  <!-- other maven repositories -->
  ...
</repositories>

  <dependencies>
    <dependency>
      <groupId>junit</groupId>
      <artifactId>junit</artifactId>
      <version>3.8.1</version>
      <scope>test</scope>
    </dependency>		
    <dependency>
      <groupId>com.gnizr</groupId>
      <artifactId>gnizr-web</artifactId>
      <!-- replace this value with the gnizr version that you want to work with -->
      <version>2.3.0-M3</version>
      <type>jar</type>
      <scope>provided</scope>
    </dependency>
    <dependency>
      <groupId>com.gnizr</groupId>
      <artifactId>gnizr-webapp</artifactId>
      <!-- replace this value with the gnizr version that you want to work with -->
      <version>2.3.0-M3</version>
      <type>war</type>
      <scope>runtime</scope>
    </dependency>
  </dependencies>
  <build>
    <finalName>HelloWorldWebapp</finalName>
    <plugins>
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
	<artifactId>maven-compiler-plugin</artifactId>
	<configuration>
	  <source>1.5</source>
	  <target>1.5</target>
	  <encoding>UTF-8</encoding>
	</configuration>
      </plugin>
      <plugin>
        <artifactId>maven-clean-plugin</artifactId>
        <configuration>
          <filesets>
            <fileset>
	      <directory>${basedir}/src/main/webapp</directory>
	      <includes>
	        <include>**/*</include>
	      </includes>
	    </fileset>
	  </filesets>
	</configuration>
      </plugin>			
    </plugins>
  </build>	
</project>
```

#### Step 4: Initialize Eclipse Project ####

Use Maven Eclipse plug-in to create project files for Eclipse.

```
mvn eclipse:eclipse
```


#### Step 5: Open your project in Eclipse ####

  * Start Eclipse
  * Add a new project by import from the existing file system.

#### Step 6: Overwrite gnizr configuration files ####

There are several configuration files that define how the gnizr web application is instantiated in a Tomcat server. Developers usually need to extend these configuration files in order to include new application behavior. For information on how these configuration files work and how to use them, see GnizrConfigurationFiles and HelloWorldDemo.

| **TIPS**: It's strongly recommended that you define these configuration files in the `src/main/resources` directory of the new project.|
|:---------------------------------------------------------------------------------------------------------------------------------------|

#### Step 7: Develop the new application that extends gnizr ####

You should place all your program files in the `src/main` directory.

| **Type of files** | **Where to place them** |
|:------------------|:------------------------|
| `*.java`          | `src/main/java`         |
| `*.xml`           | `src/main/resources`    |
| `*.ftl` (freemarker templates) | `src/main/resources/[your-project-nname]/`|
| javascript, html, css, jsp files | `src/main/resources/[your-project-name]/`|
| `spring.xml`      | `src/main/resources`    |
| `xwork.xml`       | `src/main/resources`    |

See [HelloWorldDemo](HelloWorldDemo.md) for an example of program file structure layout.

#### Step 8: Overlay `gnizr-webapp` WAR into your project ####

```
mvn compile war:inplace
```

| **TIPS**: It's highly recommended that you install this [Tomcat Eclipse Plugin](http://www.eclipsetotale.com/tomcatPlugin.html). Using this plugin, you can use Eclipse IDE to run and debug your program running in Tomcat. |
|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|


#### Step 9: Package gnizr along with your new project ####

To build a WAR file that contains the standard gnizr web application and your new application, you can invoke the following Maven command:

```
mvn war:war
```

Before you create the WAR file, make sure that a properly configure `gnizr-config.xml` is either defined in your project or inherited from the `gnizr-webapp` project. If you don't define `gnizr-config.xml` in your project, it's inherited from `gnizr-webapp` when you invoke the Maven command to perform "overlay" (see Step 8).