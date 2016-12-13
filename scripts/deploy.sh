#!/bin/bash

APP_PROFILE=$1

APP_VERSION=$2

sudo docker pull docker-registry.onplatforms.net/onplatforms.net/zero_${APP_PROFILE}:${APP_VERSION}

if [ "$(sudo docker ps -q --filter "NAME=${APP_PROFILE}.onplatforms.net" | wc -l)" -eq "1" ]; then
  sudo docker kill ${APP_PROFILE}.onplatforms.net
fi

if [ "$(sudo docker ps -a -q --filter "NAME=${APP_PROFILE}.onplatforms.net" | wc -l)" -eq "1" ]; then
  sudo docker rm ${APP_PROFILE}.onplatforms.net
fi

sudo docker run --name=${APP_PROFILE}.onplatforms.net -d -it -p 9090:9090 \
  docker-registry.onplatforms.net/onplatforms.net/zero_${APP_PROFILE}:${APP_VERSION}
