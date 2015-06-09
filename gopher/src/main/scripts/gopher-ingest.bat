REM - This script requires a user name as an argument.
REM - The user name must match one of the directories under 'test-data/ingest'

REM - Create the 'gopher' database.
mysql --user=root < %PROJECT_DIR%\db_scripts\create-db.sql

REM - Run the XML ingest.
SET files=%PROJECT_DIR%\test-data\ingest;%PROJECT_DIR%\test-data\ingest\%1%
SET dtd=%PROJECT_DIR%\DTD\gopher-data.dtd
java -classpath %CLASSPATH% com.echostar.gopher.persist.util.GopherDataIngest %files% -dtd %dtd% %2%

REM - Generate the suite files.
java -classpath %CLASSPATH% com.echostar.gopher.testng.TestNGSuiteWriter
