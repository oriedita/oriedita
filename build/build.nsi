; Create an executable file with embedded jre
; Requires powershell and JDK

!execute 'pwsh.exe -File "./build.ps1"'

!define /file VERSION ../target/version

!include LogicLib.nsh
!include WinMessages.nsh
!include FileFunc.nsh

SilentInstall silent
RequestExecutionLevel user
ShowInstDetails hide

OutFile "..\target\OrigamiEditor-${VERSION}.exe"
Icon "origami-editor.ico"
VIProductVersion 1.0.0.00000
VIAddVersionKey ProductName "Origami Editor"
VIAddVersionKey LegalCopyright "Copyright (c) 2021"
VIAddVersionKey FileDescription "Origami Editor"
VIAddVersionKey FileVersion 1.0.0.00000
VIAddVersionKey ProductVersion "1.0 / OpenJRE 15.0.1 (x64)"
VIAddVersionKey InternalName "origami-editor"
VIAddVersionKey OriginalFilename "OrigamiEditor.exe"

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