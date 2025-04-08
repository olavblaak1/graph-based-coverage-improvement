#!/bin/bash

systemName="$1"

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

mainClass="com.kuleuven.GraphAnalyzer.Main"
metric="HITS"

echo "--- RUNNING GRAPH METRIC ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $metric"