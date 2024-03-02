#!/bin/bash

version="$1"

mvn -B versions:set -DnewVersion="$version" --file pom.xml
