@echo off
rem windows startscript for gpstool

rem GPSYLON_VMARGS="-Xmx512m"

rem echo os: %OS%
rem echo cpu: %PROCESSOR_ARCHITECTURE%

rem find the driveletter/directory of the called command:
set PRG_DIR=%~d0%~p0
rem create the library path for the native libraries:
set LIBPATH=%PRG_DIR%lib\native\%OS%\%PROCESSOR_ARCHITECTURE%
set JAVA_LIB=-Djava.library.path=%LIBPATH%

rem run the jar file:
rem c:\j2skd1.4.2_12\bin\java.exe -version
java "%JAVA_LIB%" %GPSYLON_VMARGS% -jar "%PRG_DIR%gpstool-0.5.3.jar" %*
pause

