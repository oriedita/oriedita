#!/bin/bash

platform="$1"

version=$(mvn org.apache.maven.plugins:maven-help-plugin:3.4.0:evaluate \
              -Dexpression=project.version -q -DforceStdout)

jpackage \
  @build/jpackage-common.txt \
  @build/jpackage-common-"$platform".txt \
  --dest ci-build/portable \
  --type app-image \
  --name "Oriedita" \
  --app-version "$version" \
  --main-jar oriedita-"$version".jar

pushd ci-build/portable || return
zip -r "../Oriedita Portable ($platform) $version.zip" .
popd || return

jpackage \
  @build/jpackage-common.txt \
  @build/jpackage-common-"$platform".txt \
  @build/jpackage-installer.txt \
  @build/jpackage-installer-"$platform".txt \
  --name "Oriedita" \
  --app-version "$version" \
  --main-jar oriedita-"$version".jar
