#!/bin/bash

APP_PROFILE=$1
LISTEN_PORT=$2
APP_VERSION=$3

docker push docker-registry.onplatforms.net/onplatforms.net/${APP_PROFILE}:${APP_VERSION}
aws s3 sync ./static/dist/${APP_PROFILE}/ s3://cdn.onplatforms.net/${APP_PROFILE}/${APP_VERSION}/static/
aws s3 sync ./static/dist/${APP_PROFILE}/ s3://cdn.onplatforms.net/${APP_PROFILE}/latest/static/
sed -e "s/<APP_PROFILE>/${APP_PROFILE}/" \
    -e "s/<LISTEN_PORT>/${LISTEN_PORT}/" \
    -e "s/<APP_VERSION>/${APP_VERSION}/" ./README.md
