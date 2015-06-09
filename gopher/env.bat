SET PROJECT_DIR=%CD%
#SET ANT_HOME=C:\Users\Charles.Young\Downloads\apache-ant-1.9.4-bin\apache-ant-1.9.4
SET PATH=%PROJECT_DIR%\scripts\windows;^
%ANT_HOME%\bin;^
%PATH%
SET CONFIG_DIR=%PROJECT_DIR%\config
SET JARS=%PROJECT_DIR%\jars-core
SET CLASSPATH=%PROJECT_DIR%\bin;^
%CONFIG_DIR%;^
%ANT_HOME%\lib\ant.jar;^
%JARS%\antlr-2.7.7.jar;^
%JARS%\dom4j-1.6.1.jar;^
%JARS%\hibernate-commons-annotations-4.0.5.Final.jar;^
%JARS%\hibernate-core-4.3.6.Final.jar;^
%JARS%\hibernate-jpa-2.1-api-1.0.0.Final.jar;^
%JARS%\jandex-1.1.0.Final.jar;^
%JARS%\javassist-3.18.1-GA.jar;^
%JARS%\jboss-logging-3.1.3.GA.jar;^
%JARS%\jboss-logging-annotations-1.2.0.Beta1.jar;^
%JARS%\jboss-transaction-api_1.2_spec-1.0.0.Final.jar;^
%JARS%\log4j-1.2.14.jar;^
%JARS%\mysql-connector-java-5.1.31-bin.jar;^
%JARS%\poi-3.6-20091214.jar;^
%JARS%\poi-ooxml-3.6-20091214.jar;^
%JARS%\selenium-server-standalone-2.41.0.jar