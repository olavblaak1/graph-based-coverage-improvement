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
mkdir -p "data/$systemName/minimization";
mkdir -p "data/$systemName/metrics";
mkdir -p "data/$systemName/analysis";
mkdir -p "data/$systemName/graph";


sh scripts/getDependencies.sh $systemName

currentDir=$(pwd)


# GRAPH EXTRACTION
mainClass="com.kuleuven.GraphExtraction.ExtractGraph"
extractionMethod="FULL_GRAPH"


echo "--- RUNNING GRAPH EXTRACTION ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $extractionMethod"

# COVERAGE ANALYSIS
mainClass="com.kuleuven.CoverageAnalysis.MissingTestFinder"
analysisStrategy="FULL"

echo "--- RUNNING GRAPH COVERAGE ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $analysisStrategy"

# METRIC ANALYSIS
mainClass="com.kuleuven.GraphAnalyzer.Main"
metric="FAN_IN_AND_FAN_OUT"

echo "--- RUNNING GRAPH METRIC ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $metric"

# MISSING TEST IDENTIFICATION
mainClass="com.kuleuven.TestMinimization.Main"

minimizationStrategy="STANDARD"

echo "--- RUNNING TEST SUITE MINIMIZATION AND ANALYSIS ---"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName $minimizationStrategy"

# EVALUATION
echo "--- EVALUATING COVERAGE OF MINIMIZED SUITE WITH INDEPENDENT COVERAGE METRIC ---"
echo "Running original test suite"
cd systems/$systemName || exit
mvn test
echo "Creating original independent coverage report"
mvn jacoco:report

mkdir -p $currentDir/data/$systemName/metrics/jacoco-report-original
cp -r target/site/jacoco $currentDir/data/$systemName/metrics/jacoco-report-original
cd "$currentDir" || exit

mainClass="com.kuleuven.TestMinimization.MarkReducedTestSuite"

echo "Marking reduced test suite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

echo "Running reduced test suite"
cd systems/$systemName || exit
mvn clean test -Dgroups=minimized
mvn jacoco:report
mkdir -p $currentDir/data/$systemName/metrics/jacoco-report-minimized
cp -r target/site/jacoco $currentDir/data/$systemName/metrics/jacoco-report-minimized

echo "Unmarking reduced test suite"
cd "$currentDir" || exit
mainClass="com.kuleuven.TestMinimization.UnmarkReducedTestSuite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

echo "Done!"


