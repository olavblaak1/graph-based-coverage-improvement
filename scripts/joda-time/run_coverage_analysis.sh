mainClass="com.kuleuven.CoverageAnalysis.MissingTestFinder"
testInput="systems/joda-time/src/test/java/org/joda/time"
jarPath="systems/joda-time/target/classpath.txt"
srcDir="systems/joda-time/src/main/java"
graphPath="data/joda-time/graph/graph.json"

analysisStrategy="FULL"
coverageGraph="data/joda-time/analysis/coverageGraph.json"

mvn dependency:build-classpath -Dmdep.outputFile=$jarPath
mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $testInput $jarPath $srcDir $analysisStrategy $coverageGraph"