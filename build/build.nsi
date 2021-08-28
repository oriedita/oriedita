; Create an executable file with embedded jre
; Requires powershell and JDK

!execute 'pwsh.exe -File "./build.ps1"'

!define /file VERSION ../target/version
!define /file JAVA_VERSION ../target/java_version

!include LogicLib.nsh
!include WinMessages.nsh
!include FileFunc.nsh

SilentInstall silent
RequestExecutionLevel user
ShowInstDetails hide

OutFile "..\target\origami-editor-${VERSION}.exe"
Icon "origami-editor.ico"
VIProductVersion ${VERSION}.0
VIAddVersionKey ProductName "Origami Editor"
VIAddVersionKey LegalCopyright "Copyright (c) 2021"
VIAddVersionKey FileDescription "Origami Editor"
VIAddVersionKey FileVersion ${VERSION}.0
VIAddVersionKey ProductVersion "${VERSION} / OpenJRE ${JAVA_VERSION} (x64)"
VIAddVersionKey InternalName "origami-editor"
VIAddVersionKey OriginalFilename "origami-editor.exe"

Section
  SetOverwrite off

  SetOutPath "$TEMP\jre"
  File /r "..\target\jre\*"

  InitPluginsDir
  SetOutPath $PluginsDir
  File "..\target\origami-editor-${VERSION}.jar"
  SetOutPath $TEMP
  ${GetParameters} $R0
  nsExec::Exec '"$TEMP\jre\bin\java.exe" -jar $PluginsDir\origami-editor-${VERSION}.jar $R0'
  RMDir /r $PluginsDir
SectionEnd