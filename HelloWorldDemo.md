This document is intended for developers who want to extend gnizr capability or alter it's default application behavior. The build environment and setups described here are only a recommendation. Developers can choose to use other tools and build environment setups.

| Instructions described here are _experimental_ for the release gnizr-2.3.0-M3 and later. They may not work properly for the earlier releases. If you have trouble following these intructions, send your questions to [gnizr users](http://groups.google.com/group/gnizr-users). |
|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

# Introduction #

Gnizr is designed for develoeprs to add new capabilities and alter the existing application behavior. The code base of gnizr is structured in a way that attempts to simplify many software engineering tasks. Through a "Hello World" example, this tutorial will overview the gnizr code base structure and show you how to rapidly prototype and extend gnizr.

## What You Should Know ##

Readers of this document are assumed to be familiar with the use of Eclipse for Java development and the basic usage of Maven. In addition, the readers are assumed to have some programming knowledge with [WebWork](http://www.opensymphony.com/webwork/) and [Spring](http://www.springframework.org/) framework.

  * [Spring Framework Documentation](http://www.springframework.org/documentation)
  * [WebWork Framework Documentation](http://www.opensymphony.com/webwork/documentation.action)
  * [An Introduction to Maven 2](http://www.javaworld.com/javaworld/jw-12-2005/jw-1205-maven.html?lsrc=maven-users)

## What You Should Have Installed ##

Make sure you have the following installed and properly configured on your machine:

  1. Eclipse IDE + Sysdeo Tomcat Plugin for Eclipse
  1. Maven 2
  1. Gnizr 2.3.0+

If you are new to developing WebWork applications in Eclipse, follow [these instructions](http://www.opensymphony.com/webwork/wikidocs/Setting%20up%20Eclipse%20with%20Tomcat.html) to set up an integrated development environment of Eclipse and Tomcat.

It's essential that you have followed [this instruction](SetupGnizrDatabase.md) and initialized MySQL database for gnizr. In this tutorial, we assume that you have taken the default settings (i.e., DB names, usernames and password) described in the instruction.

# Gnizr Code Base Structure #

Building an extended gnizr, you need to use gnizr API libraries. These API libraries are available for download in our public Maven Repository (http://dev.gnizr.com/maven2). As of 2.3.0, the gnizr project consists of multiple Maven project modules.

```
 |-- gnizr (parent)
   |-- gnizr-db (child)
   |-- gnizr-core 
   |-- gnizr-web
   |-- gnizr-delicious
   |-- gnizr-clustermap
   |-- gnizr-robot
   |-- gnizr-test
   |-- gnizr-webapp
```

In each project module, in both `parent` and `child`, there is a POM file. In the `parent` project, the POM file defines all dependency libraries and special Maven plugin configuration for assemble gnizr release. In the `child` projects, the POM files inherit those configuration from the `parent` POM and add customize configurations that are specific to the `child` project.

|**TIPS**: There are dependencies among `child` projects. Usually your own project should define `gnizr-web` and `gnzir-webapp` as dependency libraries. `gnizr-web` should have dependency scope `provided`, and `gnizr-webapp` should have dependency scope `runtime`.|
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

Different `child` projects provide implementation of different parts of the gnizr application. The `parent` project is responsible for combining `child` project implementation into a single deployable gnizr package. Some `child` project may contains only Java class files, and some others may contain Java class files and static resources, e.g., template pages for building dynamic HTML pages and application configurations. JUnit tests (Java classes and XML input files) always reside in the `child` project in which the tests are defined.

| **Project Name** | **Description** | **Packaging Type** |
|:-----------------|:----------------|:-------------------|
| `gnizr-db`       | Implementation of gnizr database operations.  | jar                |
| `gnizr-core`     | Business logic that hides database operations from the high-level implementation. | jar                |
| `gnizr-web`      |  Interface and abstract implementation of all gnizr WebWork actions, and convenient classes for WebWork programming | jar                |
| `gnizr-delicious` | API for importing user bookmarks from `del.icio.us` | jar                |
| `gnizr-clustermap` | Clustermap applet | jar                |
| `gnizr-robot`    | Implementation for RSS crawling and bookmark imports| jar                |
| `gnizr-test`     | The base implementation of gnizr JUnit tests.| jar                |
| `gnizr-webapp`   |  Main implementation of the application and its configuration files | war                |

--

# Say "Hello World" #

The objective of our "Hello World" program is to add a new page in gnizr that does the following two things:
  1. When a user visits the page in a browser, the page prints "Hello World" in the body of the page.
  1. If a request parameter `message` is defined when visiting the page, the page will print "Hello World" and also the value of `message` .

| Say "Hello World" | Say "Hello World" + a custom message |
|:------------------|:-------------------------------------|
| [![](http://farm3.static.flickr.com/2171/2123343895_61fa6a7ae8_m.jpg)](http://www.flickr.com/photo_zoom.gne?id=2123343895&size=o) | [![](http://farm3.static.flickr.com/2232/2123343931_396850e96d_m.jpg)](http://www.flickr.com/photo_zoom.gne?id=2123343931&size=o)|


## Step 1: Create Maven Project ##

Run the following command to create a Maven project. The `artifactId` parameter defines our project name `HelloWorldWebapp`. The `archetypeArtifactId` parameter defines that your project will use Maven's WAR packaging, as oppose to the default JAR packaging.

```
mvn archetype:create 
 -DgroupId=com.example
 -DartifactId=HelloWorldWebapp
 -Dpackagename=com.example.gnizr 
 -DarchetypeArtifactId=maven-archetype-webapp
```

## Step 2: Edit POM File ##

In the `HelloWorldWebapp` project directory, you should find `pom.xml`. Edit this file.

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

|**Note**: the dependency type of `gnzir-web` is `jar` and scope `provided`, and the dependency type of `gnizr-webapp` is `war` and scope `runtime`. |
|:---------------------------------------------------------------------------------------------------------------------------------------------------|

The above POM file configuration will help us to set up the build environment with the following properties:
  * Make available all gnizr API classes and their dependency library classes in our project's `CLASSPATH`. This done through defining `gnzir-web` is of type `jar` and scope `provided`.
  * Allow the standard gnizr web application (i.e., its WAR file structure) to be "[overlaid](http://maven.apache.org/plugins/maven-war-plugin/overlays.html)" on the top of our own web application project. This is done through defining `gnizr-webapp` is of type `war` and scope `runtime`.
  * Instruct Maven to build our project using Java 1.5 compiler.
  * Instruct `maven-clean-plugin` to perform extra "cleaning" to delete files in `${basedir}/src/main/webapp` whenever we call `mvn clean`.

## Step 3: Prepare our project for Eclipse ##

Once we have configured our POM file, we will use the [Maven Eclipse Plugin](http://maven.apache.org/plugins/maven-eclipse-plugin/) to prepare our project so that it becomes editable in the Eclipse IDE.

| **TIPS:** Before calling the `mvn eclipse:eclipse` command. Check if `src/main/java` directory exists. If not, create it. |
|:--------------------------------------------------------------------------------------------------------------------------|

Run the following command:

```
mvn eclipse:eclipse
```

Now start Eclipse IDE and import `HelloWorldWebapp` as a Eclipse project.
| **1** | **2** | **3** |
|:------|:------|:------|
| ![http://farm3.static.flickr.com/2345/2123441445_3247bc949e_o.png](http://farm3.static.flickr.com/2345/2123441445_3247bc949e_o.png) |![http://farm3.static.flickr.com/2004/2124210834_9bcda20d8b_o.png](http://farm3.static.flickr.com/2004/2124210834_9bcda20d8b_o.png)| ![http://farm3.static.flickr.com/2288/2124223206_4003a7594b_o.png](http://farm3.static.flickr.com/2288/2124223206_4003a7594b_o.png) |

| **TIPS:** If you see any compile error that reports `M2_REPO` is undefined. In Eclipse, go to `Window` -> `Preference...` -> `Java` -> `Build Path` -> `Classpath Variables`. Define a new variable `M2_REPO` to point to your local maven repository (e.g., `~/.m2`). Here is [an example](http://www.flickr.com/photos/14804582@N08/2255500022/). |
|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

## Step 4: Create Action and Overwrite Default Configuration ##

There are few things to do in this step. (1) We need to create a new WebWork action to act as the "control" of our "Hello World" program. (2) We need to create a new Freemarker template page to render the dynamic HTML page of our action output. (3) We need to inject our new action implementation into the gnizr web application.

### (1) Create `HelloWorldAction.java` ###

In `src/main/java`, let's create a new Java class called `HelloWorldAction`.

![http://farm3.static.flickr.com/2115/2123471425_026ae8cf13_o.png](http://farm3.static.flickr.com/2115/2123471425_026ae8cf13_o.png)

```
package com.example.gnizr;

import com.gnizr.web.action.AbstractAction;

public class HelloWorldAction extends AbstractAction{
  
  private static final long serialVersionUID = 4604179423988890376L;

  private String message;
	
  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  @Override
  protected String go() throws Exception {
    return SUCCESS;
  }
}
```

### (2) Create `index.ftl` ###

In `src/main/resources/helloworld`, we create a new Freemarker template file called `index.ftl`.

|**TIPS**: There is no special restriction on the naming of this directory. But it's highly recommended that you choose a directory name that is unique or at least identifies your project. |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

![http://farm3.static.flickr.com/2419/2124262824_38193778c3_o.png](http://farm3.static.flickr.com/2419/2124262824_38193778c3_o.png)

```
<#-- Freemarker macros provided by gnizr -->
<#include "/lib/web/macro-lib.ftl"/>

<#-- macro builds the heading HTML -->
<@pageBegin pageTitle="Gnizr HelloWorld Page" cssHref=[]/>

<#-- macro builds the HTML of the top menu bar, search box etc. -->
<@headerBlock/>

<#-- macro builds the HTML of the page body -->
<@pageContent>

  <#-- the meat of our Hello World -->
  <p>Hello World!</p>
  <#if message?exists>
    <ul><li>Message: ${message}</li></ul>
  </#if>

</@pageContent>

<#-- macro build the HTML of page footer -->
<@pageEnd/>
```

### (3) Add "Hello World" to gnizr web application ###

In order to make available our action and template file in gnizr, we need to tell gnizr's Spring configuration to create `HelloWorldAction` when the web application starts, and tell the xwork configuration how our action will be defined (e.g., what's the action name and namespace, are we using any interceptors, how action results are handled, etc.).

In our `HelloWorldWebapp`, we first define a set of local Spring and xwork configuration, and then we define a set of global configuration files that will overwrite those defined in the standard gnizr web application.

![http://farm3.static.flickr.com/2152/2124292642_16c59ef22e_o.png](http://farm3.static.flickr.com/2152/2124292642_16c59ef22e_o.png)

| **Filename** | **Description** |
|:-------------|:----------------|
| `my-spring.xml`           |  Local Spring configuration that defines how Spring should create `HelloWorldAction`           |
| `my-xwork.xml` | Local xwork configuration that defines the `sayHello.action`  |
| `spring.xml` | Overwritten the global Spring configuration. Adds an extra `import` tag to include `my-spring.xml`|
| `xwork.xml`  | Overwritten the global xwork configuration. Adds an extra `include` tag to include `my-xwork.xml` |

**File: `my-spring.xml`**
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
  "http://www.springframework.org/dtd/spring-beans.dtd">
<beans>
  <bean id="helloWorldAction" class="com.example.gnizr.HelloWorldAction" 
        singleton="false">
  </bean>        
</beans>
```

**File: `my-xwork.xml`**
```
<!DOCTYPE xwork PUBLIC "-//OpenSymphony Group//XWork 1.0//EN"
	"http://www.opensymphony.com/xwork/xwork-1.0.dtd">
<xwork>
  <package name="gnizr-helloworld" extends="gnizr-default"
	   namespace="/helloworld">		
    <action name="sayHello" class="com.example.gnizr.HelloWorldAction">			
      <interceptor-ref name="gnizrDefaultStack"/>
      <result name="success" type="freemarker">/helloworld/index.ftl</result>
      <result name="error" type="freemarker">/common/error.ftl </result>
    </action>		
  </package>
</xwork>
```

**File: `spring.xml`**
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN//EN"
        "http://www.springframework.org/dtd/spring-beans.dtd">
<beans>
	
  <!-- initial gnizr system configuration -->
  <!-- (copied from gnizr-webapp/src/main/resources as-is) -->
  <import resource="gnizr-config.xml"/>
	
  <!-- standard spring configuration of gnizr -->
  <!-- (copied from gnizr-webapp/src/main/resources as-is) -->
  <import resource="spring/gnizr-dao.xml"/>
  <import resource="spring/core-managers.xml"/>
  <import resource="spring/core-pagers.xml"/>
  <import resource="spring/core-tagging.xml"/>
  <import resource="spring/core-search.xml"/>
  <import resource="spring/core-bookmark.xml"/>
  <import resource="spring/web-interceptors.xml"/>	
  <import resource="spring/web-useradmin.xml"/>
  <import resource="spring/web-userbmark.xml"/>
  <import resource="spring/web-userfolder.xml"/>
  <import resource="spring/web-usertags.xml"/>
  <import resource="spring/web-userfeed.xml"/>
  <import resource="spring/web-links.xml"/>
  <import resource="spring/web-ui.xml"/>
  <import resource="spring/web-clustermap.xml"/>
  <import resource="spring/web-timeline.xml"/>
  <import resource="spring/web-search.xml"/>
  <import resource="classpath:com/gnizr/web/action/robot/rss/my-spring.xml"/>
	
  <!-- helloworld's spring config -->
  <import resource="classpath:com/example/gnizr/my-spring.xml"/>
</beans>
```

**File: `xwork.xml`**
```
<!DOCTYPE xwork PUBLIC "-//OpenSymphony Group//XWork 1.0//EN"
        "http://www.opensymphony.com/xwork/xwork-1.0.dtd">
<xwork>

  <!-- standard xwork configuration of gnizr -->
  <!-- (copied from gnizr-webapp/src/main/resources as-is) -->
  <include file="webwork-default.xml"/>   
  <include file="xwork/gnizr-default.xml"/>                           
  <include file="xwork/gnizr-userpage.xml"/>              
  <include file="xwork/gnizr-useradmin.xml"/>
  <include file="xwork/gnizr-bookmark.xml"/>
  <include file="xwork/gnizr-community.xml"/>
  <include file="xwork/gnizr-search.xml" />
  <include file="xwork/gnizr-admin.xml"/>
  <include file="xwork/gnizr-ui.xml"/>
  <include file="xwork/gnizr-settings.xml"/>
  <include file="xwork/gnizr-rss.xml"/>
  <include file="xwork/gnizr-rdf.xml"/>
  <include file="xwork/gnizr-json.xml"/>
  <include file="xwork/gnizr-clustermap.xml"/>
  <include file="xwork/gnizr-timeline.xml"/>
  <include file="com/gnizr/web/action/robot/rss/my-xwork.xml"/>       
	  
  <!-- helloworld's xwork configuration -->
  <include file="com/example/gnizr/my-xwork.xml"/>
</xwork>
```

## Step 5: Configure Tomcat's `server.xml` ##

Add the following `Context` definition in your Tomcat's `${tomcat_root}/conf/server.xml`:

```
<!-- gnizr HelloWorldWebApp demo -->

<Context path="/gnizr" reloadable="true" 
  docBase="C:\some\path\to\HelloWorldWebapp\src\main\webapp" 
  workDir="C:\some\path\to\HelloWorldWebapp\target\scratch">
</Context>
```

### Step 6: Overlay gnizr web application ###

Run the following command to overlay gnizr web application in `HelloWorldWebapp`:

```
mvn compile war:inplace
```

The above command compiles `HelloWorldAction.java` and does the [overlay](http://maven.apache.org/plugins/maven-war-plugin/overlays.html) procedure.

### Step 7: Start Tomcat in Eclipse ###

| **TIPS:** Make sure your Tomcat plugin is properly configured. In Eclipse, go to `Window` -> `Preferences...` -> `Tomcat`. Configure various [Tomcat parameters](http://www.flickr.com/photos/14804582@N08/2254700625/). |
|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|

![http://farm3.static.flickr.com/2255/2123545419_7651b7dabd_o.png](http://farm3.static.flickr.com/2255/2123545419_7651b7dabd_o.png)

Point your browser to

```
http://localhost:8080/gnizr/helloworld/sayHello.action
   or 
http://localhost:8080/gnizr/helloworld/sayHello.action?message=123abc
```

Done!