
CD %~dp0openephyra-0.1.2-server
START "OpenEphyra" openephyra-0.1.2-server.bat
CD %~dp0

CD %~dp0iva-server
START "iva-server" gradlew bootRun
CD %~dp0

CD %~dp0iva-client
START "iva-client" gradlew bootRun
CD %~dp0
