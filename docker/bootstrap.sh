#!/bin/bash

CONFIG_RESOURCE=${CONFIG_RESOURCE:-${APP_PROFILE}/application.conf}
MAIN_CLASS=${MAIN_CLASS:-net.onplatforms.${APP_PROFILE}.Main}

mkdir -p logs

java -cp /opt/app.jar -XX:+UseG1GC -Xloggc:logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Dconfig.resource=${CONFIG_RESOURCE} ${MAIN_CLASS}