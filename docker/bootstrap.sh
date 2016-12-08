#!/bin/bash

APP_PROFILE=${APP_PROFILE:-www}
CONFIG_RESOURCE=${CONFIG_RESOURCE:-${APP_PROFILE}/application.conf}
MAIN_CLASS=${MAIN_CLASS:-net.onplatforms.${APP_PROFILE}.Main}

java -cp /opt/zero.jar -XX:+UseG1GC -Xloggc:logs/gc.log -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:+UseGCLogFileRotation -XX:NumberOfGCLogFiles=10 -Dconfig.resource=${CONFIG_RESOURCE} ${MAIN_CLASS}