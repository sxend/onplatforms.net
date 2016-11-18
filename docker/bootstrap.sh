#!/bin/bash

sudo -u memcache memcached -d

java -cp /opt/platform.jar $1