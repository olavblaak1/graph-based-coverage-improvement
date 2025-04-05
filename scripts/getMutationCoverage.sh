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

mainClass="com.kuleuven.TestMinimization.MarkReducedTestSuite"

echo "Marking reduced test suite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

echo "Running reduced test suite"
cd systems/$systemName || exit
mvn -DwithHistory -Dthreads=8 -DincludedGroups=minimized test-compile org.pitest:pitest-maven:mutationCoverage

mkdir -p $currentDir/data/$systemName/metrics/pitest-report-minimized
cp -r target/pit-reports $currentDir/data/$systemName/metrics/pitest-report-minimized

echo "Running full test suite"
mvn -DwithHistory -Dthreads=8 -DincludedGroups=minimized,redundant test-compile org.pitest:pitest-maven:mutationCoverage

mkdir -p $currentDir/data/$systemName/metrics/pitest-report-full
cp -r target/pit-reports/ $currentDir/data/$systemName/metrics/pitest-report-full


echo "Unmarking reduced test suite"
cd "$currentDir" || exit
mainClass="com.kuleuven.TestMinimization.UnmarkReducedTestSuite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

echo "Done!"
