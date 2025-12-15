#!/bin/sh
docker run -d --name mysql -e MYSQL_ROOT_PASSWORD=123456 -p 3306:3306 -v /docker-data/mysql-data:/var/lib/mysql --restart=always mysql:8.4.6 --max-connections=1500 --lower_case_table_names=0
