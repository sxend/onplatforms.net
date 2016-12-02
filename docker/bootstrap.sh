#!/bin/bash

CONFIG_RESOURCE=${CONFIG_RESOURCE:-www/application.conf}
MAIN_CLASS=${MAIN_CLASS:-net.onplatforms.platform.www.WwwSystem}

java -cp /opt/zero.jar -Dconfig.resource=${CONFIG_RESOURCE} ${MAIN_CLASS}