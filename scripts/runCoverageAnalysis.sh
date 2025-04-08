#!/bin/bash

systemName="$1"

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

mainClass="com.kuleuven.CoverageAnalysis.MissingTestFinder"
analysisStrategy="FULL"

echo "--- RUNNING GRAPH COVERAGE ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $analysisStrategy"