#!/bin/sh
docker run -d --name %projectName% -p %port%:%port% -e JAVA_OPTS='-Xms512m -Xmx1024m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m' -v /%projectName%/logs:/%projectName%/logs --restart=always %projectName%:%version%
