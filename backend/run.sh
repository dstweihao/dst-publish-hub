#!/bin/bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PLAYWRIGHT_DOWNLOAD_HOST=https://npmmirror.com/mirrors/playwright/
export PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1
mvn clean spring-boot:run
