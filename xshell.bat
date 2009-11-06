@echo off

rem TODO: If standalone jar exists, start it, if not, display message telling user to run maven.

java -jar xshell-core\target\XShell-1.0-SNAPSHOT-standalone.jar -p jndi -name jmx/rmi/RMIAdaptor
