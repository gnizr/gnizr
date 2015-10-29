This page describe the steps to build gnizr from SVN source code. Most users should download and install from [gnizr binary releases](HowToInstall.md).

If you have any questions, post them in [Gnizr User Discussion](http://groups.google.com/group/gnizr-users).

## Required Tools ##

Our build configuration requires the following tools already been installed:

  * [Apache Maven 2](http://maven.apache.org) (we use 2.0.7)
  * [Java SE Development Kit 6 (JDK 1.6)](http://www.javasoft.com)

## Step 1: Download Source Code ##

Follow these [instructions](http://code.google.com/p/gnizr/source).

Source code options:
  * `/trunk` -- the latest under active development; may not build or may fail tests.
  * `/branches` -- [release branches](http://svnbook.red-bean.com/en/1.1/ch04s04.html#svn-ch-4-sect-4.4.1); software under "maintenance" mode; should build and pass tests.
  * `/tags` -- tagged Stable Releases and Development Releases; should build and pass tests.

## Step 2: Prepare JUnit Test DB ##

Create a new MySQL database schema for testing. This schema should be created using the following configuration:

  * Database Name: `gnizr_test`
  * Database User: `gnizr`
  * Database Password: `gnizr`

**NOTE**: Make sure you grant `gnizr` user all proper [MySQL DB privileges](http://dev.mysql.com/doc/refman/5.1/en/privilege-system.html).

Run gnizr SQL script files:

  1. Go to the SQL script directory
    * `cd gnizr-db/src/main/resources/sql`.
  1. Run BASH script to setup database
    * `chmod 755 loadsch.sh loadsp.sh`
    * `./loadsch.sh gnizr_test gnizr gnizr`
  1. Done!

## Step 3: Compile Source Code ##

Assume that you've checked out the source code in a top-level directory called `gnizr`.

  1. Go to the top-level directory
    * `cd gnizr`
  1. Install the root project POM and update Maven plugins
    * `mvn -U -N install`
  1. Compile gnizr modules, run JUnit tests and assemble the application
    * `mvn clean install`
    * `mvn -Dmaven.test.skip=true package assembly:assembly`
  1. If you see no error messages, then the build is complete.
    * `cd gnizr/target`

In the `target` directory, you will find two different outputs of the same build: one in a uncompressed directory structure, and the other bundled in a ZIP file.