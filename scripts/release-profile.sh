#!/bin/bash

APP_PROFILE=$1
APP_VERSION=$2

docker push docker-registry.onplatforms.net/onplatforms.net/zero_${APP_PROFILE}:${APP_VERSION}
aws s3 sync ../static/dist/${APP_PROFILE}/ s3://cdn.onplatforms.net/${APP_PROFILE}/${APP_VERSION}/static/
aws s3 sync ../static/dist/${APP_PROFILE}/ s3://cdn.onplatforms.net/${APP_PROFILE}/latest/static/
sed -e "s/<APP_PROFILE>/${APP_PROFILE}/" -e "s/<APP_VERSION>/${APP_VERSION}/" ../README.md
