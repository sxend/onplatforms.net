#!/bin/bash

APP_PROFILE=$1
LISTEN_PORT=$2
APP_VERSION=$3

docker build -t docker-registry.onplatforms.net/onplatforms.net/${APP_PROFILE}:${APP_VERSION} \
  --build-arg APP_PROFILE=${APP_PROFILE} \
  --build-arg LISTEN_PORT=${LISTEN_PORT} \
  --build-arg APP_VERSION=${APP_VERSION} \
  -f ./docker/Dockerfile ./
