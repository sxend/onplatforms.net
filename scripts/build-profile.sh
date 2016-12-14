#!/bin/bash

APP_PROFILE=$1
APP_VERSION=$2

docker build -t docker-registry.onplatforms.net/onplatforms.net/zero_${APP_PROFILE}:${APP_VERSION} \
  --build-arg APP_PROFILE=${APP_PROFILE} --build-arg APP_VERSION=${APP_VERSION} -f ./docker/Dockerfile ./
