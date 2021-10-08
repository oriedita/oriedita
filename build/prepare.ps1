# Clean build the project
# Outputs version, java_version and install_size to the target directory for use in nsis files.

$root = Resolve-Path $PSScriptRoot\..
$target = "$root\target"
$jre = "$target\jre"

mvn -f $root -q clean package > $null

# Obtain current versions
$project_version = mvn -f $root help:evaluate -D"expression=project.version" -q -D"forceStdout"
echo $project_version > $target\version
jdeps -version > $target\java_version

rm -r $target\jre 2> $null

# Read dependencies of jar
$deps = jdeps --print-module-deps --ignore-missing-deps $target\origami-editor-$project_version.jar

# Create a slimmed down jre
jlink --add-modules $deps --output $jre --strip-debug --compress 2 --no-header-files --no-man-pages

# Calculate the size of the installation
[int][System.Math]::Round((((Get-ChildItem -Recurse $jre | Measure-Object -Property Length -Sum).Sum)/1KB),2) `
+ [int][System.Math]::Round((((Get-ChildItem $target\origami-editor-$project_version.jar).Length)/1KB),2) `
  > $target\install_size