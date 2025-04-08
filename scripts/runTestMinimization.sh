#!/bin/bash

systemName="$1"

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

mainClass="com.kuleuven.TestMinimization.Main"
minimizationStrategy="STANDARD"

echo "--- RUNNING TEST SUITE MINIMIZATION AND ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $minimizationStrategy"