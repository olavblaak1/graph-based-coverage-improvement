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

sh scripts/getDependencies.sh $systemName

mkdir -p "data/$systemName/missing_tests";

mvn exec:java -Dexec.mainClass="com.kuleuven.MissingTestIdentification.Main" -Dexec.args="$systemName"

echo "Done!"


