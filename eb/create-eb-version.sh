#!/bin/bash

APP_PROFILE=$1
APP_VERSION=$2

ARCHIVE_FILE_NAME=zero.zip
S3KEY=zero/${APP_VERSION}/eb/${ARCHIVE_FILE_NAME}

# Create ElasticBeanstalk version file by zip command
cd eb/
sed -i "s/<APP_VERSION>/${APP_VERSION}/g" Dockerrun.aws.json
sed -i "s/<APP_VERSION>/${APP_VERSION}/g" .ebextensions/100_load_image.config
zip ${ARCHIVE_FILE_NAME} --recurse-paths .ebextensions Dockerrun.aws.json
aws s3 cp ${ARCHIVE_FILE_NAME} s3://internal-storage.onplatforms.net/${S3KEY}

# Call ElasticBeanstalk createApplicationVersion API
aws elasticbeanstalk create-application-version \
  --application-name ${APP_PROFILE}.onplatforms.net \
  --source-bundle S3Bucket=internal-storage.onplatforms.net,S3Key=${S3KEY} \
  --version-label ${APP_VERSION} \
  --region ap-northeast-1
