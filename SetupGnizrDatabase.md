This page describes the steps to prepare MySQL database for gnizr installation. These steps must be performed before running the gnizr web application.

## Requirement ##

  1. Install MySQL 5.0
  1. Create a database schema for gnizr
    * e.g., `gnizr_db`
  1. Create a database account for gnizr, and make sure this account has all the necessary privileges to manage and access the gnizr database schema
    * e.g., username: `gnizr`, password: `gnizrpass`
  1. Download and unpack a gnizr release

Note: We assume that you have properly installed MySQL database. If you need addition help on using MySQL, see http://dev.mysql.com

## Create database tables and stored procedures ##

  1. Go to the SQL script directory
    * `cd gnizr-x.y.z/sql`
  1. Run BASH script to setup database
    * `chmod 755 loadsch.sh loadsp.sh`
    * `./loadsch.sh gnizr_db gnizr gnizrpass`
    * syntax: `loadsch [db_name] [db_username] [db_password]`
  1. Done!


## Upgrade database tables and stored procedures ##

Instructions described in this section are for upgrading gnizr software from an existing installation. If you install gnizr for the first time, follow instructions in "_Create database tables and stored procedures_".

### Upgrading to gnizr 2.3.0 ###

If you are currently running gnizr 2.2.x or 2.3.0 milestones, do the following:

  1. **Backup your existing database** -- always a good idea :-)
  1. Go to the SQL script directory
    * `cd gnizr-2.3.0/sql`
  1. Run BASH script to upgrade database
    * `chmod 755 loadsch.sh loadsp.sh loadupg.sh`
    * syntax: `loadupg [db_name] [db_username] [db_password]`
      * e.g., `./loadupg.sh gnizr_db gnizr gnizrpass`
  1. Done!

## Related ##
  * HowToInstall
  * HowToBuild