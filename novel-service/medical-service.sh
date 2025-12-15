#!/bin/sh
if [ -n "$JAVA_OPTS" ]; then
  JAVA_OPTS="$JAVA_OPTS"
else
  JAVA_OPTS="-Xms512m -Xmx1024m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m"
fi
java -Xbootclasspath/a:$(
  cd $(dirname $0)
  pwd
) $JAVA_OPTS -Djava.security.egd=file:/dev/urandom -jar $(
  cd $(dirname $0)
  pwd
)/%jarName%
