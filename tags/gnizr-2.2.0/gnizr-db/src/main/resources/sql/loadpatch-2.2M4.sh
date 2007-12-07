#!/bin/bash
if [ -z "$1" -a -z "$2" -a -z "$3" ]; then
  echo usage: $0 [database] [mysql_user] [mysql_password]
  exit
fi 
DB=$1
USER=$2
PASS=$3
echo "DATABSE: $DB"
echo "USER: $USER"
echo "PASSWORD: $PASS"
echo "======= Load gnizr DB patch  ========"
echo "loading up-sql-2.2M4.sql into $DB"
mysql -u $USER --password=$PASS $DB < up-sql-2.2M4.sql
mysql -u $USER --password=$PASS $DB < up-sql-2.2M4.1.sql
mysql -u $USER --password=$PASS $DB < migrate-geotags.sql

./loadsp.sh $1 $2 $3