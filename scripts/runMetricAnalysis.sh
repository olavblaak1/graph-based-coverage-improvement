#!/bin/bash

systemName="$1"

# This script analyzes the system's test suite and graph model and assigns a value to each node of the graph model according
# to the chosen metric, constructing the function r: V -> R as explained in Section 3.2 of the paper, the supported
# metric options are:
#    1. FAN_IN_AND_FAN_OUT,
#    2. NOOM,
#    3. HITS,
#    4. BETWEENNESS,
#    5. PAGERANK,
#    6. OVERRIDES,
#    7. DIT,
#    8. DITANDHITS,
#    9. FIELD_ACCESS


if [ -z "$systemName" ]; then
  echo "Usage: $0 <systemName>"
  exit 1
fi

mainClass="com.kuleuven.GraphAnalyzer.Main"
metric="$2"

echo "--- RUNNING GRAPH METRIC ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $metric"

