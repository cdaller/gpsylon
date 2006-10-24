rem windows startscript for gpsylon

rem GPSYLON_VMARGS="-Xmx512m"

rem echo os: %OS%
rem echo cpu: %PROCESSOR_ARCHITECTURE%

rem find the driveletter/directory of the called command:
set PRG_DIR=%~d0%~p0
rem create the library path for the native libraries:
set LIBPATH=%PRG_DIR%lib\native\%OS%\%PROCESSOR_ARCHITECTURE%
set JAVA_LIB=-Djava.library.path=%LIBPATH%

set CLASSPATH="%PRG_DIR%gpsinput-0.5.3.jar;%PRG_DIR%lib/java/RXTXcomm.jar"

rem run the jar file:
java "%JAVA_LIB%" %GPSYLON_VMARGS% -cp %CLASSPATH% org.dinopolis.gpstool.gpsinput.SerialPortList

