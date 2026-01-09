#!/bin/bash
cur_dir=`pwd`

docker stop caring_mysql
docker rm caring_mysql
docker run --name caring_mysql --restart=always \
    -v `pwd`/conf:/etc/mysql/conf.d \
    -v /data/docker-data/mysql8-data/:/var/lib/mysql \
    -p 3218:3306 \
    -e MYSQL_ROOT_PASSWORD="${MYSQL_ROOT_PASSWORD:-root}" \
    -e TZ=Asia/Shanghai \
    -d mysql:8.0.19
