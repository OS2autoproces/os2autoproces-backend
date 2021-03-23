; This file is a script that allows to build the OrgSyncer instalation package
; To generate the installer, define the variable MyAppSourceDir MUST point to the Directory where the dll's should be copied from
; The script may be executed from the console-mode compiler - iscc "c:\isetup\samples\my script.iss" or from the Inno Setup Compiler UI
#define AppId "{{99fc06ec-8285-4bd1-a018-58c74fca4987}"
#define AppSourceDir "\\VBOXSVR\brian\projects\os2autoproces\OS2autoprocesImporter\OS2autoprocesImporter\bin\Release\"
#define AppName "OS2autoprocesImporter"
#define AppVersion "1.0"
#define AppPublisher "Digital Identity"
#define AppURL "http://digital-identity.dk/"
#define AppExeName "OS2autoprocesImporter.exe"

[Setup]
AppId={#AppId}
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher={#AppPublisher}
AppPublisherURL={#AppURL}
AppSupportURL={#AppURL}
AppUpdatesURL={#AppURL}
DefaultDirName={pf}\{#AppPublisher}\{#AppName}
DefaultGroupName={#AppName}
DisableProgramGroupPage=yes
SetupLogging=yes
OutputBaseFilename=OS2autoprocesImporter
Compression=lzma
SolidCompression=yes
SourceDir={#AppSourceDir}
OutputDir=..\..\..\Installer
SetupIconFile={#AppSourceDir}\..\..\..\Resources\di.ico
UninstallDisplayIcon={#AppSourceDir}\..\..\..\Resources\di.ico

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "Common.Logging.Core.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "Common.Logging.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "Log.config"; DestDir: "{app}"; Flags: ignoreversion
Source: "log4net.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "OS2autoprocesImporter.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "OS2autoprocesImporter.exe.config"; DestDir: "{app}"; Flags: ignoreversion
Source: "Quartz.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "RestSharp.dll"; DestDir: "{app}"; Flags: ignoreversion
Source: "TopShelf.dll"; DestDir: "{app}"; Flags: ignoreversion

[Icons]
Name: "{group}\{#AppName}"; Filename: "{app}\{#AppExeName}"; IconFilename: "{app}/di.ico";

[Run]
Filename: "{app}\OS2autoprocesImporter.exe"; Parameters: "install"

[UninstallRun]
Filename: "{app}\OS2autoprocesImporter.exe"; Parameters: "uninstall"
