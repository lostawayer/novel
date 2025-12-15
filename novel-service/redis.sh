#!/bin/sh
docker run -d --name redis -p 6379:6379 -v /docker-data/redis-data:/data --restart=always redis:7.4.5 redis-server /data/redis.conf
