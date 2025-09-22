#!/bin/bash

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

      cd systems/"$systemName" || exit
      echo "--- RUNNING MUTATION TESTING ---"
      mvn -DtimeOutFactor=1.5 -Dthreads=8 -DwithHistory -DoutputFormats=CSV,HTML,XML -DfullMutationMatrix=true clean test org.pitest:pitest-maven:mutationCoverage

      report_dir="$currentDir/data/$systemName/metrics/pitest-report-full"
      mkdir -p "$report_dir"
      cp -r target/pit-reports/* "$report_dir"

      cd "$currentDir" || exit


      sh scripts/getDependencies.sh "$systemName"

      sh scripts/analyzeGraphFull.sh "$systemName"

      echo "--- RUNNING METRIC ANALYSIS WITH METRIC: $metric ---"
      bash scripts/runMetricAnalysis.sh "$systemName" "$metric"

      echo "--- RUNNING TEST MINIMIZATION with STRATEGY: $strategy ---"
      bash scripts/runTestMinimization.sh "$systemName" "$strategy"

      mkdir "$currentDir/data/$systemName/metrics/$strategy/$metric"
      cp "$currentDir/data/$systemName/minimization/minimizedTests.json" "$currentDir/data/$systemName/metrics/$strategy/$metric/minimizedTests.json"

      echo "--- CALCULATING APFD FOR $metric ---"
      python3 src/main/python/analyzeAPFD.py "$systemName"

    done

  done

done