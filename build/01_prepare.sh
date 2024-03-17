#!/bin/bash

set -e
set -x

# cleanup
rm -rf lib
rm -rf ci-build

# build
mvn -B clean package --file pom.xml

# prepare directories
mkdir lib
mkdir ci-build
mkdir ci-build/portable

# get version
version=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate \
              -Dexpression=project.version -q -DforceStdout)

# create release
cp build/tinylog.release.properties lib/
cp oriedita/target/oriedita-"$version".jar lib/
dependencies=$(jdeps --print-module-deps --ignore-missing-deps lib/oriedita-"$version".jar)

echo "Required jlink modules: $dependencies"

jlink --add-modules "$dependencies" --output ci-build/runtime --strip-debug --strip-native-commands --no-header-files --no-man-pages --compress=2
