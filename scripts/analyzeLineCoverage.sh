#!/bin/bash

systemName="$1"

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi


# Check if data/ directory exists
if [ ! -d "data" ]; then
  echo "Error: data/ directory does not exist."
  exit 1
fi
# Create necessary directories
mkdir -p "data/$systemName/minimization";
mkdir -p "data/$systemName/metrics";
mkdir -p "data/$systemName/analysis";
mkdir -p "data/$systemName/graph";

classPaths="target/classpath.txt"


currentDir=$(pwd)
cd systems/$systemName || exit

# Path to the pom.xml file
POM_FILE="pom.xml"

# Check if the pom.xml file exists
if [ ! -f "$POM_FILE" ]; then
  echo "Error: $POM_FILE does not exist."
  exit 1
fi

# Extract the JUnit version from the effective POM
JUnit_VERSION=$(mvn help:effective-pom | grep -A 1 '<artifactId>junit-jupiter-api</artifactId>' | grep '<version>' | awk -F '[<>]' '{print $3}')

# Check if the version was found
if [ -n "$JUnit_VERSION" ]; then
  echo "JUnit version: $JUnit_VERSION"
else
  JUnit_VERSION=$(mvn help:effective-pom | grep -A 1 '<artifactId>org-junit-vintage</artifactId>' | grep '<version>' | awk -F '[<>]' '{print $3}')
  if [ -n "$JUnit_VERSION" ]; then
    echo "JUnit version: $JUnit_VERSION"
  else
    echo "Error: JUnit version not found."
    exit 1
  fi

fi


OUTPUT_FILE="target/targetjars.txt"

echo "--- COMPILING PROJECT ---"
# Build all JARs (main, sources, test-sources, tests)
mvn clean install -DskipTests


echo "--- RETRIEVING DEPENDENCIES ---"
# Get all dependency jars
mvn dependency:build-classpath -Dmdep.outputFile=$classPaths

# Get target directory
TARGET_DIR=$(mvn help:evaluate -Dexpression=project.build.directory -q -DforceStdout)

# Find all JAR files in the target directory and format them as a colon-separated list
jarPaths=$(find "$TARGET_DIR" -name "*.jar" | tr '\n' ':' | sed 's/:$//')



# Save the formatted JAR paths to the output file
echo "$jarPaths" > "$OUTPUT_FILE"

# EVALUATION

echo "--- EVALUATING COVERAGE OF MINIMIZED SUITE WITH INDEPENDENT COVERAGE METRIC ---"
echo "Running original test suite"
mvn test
echo "Creating original independent coverage report"
mvn jacoco:report

mkdir -p $currentDir/data/$systemName/metrics/jacoco-report-original
cp -r target/site/jacoco $currentDir/data/$systemName/metrics/jacoco-report-original
cd "$currentDir" || exit

mainClass="com.kuleuven.TestMinimization.MarkReducedTestSuite"

echo "Marking reduced test suite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName" || exit

echo "Running reduced test suite"
cd systems/$systemName || exit
mvn clean test -Dgroups=minimized
mvn jacoco:report
mkdir -p $currentDir/data/$systemName/metrics/jacoco-report-minimized
cp -r target/site/jacoco $currentDir/data/$systemName/metrics/jacoco-report-minimized

echo "Unmarking reduced test suite"
cd "$currentDir" || exit
mainClass="com.kuleuven.TestMinimization.UnmarkReducedTestSuite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName" || exit

echo "Done!"


