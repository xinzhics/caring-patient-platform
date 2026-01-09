#!/bin/bash

docker stop caring_redis
docker rm caring_redis
docker run -idt -p 16379:16379 --name caring_redis --restart=always \
    -v `pwd`/redis.conf:/etc/redis/redis_default.conf \
    -v /data/docker-data/redis-data/:/data \
    -e TZ="Asia/Shanghai" \
    redis:4.0.12 redis-server /etc/redis/redis_default.conf --appendonly yes

