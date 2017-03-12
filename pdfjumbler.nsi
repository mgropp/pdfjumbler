;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; IMPORTANT:                                     ;;
;; Build fat jars of the plugins (or use gradle)! ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

!include "MUI2.nsh"
Name "PdfJumbler"
OutFile "build\setup-pdfjumbler.exe"
InstallDir "$LOCALAPPDATA\PdfJumbler"
InstallDirRegKey HKCU "Software\PdfJumbler" ""
CRCCheck on
RequestExecutionLevel user

Var StartMenuFolder

!define MUI_ABORTWARNING

!insertmacro MUI_PAGE_LICENSE "LICENSE"
!insertmacro MUI_PAGE_DIRECTORY

!define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU" 
!define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\PdfJumbler" 
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
!insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder

!insertmacro MUI_PAGE_INSTFILES

!insertmacro MUI_UNPAGE_CONFIRM
!insertmacro MUI_UNPAGE_INSTFILES

!insertmacro MUI_LANGUAGE "English"

Function .onInit
	Var /GLOBAL JavaExecutable
	Push $R0
	Call GetJRE
	Pop $R0
	StrCpy $JavaExecutable $R0
	Pop $R0
FunctionEnd

Section "PdfJumbler Main" PdfJumblerMain
	SetOutPath "$INSTDIR"
	File /oname=pdfjumbler.jar "build\libs\pdfjumbler-noplugins.jar"
	File "src\main\resources\pdfjumbler.ico"
	File /oname=license.txt "LICENSE"
	File "build\libs\pdfjumbler-itext.jar"
	File "build\libs\pdfjumbler-jpedal.jar"
	;File "build\libs\pdfjumbler-pdfbox.jar"
	;File "build\libs\pdfjumbler-jpod.jar"
	File /oname=readme.txt "README.md"
	
	WriteRegStr HKCU "Software\PdfJumbler" "" $INSTDIR
  
	;Create uninstaller
	WriteUninstaller "$INSTDIR\Uninstall.exe"

	!insertmacro MUI_STARTMENU_WRITE_BEGIN Application

	;Create shortcuts
	CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
	CreateShortCut "$SMPROGRAMS\$StartMenuFolder\PdfJumbler.lnk" "$JavaExecutable" '-jar "$INSTDIR\pdfjumbler.jar"' "$INSTDIR\pdfjumbler.ico"
	CreateShortCut "$SMPROGRAMS\$StartMenuFolder\Uninstall PdfJumbler.lnk" "$INSTDIR\Uninstall.exe"
	CreateShortCut "$SMPROGRAMS\$StartMenuFolder\readme.txt.lnk" "$INSTDIR\readme.txt"

	!insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section "Uninstall"
	Delete "$INSTDIR\pdfjumbler.jar"
	Delete "$INSTDIR\pdfjumbler.ico"
	Delete "$INSTDIR\LICENSE.txt"
	Delete "$INSTDIR\pdfjumbler-itext.jar"
	Delete "$INSTDIR\pdfjumbler-jpedal.jar"
	;Delete "$INSTDIR\pdfjumbler-pdfbox.jar"
	;Delete "$INSTDIR\pdfjumbler-jpod.jar"
	Delete "$INSTDIR\readme.txt"

	Delete "$INSTDIR\Uninstall.exe"

	RMDir "$INSTDIR"

	!insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder

	Delete "$SMPROGRAMS\$StartMenuFolder\readme.txt.lnk"
	Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall PdfJumbler.lnk"
	Delete "$SMPROGRAMS\$StartMenuFolder\PdfJumbler.lnk"
	RMDir "$SMPROGRAMS\$StartMenuFolder"

	DeleteRegKey /ifempty HKCU "Software\PdfJumbler"
SectionEnd


Function GetJRE
;
;  returns the full path of a valid java.exe
;  looks in:
;  1 - .\jre directory (JRE Installed with application)
;  2 - JAVA_HOME environment variable
;  3 - the registry
;  4 - hopes it is in current dir or PATH

  Push $R0
  Push $R1

  !define JAVAEXE "javaw.exe"
 
  ClearErrors
  StrCpy $R0 "$EXEDIR\jre\bin\${JAVAEXE}"
  IfFileExists $R0 JreFound  ;; 1) found it locally
  StrCpy $R0 ""
 
  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
  IfErrors 0 JreFound  ;; 2) found it in JAVA_HOME
 
  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\${JAVAEXE}"
 
  IfErrors 0 JreFound  ;; 3) found it in the registry

  MessageBox MB_YESNOCANCEL "No Java Runtime Environment Found. Download?" IDYES Download IDCANCEL GiveUp
  
  StrCpy $R0 "${JAVAEXE}"  ;; 4) wishing you good luck
  goto JreFound

  Download:
  ExecShell "open" "http://www.java.com"

  GiveUp:
  Abort

  JreFound:
  Pop $R1
  Exch $R0
FunctionEnd
