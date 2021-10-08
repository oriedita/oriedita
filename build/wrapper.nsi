; Create an executable wrapper for the jar, requires a recent version of java to be installed

!define /file VERSION ../target/version

!include LogicLib.nsh
!include WinMessages.nsh
!include FileFunc.nsh

; Custom MessageBox macro with title
!macro MsgBox out text title flags
   System::Call "user32::MessageBox(i $HWNDPARENT, t '${text}', t '${title}', i ${flags}) i.s"
   Pop ${out}
!macroend

SilentInstall silent
RequestExecutionLevel user
ShowInstDetails hide

OutFile "..\target\origami-editor-${VERSION}.exe"
Icon "logo.ico"
VIProductVersion 0.0.0.0
VIAddVersionKey ProductName "Origami Editor"
VIAddVersionKey LegalCopyright "Copyright (c) 2021"
VIAddVersionKey FileDescription "Origami Editor"
VIAddVersionKey FileVersion 0.0.0.0
VIAddVersionKey ProductVersion "${VERSION}"
VIAddVersionKey InternalName "origami-editor"
VIAddVersionKey OriginalFilename "origami-editor.exe"

Section
  SetOverwrite off

  InitPluginsDir
  SetOutPath $PluginsDir
  File "..\target\origami-editor-${VERSION}.jar"
  ${GetParameters} $R0

  nsExec::ExecToStack '"javaw.exe" -Dfile.encoding="UTF-8" -jar $PluginsDir\origami-editor-${VERSION}.jar $R0'
  Pop $0
  Pop $1

  ${If} $0 == "error" ; Likely command not found
    !insertmacro MsgBox $0 "Java not found! Download the latest version (at least 11) from adoptium.net or download a version of Origami Editor with a pre-packaged Java installation." "Error" 0x10
  ${ElseIf} $0 == 1 ; Java errored, probably version too old
    !insertmacro MsgBox $0 "$1 $0 Java started but exited with an error. If Origami Editor did not start, the installed Java version is probably too old. Download the latest version (at least 11) from adoptium.net or download a version of Origami Editor with a pre-packaged Java installation." "Error" 0x10
  ${EndIf}
  RMDir /r $PluginsDir
SectionEnd