#!/bin/bash
mysqldump -u gnizr --password=gnizr --no-data=true gnizr_design > gnizr_db.sql
