#!/bin/bash

# This file assumes the rankings have been generated for each system, strategy and metric combination (with calculateAPFDMetrics.sh)
currentDir=$(pwd)

if [ "$#" -lt 1 ]; then
  echo "Usage: $0 <systemName1> [<systemName2> ...]"
  exit 1
fi

# Define metrics and importance strategies
metrics=("FAN_IN_AND_FAN_OUT" "NOOM" "HITS" "BETWEENNESS" "PAGERANK" "DIT" "FIELD_ACCESS")
strategies=("EXP_SCORE_BASED" "SCORE_BASED" "COMBINED")

for systemName in "$@"; do
  echo "--- PROCESSING SYSTEM: $systemName ---"
  for strategy in "${strategies[@]}"; do
    echo "--- STRATEGY: $strategy ---"
    for metric in "${metrics[@]}"; do


      echo "--- CALCULATING & WRITING APFD DATA FOR $metric ---"
      python3 src/main/python/analyzeAPFD.py "$systemName" "$strategy" "$metric"

    done
  done
done