$java_version = jdeps -version

# Clean build the project
mvn -f .. -q clean package

# Obtain current version
$project_version =  mvn -f .. org.apache.maven.plugins:maven-help-plugin:3.2.0:evaluate -D"expression=project.version" -q -D"forceStdout"

echo $project_version > ../target/version

rm -r ../target/jre 2> $null

# Read dependencies of jar
$deps = jdeps --print-module-deps ../target/orihime-$project_version.jar

# Create a slimmed down jre
jlink --add-modules $deps --output ../target/jre --strip-debug --compress 2 --no-header-files --no-man-pages
