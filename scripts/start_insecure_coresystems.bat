@ECHO OFF

SET parent_path=%~dp0
cd %parent_path%

SET time_to_sleep=10

echo Starting Core Systems... Service initializations usually need ~ 20 seconds.

cd ..\serviceregistry_sql\target
START "" /B "cmd /c javaw -DarrowheadSystem=serviceregistry_sql -jar arrowhead-serviceregistry-sql-4.0.jar -d -daemon > insecure_sr.log 2>&1"
echo Service Registry started
timeout /t %time_to_sleep% /nobreak > NUL

cd ..\..\authorization\target
START "" /B "cmd /c javaw -jar -DarrowheadSystem=authorization arrowhead-authorization-4.0.jar -d -daemon > insecure_auth.log 2>&1"
echo Authorization started

cd ..\..\gateway\target
START "" /B "cmd /c javaw -jar -DarrowheadSystem=gateway arrowhead-gateway-4.0.jar -d -daemon > insecure_gateway.log 2>&1"
echo Gateway started

cd ..\..\eventhandler\target
START "" /B "cmd /c javaw -jar -DarrowheadSystem=eventhandler arrowhead-eventhandler-4.0.jar -d -daemon > insecure_eventhandler.log 2>&1"
echo Event Handler started

cd ..\..\gatekeeper\target
START "" /B "cmd /c javaw -jar -DarrowheadSystem=gatekeeper arrowhead-gatekeeper-4.0.jar -d -daemon > insecure_gk.log 2>&1"
echo Gatekeeper started

cd ..\..\orchestrator\target
START "" /B "cmd /c javaw -jar -DarrowheadSystem=orchestrator arrowhead-orchestrator-4.0.jar -d -daemon > insecure_orch.log 2>&1"
echo Orchestrator started

cd %parent_path%

::Kill self
title=arrowheadInsecureStarter
FOR /F "tokens=2" %%p in ('"tasklist /v /NH /FI "windowtitle eq arrowheadInsecureStarter""') DO taskkill /pid %%p > NUL 2>&1

