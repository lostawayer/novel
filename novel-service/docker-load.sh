#!/bin/sh
gunzip -c %projectName%-%version%.tar.gz | docker load
