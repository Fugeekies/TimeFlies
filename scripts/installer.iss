; Windows installer of TimeFlies (docs/ARCHITECTURE.md, "Distribution"), built by release.yml with Inno Setup as for
; LMU-DeltaRacing: no WiX. It packs the app folder made by `./gradlew :desktopApp:createDistributable` (jpackage
; app-image, with its own Java) and installs it for the current user, without administrator rights.
;
; Local build: iscc /DAppVersion=0.1.0 scripts\installer.iss (output in output\TimeFlies-Setup.exe).

#ifndef AppVersion
  #define AppVersion "0.0.0"
#endif

#define AppFolder "..\src\desktopApp\build\compose\binaries\main\app\TimeFlies"

[Setup]
; Never change the AppId: Windows recognises an update of the same application by it.
AppId={{B83805BE-517A-4DD6-B5CF-8600719E0F1E}
AppName=TimeFlies
AppVersion={#AppVersion}
AppVerName=TimeFlies {#AppVersion}
AppPublisher=Fugeekies
AppPublisherURL=https://github.com/Fugeekies/TimeFlies
AppSupportURL=https://github.com/Fugeekies/TimeFlies/issues
DefaultDirName={localappdata}\Programs\TimeFlies
DefaultGroupName=TimeFlies
DisableProgramGroupPage=yes
SetupIconFile=..\assets\icon\timeflies.ico
UninstallDisplayIcon={app}\TimeFlies.exe
UninstallDisplayName=TimeFlies
VersionInfoVersion={#AppVersion}
OutputDir=..\output
OutputBaseFilename=TimeFlies-Setup
ArchitecturesAllowed=x64compatible
ArchitecturesInstallIn64BitMode=x64compatible
PrivilegesRequired=lowest
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
; TimeFlies may be running in the notification area: the setup closes it before replacing its files.
CloseApplications=yes
RestartApplications=no
ShowLanguageDialog=no

[Languages]
; English only (decision of 17/11/2026): the app itself starts in the Windows language when it has it
; (Settings › Affichage › Langue, milestone 17).
Name: "en"; MessagesFile: "compiler:Default.isl"

[Tasks]
; Messages of the Inno Setup language files: "Create a desktop shortcut", "Additional shortcuts:".
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
; ignoreversion: an update always replaces every file of the previous version.
Source: "{#AppFolder}\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[InstallDelete]
; Files of an older version that the new one no longer ships (libraries renamed by a new version).
Type: filesandordirs; Name: "{app}\app"
Type: filesandordirs; Name: "{app}\runtime"

[Icons]
Name: "{group}\TimeFlies"; Filename: "{app}\TimeFlies.exe"; WorkingDir: "{app}"
Name: "{autodesktop}\TimeFlies"; Filename: "{app}\TimeFlies.exe"; WorkingDir: "{app}"; Tasks: desktopicon

[Registry]
; The app writes this value itself ("Lancer TimeFlies au démarrage de Windows"); the uninstaller removes it,
; without the setup ever creating it.
Root: HKCU; Subkey: "Software\Microsoft\Windows\CurrentVersion\Run"; ValueType: none; ValueName: "TimeFlies"; Flags: uninsdeletevalue dontcreatekey

[Run]
Filename: "{app}\TimeFlies.exe"; Description: "{cm:LaunchProgram,TimeFlies}"; Flags: postinstall nowait skipifsilent

; The settings in %APPDATA%\TimeFlies stay after an uninstall: a new installation finds them again
; (decision of 13/11/2026, docs/ARCHITECTURE.md, "Réglages").
