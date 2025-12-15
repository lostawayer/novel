@echo off
title %projectName%
java -Xbootclasspath/a:./ -Xms512m -Xmx1024m -Xmn256m -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=256m -jar %jarName%
