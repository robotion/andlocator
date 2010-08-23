#!/bin/sh

mvn grails:exec -Dcommand=app-engine -Dargs=package  && $APPENGINE_HOME/bin/appcfg.sh update target/war
