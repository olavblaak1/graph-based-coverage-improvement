#!/bin/bash

systemName="$1"

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

mainClass="com.kuleuven.GraphExtraction.ExtractGraph"
extractionMethod="FULL_GRAPH"

echo "--- RUNNING GRAPH EXTRACTION ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $extractionMethod"