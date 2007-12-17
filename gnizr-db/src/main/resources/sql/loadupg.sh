#!/bin/bash
if [ -z "$1" -a -z "$2" -a -z "$3" ]; then
    echo "usage: $0 [database] [mysql_user] [mysql_password]";
    exit
fi
DB=$1
USER=$2
PASS=$3
echo "DATABSE: $DB"
echo "USER: $USER"
echo "PASSWORD: $PASS"
echo "====== Upgrading databaes to 2.3.0 ======"
echo "  ==> Step 1: Reload Stored Procedures ...."
./loadsp.sh $1 $2 $3
echo "  ==> Upgrade Done!"
