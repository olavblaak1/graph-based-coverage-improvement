systemName="joda-time"

# Check if data/ directory exists
if [ ! -d "data" ]; then
  echo "Error: data/ directory does not exist."
  exit 1
fi

# Create necessary directories
mkdir -p "data/$systemName/minimization";
mkdir -p "data/$systemName/metrics";
mkdir -p "data/$systemName/analysis";
mkdir -p "data/$systemName/graph";

# GRAPH EXTRACTION
mainClass="com.kuleuven.GraphExtraction.ExtractGraph"
extractionMethod="FULL_GRAPH"
classPaths="systems/$systemName/target/classpath.txt"

mvn dependency:build-classpath -Dmdep.outputFile=$classPaths
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $extractionMethod"

# COVERAGE ANALYSIS
mainClass="com.kuleuven.CoverageAnalysis.MissingTestFinder"
analysisStrategy="FULL"

mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $analysisStrategy"

# METRIC ANALYSIS
mainClass="com.kuleuven.GraphAnalyzer.Main"
metric="FAN_IN_AND_FAN_OUT"

mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $metric"

# MISSING TEST IDENTIFICATION
mainClass="com.kuleuven.TestMinimization.Main"

minimizationStrategy="STANDARD"

mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $minimizationStrategy"