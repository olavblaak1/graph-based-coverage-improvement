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


echo "Marking reduced test suite"
mainClass="com.kuleuven.TestMinimization.MarkReducedTestSuite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

cd systems/$systemName || exit
echo "Running full test suite"
mvn -DwithHistory -DtimeOutFactor=1.5 -Dthreads=8 -DincludedGroups=minimized,redundant clean test org.pitest:pitest-maven:mutationCoverage

mkdir -p $currentDir/data/$systemName/metrics/pitest-report-full
cp -r target/pit-reports/ $currentDir/data/$systemName/metrics/pitest-report-full

echo "Running reduced test suite"
mvn -DwithHistory -DtimeOutFactor=1.5 -Dthreads=8 -DincludedGroups=minimized org.pitest:pitest-maven:mutationCoverage

mkdir -p $currentDir/data/$systemName/metrics/pitest-report-minimized
cp -r target/pit-reports $currentDir/data/$systemName/metrics/pitest-report-minimized


echo "Unmarking reduced test suite"
cd $currentDir || exit
mainClass="com.kuleuven.TestMinimization.UnmarkReducedTestSuite"
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$systemName"

echo "Done!"
