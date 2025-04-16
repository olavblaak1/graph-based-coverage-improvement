#!/bin/bash

systemName="$1"

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

currentDir=$(pwd)

mainClass="com.kuleuven.TestMinimization.MarkReducedTestSuite"

echo "Marking reduced test suite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

echo "Running reduced test suite"
cd systems/$systemName || exit
mvn clean test -Dgroups=minimized
mvn jacoco:report
mkdir -p $currentDir/data/$systemName/metrics/jacoco-report-minimized
cp -r target/site/jacoco $currentDir/data/$systemName/metrics/jacoco-report-minimized

echo "Unmarking reduced test suite"
cd "$currentDir" || exit
mainClass="com.kuleuven.TestMinimization.UnmarkReducedTestSuite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

echo "EVALUATING COVERAGE OF FULL SUITE WITH MUTATION COVERAGE"
sh scripts/getMutationCoverage.sh "$systemName"