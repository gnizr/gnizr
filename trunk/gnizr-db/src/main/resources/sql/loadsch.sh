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
echo "-----------------"
rows=`mysql -u $USER -p${PASS} -e "show tables" $DB |wc -l`
if [ $rows -gt 0 ]
  then echo "Looks like the gnizr has already been installed."
       echo "Enter YES to continue (ALL EXISTING DATA WILL BE LOST!): "
       read input
       if [ "$input" != "YES" ] 
          then echo "Quit! No changes are made."
               exit 1
       fi 
fi 
echo "======= Create gnizr DB Schema =======" 
echo "loading gnizr_db.sql into $DB"
mysql -u $2 --password=$3 $DB < gnizr_db.sql
echo ""
echo "======= Load gnizr System Data  ========"
echo "loading mime_type.sql into $DB" 
mysql -u $2 --password=$3 $DB < mime_type.sql
echo "loading tag_prpt.sql into $DB" 
mysql -u $2 --password=$3 $DB < tag_prpt.sql
echo "loading admin-data.sql into $DB"
mysql -u $2 --password=$3 $DB < admin-data.sql
echo ""

./loadsp.sh $1 $2 $3
