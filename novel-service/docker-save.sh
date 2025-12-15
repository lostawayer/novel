#!/bin/sh
docker save %projectName%:%version% | gzip > %projectName%-%version%.tar.gz
