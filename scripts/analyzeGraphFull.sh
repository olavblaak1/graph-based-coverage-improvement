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

# GRAPH EXTRACTION
mainClass="com.kuleuven.GraphExtraction.ExtractGraph"
extractionMethod="FULL_GRAPH"
classPaths="target/classpath.txt"


currentDir=$(pwd)
cd systems/$systemName || exit

OUTPUT_FILE="target/targetjars.txt"

# Build all JARs (main, sources, test-sources, tests)
mvn clean install -DskipTests


# Get all dependency jars
mvn dependency:build-classpath -Dmdep.outputFile=$classPaths

# Get target directory
TARGET_DIR=$(mvn help:evaluate -Dexpression=project.build.directory -q -DforceStdout)

# Find all JAR files in the target directory and format them as a colon-separated list
jarPaths=$(find "$TARGET_DIR" -name "*.jar" | tr '\n' ':' | sed 's/:$//')



# Save the formatted JAR paths to the output file
echo "$jarPaths" > "$OUTPUT_FILE"

cd "$currentDir" || exit
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

# EVALUATION

mvn jacoco:report

