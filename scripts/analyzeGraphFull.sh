#!/bin/bash


# This script:
# 1. Initializes the data subdirectory for the given system, the repository of which is expected to:
#       1) Be placed in the systems/ directory
#       2) Have the usual Java project structure, with systems/$systemName/src/main/ pointing to the Java source code
#       and systems/$systemName/src/test pointing to its test suite(s)
# 2. Extracts the graph model from the system source code
# 3. Analyzes the test suite's coverage of the graph model, generating the function 𝑐 : 𝑉 ∪ 𝐸 → N as explained in
#    Section 3.3 of the paper, and outputs it to data/$systemName/analysis

set -e
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
echo "Done!"