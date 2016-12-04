#!/bin/bash

APP_PROFILE=${APP_PROFILE:-www}
CONFIG_RESOURCE=${CONFIG_RESOURCE:-${APP_PROFILE}/application.conf}
MAIN_CLASS=${MAIN_CLASS:-net.onplatforms.platform.${APP_PROFILE}.Main}

java -cp /opt/zero.jar -Dconfig.resource=${CONFIG_RESOURCE} ${MAIN_CLASS}