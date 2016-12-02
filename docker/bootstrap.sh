#!/bin/bash

MAIN_CLASS=${MAIN_CLASS:-net.onplatforms.platform.www.WwwSystem}

java -cp /opt/zero.jar -Dconfig.resource=www/application.conf ${MAIN_CLASS}