@ECHO OFF

SET parent_path=%~dp0
cd %parent_path%

SET time_to_sleep=10

echo Starting Core Systems... Service initializations usually need ~ 20 seconds.

cd ..\serviceregistry_sql\target
START "serviceregistry_sql" /B "cmd /c java -DarrowheadSystem=serviceregistry_sql -jar arrowhead-serviceregistry-sql-4.0.jar -d -daemon -tls > secure_sr.log 2>&1"
echo Service Registry started
timeout /t %time_to_sleep% /nobreak > NUL

cd ..\..\authorization\target
START "" /B "cmd /c java -jar -DarrowheadSystem=authorization arrowhead-authorization-4.0.jar -d -daemon -tls > secure_auth.log 2>&1"
echo Authorization started

cd ..\..\gateway\target
START "" /B "cmd /c java -jar -DarrowheadSystem=gateway arrowhead-gateway-4.0.jar -d -daemon -tls > secure_gateway.log 2>&1"
echo Gateway started

cd ..\..\eventhandler\target
START "" /B "cmd /c java -jar -DarrowheadSystem=eventhandler arrowhead-eventhandler-4.0.jar -d -daemon -tls > secure_eventhandler.log 2>&1"
echo Event Handler started

cd ..\..\gatekeeper\target
START "" /B "cmd /c java -jar -DarrowheadSystem=gatekeeper arrowhead-gatekeeper-4.0.jar -d -daemon -tls > secure_gk.log 2>&1"
echo Gatekeeper started

cd ..\..\orchestrator\target
START "" /B "cmd /c java -jar -DarrowheadSystem=orchestrator arrowhead-orchestrator-4.0.jar -d -daemon -tls > secure_orch.log 2>&1"
echo Orchestrator started

cd %parent_path%
