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
mkdir -p "data/$systemName/minimization"
mkdir -p "data/$systemName/metrics"
mkdir -p "data/$systemName/analysis"
mkdir -p "data/$systemName/graph"

# Call sub-scripts in order
sh scripts/getDependencies.sh "$systemName"
sh scripts/runGraphExtraction.sh "$systemName"
sh scripts/runCoverageAnalysis.sh "$systemName"
sh scripts/runMetricAnalysis.sh "$systemName"
sh scripts/runTestMinimization.sh "$systemName"
sh scripts/evaluateCoverage.sh "$systemName"

echo "Done!"