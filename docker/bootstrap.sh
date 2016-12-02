#!/bin/bash

MAIN_CLASS=${MAIN_CLASS:-net.onplatforms.platform.www.WwwSystem}

java -cp /opt/platform.jar -Dconfig.resource=www/application.conf ${MAIN_CLASS}