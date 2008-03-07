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
echo "====== Load Stored Procedures ========"
echo "loading sp-user.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-user.sql

echo "loading sp-tag.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-tag.sql

echo "loading sp-link.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-link.sql

echo "loading sp-bookmark.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-bookmark.sql

echo "loading sp-tagprpt.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-tagprpt.sql

echo "loading sp-tagassertion.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-tagassertion.sql

echo "loading sp-subscriptions.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-subscriptions.sql

echo "loading sp-foruser.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-foruser.sql

echo "loading sp-folder.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-folder.sql

echo "loading sp-geommarker.sql into $DB"
mysql -u $2 --password=$3 $DB < sp-geommarker.sql

