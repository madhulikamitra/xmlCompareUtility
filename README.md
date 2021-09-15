# xmlCompareUtility
To compare two xml files and list their differences

This is a simple utility which will compare 2 xmls.

Steps to use the project:

1. Do a git clone :https://github.com/madhulikamitra/xmlCompareUtility
2. The source and target xml files are already placed in teh src/test/resources folder of the project
3. The xml file paths are configurable from config properties also present in src/test/resources
4. You can either import the maven project in an IDE of your choice or build it from command line
5. To execute the test use the command
mvn install
mvn test -Dtest=compareXML
6. The report after the test run is available at /target/Spark.html
