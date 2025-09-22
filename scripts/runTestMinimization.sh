#!/bin/bash

systemName="$1"

# This script ranks the tests in the system test suite according to Section 3.4 in the paper

if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

mainClass="com.kuleuven.TestMinimization.Main"
minimizationStrategy="$2"

echo "--- RUNNING TEST SUITE MINIMIZATION AND ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $minimizationStrategy"