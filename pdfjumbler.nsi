!include "MUI2.nsh"
!include "FileFunc.nsh"
Name "PdfJumbler"
OutFile "build\setup-pdfjumbler.exe"
Unicode true
InstallDir "$LOCALAPPDATA\PdfJumbler"
InstallDirRegKey HKCU "Software\PdfJumbler" ""
CRCCheck on
RequestExecutionLevel user

Var StartMenuFolder

!define MUI_CUSTOMFUNCTION_GUIINIT jreGuiInit
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
!insertmacro MUI_LANGUAGE "German"
!insertmacro MUI_LANGUAGE "Spanish"

LangString INSTALL_JAVA_QUESTION ${LANG_ENGLISH} "No Java Runtime Environment (version 21 or above) found. Download?"
LangString INSTALL_JAVA_QUESTION ${LANG_GERMAN} "Kein Java-Runtime-Environment (Version 21 oder neuer) gefunden. Herunterladen?"
LangString INSTALL_JAVA_QUESTION ${LANG_SPANISH} "No se encontró ningún entorno de ejecución Java (versión 21 o superior). ¿Descargar?"

Function .onInit
	StrCpy $Language ${LANG_ENGLISH}
	!insertmacro MUI_LANGDLL_DISPLAY
FunctionEnd

Function jreGuiInit
	Var /GLOBAL JavaExecutable
	Push $R0
	Call GetJRE
	Pop $R0
	StrCpy $JavaExecutable $R0
	Pop $R0
FunctionEnd

Section "PdfJumbler Main" PdfJumblerMain
	SetOutPath "$INSTDIR"
	File "build\libs\pdfjumbler.jar"
	File "src\main\resources\icons\pdfjumbler.ico"
	File /oname=license.txt "LICENSE"
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


!define JAVAEXE "javaw.exe"
!define MIN_JAVA_VERSION 21

Function JavawInJavaHome
	Exch $R2
	Push $R1
	Push $R3
	Push $R4
	StrCpy $R0 ""

	StrCpy $R4 $R2 1 -1
	StrCmp $R4 "\" 0 +2
	StrCpy $R2 $R2 -1

	IfFileExists "$R2\bin\${JAVAEXE}" 0 done
	ClearErrors
	FileOpen $R3 "$R2\release" r
	IfErrors done

	readLine:
		ClearErrors
		FileRead $R3 $R1
		IfErrors close
		StrCpy $R4 $R1 14
		StrCmp $R4 'JAVA_VERSION="' 0 readLine
		StrCpy $R1 $R1 "" 14
		IntCmp $R1 ${MIN_JAVA_VERSION} found close found

	found:
		StrCpy $R0 "$R2\bin\${JAVAEXE}"

	close:
		FileClose $R3

	done:
	Pop $R4
	Pop $R3
	Pop $R1
	Pop $R2
FunctionEnd

Function JavawFromJavaSoftKey
	Exch $R2
	Push $R1
	StrCpy $R0 ""

	ClearErrors
	ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\$R2" "CurrentVersion"
	IfErrors done
	ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\$R2\$R1" "JavaHome"
	IfErrors done
	Push $R1
	Call JavawInJavaHome

	done:
	Pop $R1
	Pop $R2
FunctionEnd

Function JavawFromAdoptiumKey
	Exch $R2
	Push $R1
	Push $R3
	StrCpy $R3 0

	nextVersion:
		StrCpy $R0 ""
		ClearErrors
		EnumRegKey $R1 HKLM "SOFTWARE\Eclipse Adoptium\$R2" $R3
		IfErrors done
		StrCmp $R1 "" done
		IntOp $R3 $R3 + 1
		ReadRegStr $R1 HKLM "SOFTWARE\Eclipse Adoptium\$R2\$R1\hotspot\MSI" "Path"
		StrCmp $R1 "" nextVersion
		Push $R1
		Call JavawInJavaHome
		StrCmp $R0 "" nextVersion

	done:
	Pop $R3
	Pop $R1
	Pop $R2
FunctionEnd

Function GetJRE
	;  returns the full path of a valid java.exe
	;  looks in:
	;  1 - .\jre directory (JRE Installed with application)
	;  2 - JAVA_HOME environment variable
	;  3 - the registry
	;  4 - hopes it is in current dir or PATH
	Push $R0
	Push $R1

	Push "$EXEDIR\jre"
	Call JavawInJavaHome
	StrCmp $R0 "" 0 JreFound  ;; 1) found it locally

	ReadEnvStr $R1 "JAVA_HOME"
	StrCmp $R1 "" registry
	Push $R1
	Call JavawInJavaHome
	StrCmp $R0 "" 0 JreFound  ;; 2) found it in JAVA_HOME

	registry:
	SetRegView 64
	Push "JDK"
	Call JavawFromJavaSoftKey
	StrCmp $R0 "" 0 registryDone
	Push "JRE"
	Call JavawFromJavaSoftKey
	StrCmp $R0 "" 0 registryDone
	Push "Java Runtime Environment"
	Call JavawFromJavaSoftKey
	StrCmp $R0 "" 0 registryDone
	Push "JRE"
	Call JavawFromAdoptiumKey
	StrCmp $R0 "" 0 registryDone
	Push "JDK"
	Call JavawFromAdoptiumKey
	registryDone:
	SetRegView default
	StrCmp $R0 "" 0 JreFound  ;; 3) found it in the registry

	ClearErrors
	SearchPath $R1 "${JAVAEXE}"
	IfErrors askDownload
	${GetParent} $R1 $R1
	${GetParent} $R1 $R1
	Push $R1
	Call JavawInJavaHome
	StrCmp $R0 "" 0 JreFound

	askDownload:
	MessageBox MB_YESNOCANCEL $(INSTALL_JAVA_QUESTION) IDYES Download IDCANCEL GiveUp

	StrCpy $R0 "${JAVAEXE}"  ;; 4) wishing you good luck
	goto JreFound

	Download:
	ExecShell "open" "https://adoptium.net/"

	GiveUp:
	Abort

	JreFound:
	Pop $R1
	Exch $R0
FunctionEnd

Function un.onInit
	!insertmacro MUI_UNGETLANGUAGE  
FunctionEnd
