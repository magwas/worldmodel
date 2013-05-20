#!/bin/bash
curl -T target/worldmodel.war "https://tomcat.realm:8443/manager/text/deploy?path=/worldmodel&update=true"
