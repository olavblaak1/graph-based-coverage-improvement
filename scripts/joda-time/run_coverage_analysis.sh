mainClass="com.kuleuven.CoverageAnalysis.MissingTestFinder"
testInput="systems/joda-time/src/test/java/org/joda/time"
jarPath="systems/joda-time/target/joda-time-2.13.0.jar"
srcDir="systems/joda-time/src/main/java"
graphPath="data/joda-time/graph/graph.json"

analysisStrategy="METHODS"
coverageGraph="data/joda-time/analysis/coverageGraph.json"

mvn exec:java -Dexec.mainClass=$mainClass -Dexec.args="$graphPath $testInput $jarPath $srcDir $analysisStrategy $coverageGraph"