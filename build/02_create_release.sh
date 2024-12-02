#!/bin/bash

set -e
set -x

platform="$1"
name="$2"
suffix="$3"

jar_version=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate \
              -Dexpression=project.version -q -DforceStdout)

# Strip off -SNAPSHOT as jpackage cannot handle it.
version="${jar_version/"-SNAPSHOT"/}"

jpackage \
  @build/jpackage-common.txt \
  @build/jpackage-common-"$platform".txt \
  --dest ci-build/portable \
  --type app-image \
  --name "Oriedita" \
  --app-version "$version" \
  --main-jar oriedita-"$jar_version".jar

pushd ci-build/portable || return
if ! type zip > /dev/null; then
  7z a -tzip "../Oriedita Portable ($name$suffix) $version.zip" .
else
  zip -r "../Oriedita Portable ($name$suffix) $version.zip" .
fi
popd || return

jpackage \
  @build/jpackage-common.txt \
  @build/jpackage-common-"$platform".txt \
  @build/jpackage-installer.txt \
  @build/jpackage-installer-"$platform".txt \
  --name "Oriedita$suffix" \
  --app-version "$version" \
  --main-jar oriedita-"$jar_version".jar
