#!/bin/bash

APP_PROFILE=$1
LISTEN_PORT=$2
APP_VERSION=$3

sudo docker pull docker-registry.onplatforms.net/onplatforms.net/zero_${APP_PROFILE}:${APP_VERSION}

if [ "$(sudo docker ps -q --filter "NAME=${APP_PROFILE}.onplatforms.net" | wc -l)" -eq "1" ]; then
  sudo docker kill ${APP_PROFILE}.onplatforms.net
fi

if [ "$(sudo docker ps -a -q --filter "NAME=${APP_PROFILE}.onplatforms.net" | wc -l)" -eq "1" ]; then
  sudo docker rm ${APP_PROFILE}.onplatforms.net
fi

sudo docker run --name=${APP_PROFILE}.onplatforms.net -d -it -p ${LISTEN_PORT}:${LISTEN_PORT} \
    -e RDB_HOST=$(ip route get 8.8.8.8 | awk '{ print $NF; exit }') \
    -e RDB_USER=$(aws s3 cp s3://internal-storage.arimit.su/internal/datastore/rdb/user.name - ) \
    -e RDB_PASS=$(aws s3 cp s3://internal-storage.arimit.su/internal/datastore/rdb/user.pass - ) \
    -e PASSWORD_HASH_SALT=$(aws s3 cp s3://internal-storage.onplatforms.net/accounts.onplatforms.net/authentication/owned/password-hash-salt - ) \
    docker-registry.onplatforms.net/onplatforms.net/zero_${APP_PROFILE}:${APP_VERSION}
